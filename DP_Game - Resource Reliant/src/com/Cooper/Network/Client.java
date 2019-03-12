package com.Cooper.Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.Cooper.Control.Controller;
import com.Cooper.Game.Player;
import com.Cooper.Network.Message.MessageTypes.MessageType;
import com.Cooper.Network.Message.Messages.*;
import com.Cooper.UI.VehicleLabel;

public class Client 
{
	private static Client _Client;
	private int ErrorCount=0;
	private boolean Listening=false;
	private Socket _Connection;
	private Player _LocalPlayer;
	private Thread ListenThread, ListenThreadUDP;
	
	//TCP STUFF
	private ObjectOutputStream _OutputStream;
	private ObjectInputStream _InputStream;
	
	//UDP STUFF
	//https://stackoverflow.com/questions/4252294/sending-objects-across-network-using-udp-in-java
	private ObjectOutputStream _UDPOutputStream;
	private ObjectInputStream _UDPInputStream;
	private ByteArrayOutputStream _UDPOutputByteArray;
	private ByteArrayInputStream _UDPInputByteArray;
	private DatagramSocket _UDPSocket;
	
	public static Client GetInstance()
	{
		if(_Client == null)
		{
			_Client = new Client();
		}
		return _Client;
	}
	
	private Client()
	{
		
	}
	
	public boolean Connect(String address, int portnumber)
	{
		try
		{
			_Connection = new Socket(address, portnumber);
			_Connection.setSoTimeout(0);
			
			//Creates Streams
			if(_OutputStream == null)
				_OutputStream = new ObjectOutputStream(_Connection.getOutputStream());
			if(_InputStream == null)
			_InputStream = new ObjectInputStream(_Connection.getInputStream());
			
			Send(new NetworkMessage(MessageType.HELLO));
			ConnectionAccept msg = (ConnectionAccept)_InputStream.readObject();
			CheckMessage(msg);
			
			
			_UDPOutputByteArray = new ByteArrayOutputStream();
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			
			_UDPSocket = new DatagramSocket(7050 + (_LocalPlayer.GetPlayerID() * 2), _Connection.getInetAddress());
			
			ListenThread = new Thread(Listen);
			Listening = true;
			ListenThread.start();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ErrorCount++;
		}
		return false;
	}
	
	public Runnable Listen = () ->
	{
		while(Listening && ErrorCount < 3)
		{
			try
			{
				NetworkMessage m = (NetworkMessage)_InputStream.readObject();
				CheckMessage(m);
			}
			catch(Exception e)
			{
				ErrorCount++;
				e.printStackTrace();
			}
		}
	};
	
	public Runnable ListenUDP = () ->
	{
		while(ErrorCount < 5 && !_Connection.isClosed())
		{
			byte[] buffer = new byte[1024];
			
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try
			{
				_UDPSocket.receive(p);
				_UDPInputByteArray = new ByteArrayInputStream(p.getData());
				_UDPInputStream = new ObjectInputStream(_UDPInputByteArray);
				
				NetworkMessage msg = (NetworkMessage)_UDPInputStream.readObject();
				_UDPInputStream.close();
				CheckMessage(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ErrorCount++;
			}
		}
	};
	
	private void CheckMessage(NetworkMessage msg)
	{
		if(msg.GetMessageType() == MessageType.CONNECTION_ACCEPT)
		{
			ConnectionAccept ca = (ConnectionAccept)msg;
			String mapname = ca.GetMapName();
			String maploc = ca.GetMapLocation();
			Controller.getInstance().SetMap(maploc, mapname);
			
			Player p = new Player(ca.GetPlayerID(), "Player " + ca.GetPlayerID());
			p.GetPlayerCar().setImageLocation("resources/Cars/NotAudi/audi0.png");
			p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
			p.SetReady(false);
			
			_LocalPlayer = p;
		}
		
		else if(msg.GetMessageType() == MessageType.CURRENT_PLAYERS)
		{
			CurrentPlayers cp = (CurrentPlayers)msg;
			ArrayList<Integer> ids = cp.GetIds();
			ArrayList<String> names = cp.GetNames();
			ArrayList<String> carlocs = cp.GetCarLocs();
			ArrayList<Boolean> ready = cp.GetReady();
			
			for(int i=0;i<cp.GetIds().size(); i++)
			{
				Player p = new Player(ids.get(i), names.get(i));
				p.GetPlayerCar().setImageLocation(carlocs.get(i));
				p.SetReady(ready.get(i));
				p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
				Controller.getInstance().AddPlayer(p);
			}
		}
		
		else if(msg.GetMessageType() == MessageType.NEW_PLAYER_CONNECTED)
		{
			NewPlayerConnected npc = (NewPlayerConnected)msg;
			Player p = new Player(npc.GetPlayerID(),"Player " + npc.GetPlayerID());
			p.GetPlayerCar().setImageLocation("resources/Cars/NotAudi/audi0.png");
			p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
			p.SetReady(false);
			
			Controller.getInstance().AddPlayer(p);
		}
		
		else if(msg.GetMessageType() == MessageType.MAP_UPDATE)
		{
			MapUpdate mu = (MapUpdate)msg;
			String name = mu.GetMapName();
			String map = mu.GetMapLocation();
			Controller.getInstance().SetMap(map, name);
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_CAR_UPDATE)
		{
			PlayerCarUpdate pcu = (PlayerCarUpdate)msg;
			
			Controller.getInstance().UpdatePlayerCar(pcu.GetPlayerID(), pcu.GetCarLocation());
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_NAME_UPDATE)
		{
			PlayerNameUpdate pnu = (PlayerNameUpdate)msg;
			
			Controller.getInstance().UpdatePlayerName(pnu.GetPlayerID(), pnu.GetPlayerName());
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_READY_UPDATE)
		{
			PlayerReadyUpdate pru = (PlayerReadyUpdate)msg;
			
			Controller.getInstance().UpdatePlayerReady(pru.GetPlayerID(), pru.GetReady());
		}
		
		else if(msg.GetMessageType() == MessageType.CHAT_MESSAGE)
		{
			ChatMessage cm = (ChatMessage)msg;
			
			Controller.getInstance().UpdateChat(cm.GetPlayerID(), cm.GetMessage());
		}
		
		else if(msg.GetMessageType() == MessageType.START_GAME)
		{
			ListenThreadUDP = new Thread(ListenUDP);
			ListenThreadUDP.start();
			Controller.getInstance().StartMultiPlayerGame();
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_POS_UPDATE)
		{
			VehiclePositionUpdate vpu = (VehiclePositionUpdate)msg;
			
			int x = vpu.GetX();
			int y = vpu.GetY();
			
			for(Player p : Controller.getInstance().GetPlayerList())
			{
				if(p.GetPlayerID() == vpu.GetPlayerID())
				{
					p.GetPlayerCar().GetVehicleLabel().setLocation(x, y);
					p.GetPlayerCar().setAngle(vpu.GetAngle());
				}
			}
		}
		else if(msg.GetMessageType() == MessageType.END_GAME)
		{
			EndGame eg = (EndGame)msg;
			
			String Winner = eg.GetWinningPlayer();
			Controller.getInstance().FinishGame(Winner);
		}
	}
	
	private synchronized boolean Send(NetworkMessage msg)
	{
		try
		{
			_OutputStream.writeObject(msg);
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private synchronized void SendOutUDP(VehiclePositionUpdate msg)
	{
		try
		{
			_UDPOutputByteArray = new ByteArrayOutputStream();
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			
			_UDPOutputStream.writeObject(msg);
			_UDPOutputStream.flush();
			
			byte[] buffer = _UDPOutputByteArray.toByteArray();
			DatagramPacket p = new DatagramPacket(buffer, buffer.length, _Connection.getInetAddress(), _UDPSocket.getLocalPort() -1);
			_UDPSocket.send(p);
		}
		catch(IOException e)
		{
			ErrorCount++;
			e.printStackTrace();
		}
	}
	
	public Player GetPlayer()
	{
		return _LocalPlayer;
	}
	
	public void SetFinalLocalPlayer(Player p)
	{
		_LocalPlayer = p;
	}
	
	public void SetReady(boolean ready)
	{
		_LocalPlayer.SetReady(ready);
		
		PlayerReadyUpdate pru = new PlayerReadyUpdate(MessageType.PLAYER_READY_UPDATE, _LocalPlayer.GetPlayerID(), _LocalPlayer.GetReady());
		
		try 
		{
			_OutputStream.writeObject(pru);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	public void UpdateMap(String mapname, String maplocation) 
	{
		MapUpdate msg = new MapUpdate(MessageType.MAP_UPDATE, mapname, maplocation);
		try 
		{
			_OutputStream.writeObject(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	public void UpdateCarLocation(int x, int y, double angle)
	{
		_LocalPlayer.GetPlayerCar().GetVehicleLabel().setLocation(x, y);
		VehiclePositionUpdate vpu = new VehiclePositionUpdate(MessageType.PLAYER_POS_UPDATE, _LocalPlayer.GetPlayerID(), x, y, angle); 
		SendOutUDP(vpu);
	}

	public void UpdatePlayerCar(String path) 
	{
		_LocalPlayer.GetPlayerCar().setImageLocation(path);
		PlayerCarUpdate pcu = new PlayerCarUpdate(MessageType.PLAYER_CAR_UPDATE, _LocalPlayer.GetPlayerID(), path);
		try 
		{
			_OutputStream.writeObject(pcu);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	public void UpdatePlayerName(String Name)
	{
		_LocalPlayer.SetPlayerName(Name);
		PlayerNameUpdate pnu = new PlayerNameUpdate(MessageType.PLAYER_NAME_UPDATE, _LocalPlayer.GetPlayerID(), Name);
		
		try 
		{
			_OutputStream.writeObject(pnu);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	public void SendChatMessage(int playerid, String message)
	{
		ChatMessage cm = new ChatMessage(MessageType.CHAT_MESSAGE,playerid, message);
		try 
		{
			_OutputStream.writeObject(cm);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	public void LocalPlayerWins(String name) 
	{
		EndGame eg = new EndGame(MessageType.END_GAME, name);
		try 
		{
			_OutputStream.writeObject(eg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	public void Disconnect() 
	{
		NetworkMessage msg = new NetworkMessage(MessageType.DISCONNECT);
		try 
		{
			_OutputStream.writeObject(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
