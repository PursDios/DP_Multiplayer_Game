package com.Cooper.Network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.Cooper.Game.Player;
import com.Cooper.Network.Message.MessageTypes.*;
import com.Cooper.Network.Message.Messages.*;
import com.Cooper.UI.VehicleLabel;

public class ServerSession implements Runnable
{
	Player _ClientPlayer;
	InetAddress _ClientIP;
	int ErrorCount=0;
	
	//TCP STUFF
	ObjectOutputStream _OutputStream;
	ObjectInputStream _InputStream;
	Socket _ClientSocket;
	
	//UDP STUFF
	private Thread UDPListenThread;
	private ObjectOutputStream _UDPOutputStream;
	private ObjectInputStream _UDPInputStream;
	private ByteArrayOutputStream _UDPOutputByteArray;
	private ByteArrayInputStream _UDPInputByteArray;
	private DatagramSocket _UDPSocket;
	
	public ServerSession(Socket NewClient)
	{
		_ClientSocket = NewClient;
		int i = Server.getInstance().GetClientSessions().size() + 1;
		_ClientPlayer = new Player(i, "Player " + i);
		_ClientPlayer.GetPlayerCar().SetVehicleLabel(new VehicleLabel(_ClientPlayer));
		_ClientIP = NewClient.getInetAddress();
	}
	
	public void OpenConnection()
	{
		//Set Player ID here?
		try 
		{
			//TCP 
			_OutputStream = new ObjectOutputStream(_ClientSocket.getOutputStream());
			_InputStream = new ObjectInputStream(_ClientSocket.getInputStream());
			
			//UDP
			_UDPOutputByteArray = new ByteArrayOutputStream();
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			
			int i = _ClientPlayer.GetPlayerID() *2;
			_UDPSocket = new DatagramSocket(7050 + i -1, _ClientSocket.getInetAddress());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	@Override
	public void run() 
	{
		if(_OutputStream == null || _InputStream == null)
		{
			//this is here as a precaution but should never actually be run.
			System.out.println("Something has gone awfully wrong.\nThe Input and Output streams have not be created!");
			return;
		}
		
		NetworkMessage msg;
		
		while(!_ClientSocket.isClosed() && ErrorCount < 3)
		{
			try
			{
				msg = (NetworkMessage)_InputStream.readObject();
				MessageCheck(msg);
			}
			catch(Exception e)
			{
				ErrorCount++;
				e.printStackTrace();
			}
		}
		DisconnectClient();
	}
	
	private void DisconnectClient()
	{
		Server.getInstance().RemoveClient(this);
	}
	
	public Player GetPlayer()
	{
		return _ClientPlayer;
	}
	
	private void MessageCheck(NetworkMessage msg)
	{
		if(msg.GetMessageType() == MessageType.HELLO)
		{
			try 
			{
				//the size will be the number of the player id (because the size counts 0 as 1
				int i = Server.getInstance().GetClientSessions().size();
				String mapname = Server.getInstance().GetMap().GetName();
				String maploc = Server.getInstance().GetMap().getMapLocation();
				
				//sends the map and the new players id to them
				_OutputStream.writeObject(new ConnectionAccept(MessageType.CONNECTION_ACCEPT, i, mapname, maploc));
				
				//Updates other players to the new player
				NewPlayerConnected npc = new NewPlayerConnected(MessageType.NEW_PLAYER_CONNECTED, i);
				Server.getInstance().Broadcast(npc, this);
				
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<String> carlocs = new ArrayList<String>();
				ArrayList<Boolean> ready = new ArrayList<Boolean>();
				
				for(ServerSession s : Server.getInstance().GetClientSessions())
				{
					ids.add(s.GetPlayer().GetPlayerID());
					names.add(s.GetPlayer().GetPlayerName());
					carlocs.add(s.GetPlayer().GetPlayerCar().getImageLocation());
					ready.add(s.GetPlayer().GetReady());
				}
				
				CurrentPlayers cp = new CurrentPlayers(MessageType.CURRENT_PLAYERS, ids, names, carlocs, ready);
				_OutputStream.writeObject(cp);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				ErrorCount++;
			
			}
		}
		else if(msg.GetMessageType() == MessageType.MAP_UPDATE)
		{
			MapUpdate mu = (MapUpdate)msg;
			
			Server.getInstance().SetMap(mu.GetMapName(), mu.GetMapLocation());
			Server.getInstance().Broadcast(mu, this);
		}
		else if(msg.GetMessageType() == MessageType.PLAYER_READY_UPDATE)
		{
			PlayerReadyUpdate pru = (PlayerReadyUpdate)msg;
			_ClientPlayer.SetReady(pru.GetReady());
			Server.getInstance().Broadcast(pru, this);
			Server.getInstance().CheckForStart();
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_CAR_UPDATE)
		{
			PlayerCarUpdate pcu = (PlayerCarUpdate)msg;
			_ClientPlayer.GetPlayerCar().setImageLocation(pcu.GetCarLocation());
			Server.getInstance().Broadcast(pcu, this);
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_NAME_UPDATE)
		{
			PlayerNameUpdate pnu = (PlayerNameUpdate)msg;
			_ClientPlayer.SetPlayerName(pnu.GetPlayerName());
			Server.getInstance().Broadcast(pnu, this);
		}
		
		else if(msg.GetMessageType() == MessageType.CHAT_MESSAGE)
		{
			ChatMessage cm = (ChatMessage)msg;
			Server.getInstance().Broadcast(cm, this);
		}
		
		else if(msg.GetMessageType() == MessageType.PLAYER_POS_UPDATE)
		{
			VehiclePositionUpdate ppu = (VehiclePositionUpdate)msg;
			_ClientPlayer.GetPlayerCar().GetVehicleLabel().setLocation(ppu.GetX(), ppu.GetY());
			Server.getInstance().BroadcastUDP(msg, this);
		}
		
		else if(msg.GetMessageType() == MessageType.END_GAME)
		{
			Server.getInstance().Broadcast(msg, this);
		}
		else if(msg.GetMessageType() == MessageType.DISCONNECT)
		{
			DisconnectClient();
		}
	}
	
	public void SendMessage(NetworkMessage msg)
	{
		try
		{
			_OutputStream.writeObject(msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();    
			ErrorCount++;
		}
	}
	public synchronized void SendMessageUDP(NetworkMessage msg)
	{
		if(!_ClientSocket.isClosed())
		{
			try
			{
				_UDPOutputByteArray = new ByteArrayOutputStream();
				_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
				
				_UDPOutputStream.writeObject(msg);
				_UDPOutputStream.flush();
				
				byte[] buffer = _UDPOutputByteArray.toByteArray();
				
				DatagramPacket p = new DatagramPacket(buffer, buffer.length, _ClientSocket.getInetAddress(), _UDPSocket.getLocalPort() + 1);
				_UDPSocket.send(p);
				
				_UDPOutputByteArray.reset();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ErrorCount++;
			}
		}
	}
	
	public void StartUDPListen()
	{
		UDPListenThread = new Thread(UDPListen);
		UDPListenThread.start();
	}
	
	
	private Runnable UDPListen = () ->
	{	
		while(ErrorCount < 3 && !_ClientSocket.isClosed())
		{
			//required for the datagram packet
			byte[] buf = new byte[1024];
			//creates the datagram packet
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			try
			{
				//recieves the data into a datagram packet
				_UDPSocket.receive(dp);
				 _UDPInputByteArray = new ByteArrayInputStream(dp.getData());
				 
				 _UDPInputStream = new ObjectInputStream(_UDPInputByteArray);
				 NetworkMessage msg = (NetworkMessage) _UDPInputStream.readObject();
				 _UDPInputStream.close();
				 
				 MessageCheck(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ErrorCount++;
			}
		}
	};
}
