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

/**
 * @author PursDios
 * @version 1.4
 */
public class Client 
{
	//Instance of the client
	private static Client _Client;
	//number of errors encountered while trying to communicate with the server
	private int ErrorCount=0;
	//whether the client is listening for messages
	private boolean Listening=false;
	//the TCP socket connection with the server
	private Socket _Connection;
	//the local player
	private Player _LocalPlayer;
	//threads used for listening to TCP and UDP packets
	private Thread ListenThread, ListenThreadUDP;
	
	//TCP STUFF
	//the TCP output and input streams
	private ObjectOutputStream _OutputStream;
	private ObjectInputStream _InputStream;
	
	//UDP STUFF
	//https://stackoverflow.com/questions/4252294/sending-objects-across-network-using-udp-in-java
	//the UDP output and input streams
	private ObjectOutputStream _UDPOutputStream;
	private ObjectInputStream _UDPInputStream;
	//the ByteArray streams (required for UDP communication)
	private ByteArrayOutputStream _UDPOutputByteArray;
	private ByteArrayInputStream _UDPInputByteArray;
	//the UDP socket connection with the server
	private DatagramSocket _UDPSocket;
	
	/**
	 * Returns the instance of the client
	 * @return Client instance 
	 */
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
		//private client consutrctor
	}
	
	/**
	 * Attempts to connect to the server with the given portnumber and address and initalises the streams
	 * @param address IP address of the server
	 * @param portnumber the port number we are connecting on
	 * @return if the connection succeded of not
	 */
	public boolean Connect(String address, int portnumber)
	{
		try
		{
			//creates a new socket connection with the server
			_Connection = new Socket(address, portnumber);
			_Connection.setSoTimeout(0);
			
			//Creates Streams
			//if(_OutputStream == null)
				_OutputStream = new ObjectOutputStream(_Connection.getOutputStream());
			//if(_InputStream == null)
			_InputStream = new ObjectInputStream(_Connection.getInputStream());
			
			//Sends the hello message to the server
			Send(new NetworkMessage(MessageType.HELLO));
			//listens for a reply
			NetworkMessage msg = (NetworkMessage)_InputStream.readObject();
			//if it gets a reply checks the message content
			boolean connected = CheckMessage(msg);
			
			if(!connected)
				return false;
				
			//creates the output bytearray stream dn output stream for UDP.
			_UDPOutputByteArray = new ByteArrayOutputStream();
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			//creates the UDP socket for UDP based on the player ID (since all players need to communicate using different ports).
			_UDPSocket = new DatagramSocket(7050 + (_LocalPlayer.GetPlayerID() * 2), _Connection.getInetAddress());
			
			//creates the TCP listen thread, sets listening to true and starts the thread.
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
	
	/**
	 * Listens for incoming TCP packets.
	 */
	public Runnable Listen = () ->
	{
		//listens while there isn't more than 3 errors and the server is listening.
		while(Listening && ErrorCount < 3)
		{
			try
			{
				//listens for message....
				NetworkMessage m = (NetworkMessage)_InputStream.readObject();
				//if it recieves a message calls checkmessage.
				CheckMessage(m);
			}
			catch(Exception e)
			{
				ErrorCount++;
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Listens for UDP packets
	 */
	public Runnable ListenUDP = () ->
	{
		//while there is less than 3 errors and the connection isn't closed and the client is listening.
		while(ErrorCount < 3 && !_Connection.isClosed() && Listening)
		{
			//create a new buffer
			byte[] buffer = new byte[1024];
			//create a new datagram packet using the buffer
			DatagramPacket p = new DatagramPacket(buffer, buffer.length);
			try
			{
				//listen for incoming packets and store them into the datagram packet
				_UDPSocket.receive(p);
				//create a new input byte array with the content of the datagram packet
				_UDPInputByteArray = new ByteArrayInputStream(p.getData());
				//create a new input stream based on the contents of the inputbyte array
				_UDPInputStream = new ObjectInputStream(_UDPInputByteArray);
				//create a network message by reading an object from the udpinputstream
				NetworkMessage msg = (NetworkMessage)_UDPInputStream.readObject();
				//close the inputstream
				_UDPInputStream.close();
				//check the contents of the newly created network message
				CheckMessage(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ErrorCount++;
			}
		}
	};
	
	/**
	 * checks the content of a network message
	 * @param msg a network message
	 */
	private boolean CheckMessage(NetworkMessage msg)
	{
		//if the message is a connection accept message
		if(msg.GetMessageType() == MessageType.CONNECTION_ACCEPT)
		{
			//cast the message appropriately 
			ConnectionAccept ca = (ConnectionAccept)msg;
			//get the name of the map from the message
			String mapname = ca.GetMapName();
			//get the location of the map from the message
			String maploc = ca.GetMapLocation();
			//set the local map to be equal to the contents of the message
			Controller.getInstance().SetMap(maploc, mapname);
			
			//creates a new player with the id from the message
			Player p = new Player(ca.GetPlayerID(), "Player " + ca.GetPlayerID());
			//sets the default car location.
			p.GetPlayerCar().setImageLocation("Cars/car1.png");
			//creates a new vehiclelabel
			p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
			//sets the ready status of the player to be true
			p.SetReady(false);
			
			//sets the local player to be the same as the player just created.
			_LocalPlayer = p;
			
		}
		else if(msg.GetMessageType() == MessageType.CONNECTION_REJECT)
		{
			System.out.println("PLAYER REJECTED");
			return false;
		}
		//if the message is a current players message
		else if(msg.GetMessageType() == MessageType.CURRENT_PLAYERS)
		{
			//casts the message
			CurrentPlayers cp = (CurrentPlayers)msg;
			//gets the arraylist of ids
			ArrayList<Integer> ids = cp.GetIds();
			//gets the arraylist of names
			ArrayList<String> names = cp.GetNames();
			//gets the carlocations
			ArrayList<String> carlocs = cp.GetCarLocs();
			//gets the ready status'
			ArrayList<Boolean> ready = cp.GetReady();
			
			//for each id (player)
			for(int i=0;i<cp.GetIds().size(); i++)
			{
				//create a new player with the id and name from the lists
				Player p = new Player(ids.get(i), names.get(i));
				//set the car location (from the list)
				p.GetPlayerCar().setImageLocation(carlocs.get(i));
				//set the ready status (from the list)
				p.SetReady(ready.get(i));
				//creates a new default vehiclelabel (they are updated at the game start)
				p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
				//add the player to the local list.
				Controller.getInstance().AddPlayer(p);
			}
		}
		//if the message is a new player connected message
		else if(msg.GetMessageType() == MessageType.NEW_PLAYER_CONNECTED)
		{
			//casts the message
			NewPlayerConnected npc = (NewPlayerConnected)msg;
			
			//creates a new player with the default values
			Player p = new Player(npc.GetPlayerID(),"Player " + npc.GetPlayerID());
			p.GetPlayerCar().setImageLocation("Cars/car1.png");
			p.GetPlayerCar().SetVehicleLabel(new VehicleLabel(p));
			p.SetReady(false);
			
			//adds them to the local list.
			Controller.getInstance().AddPlayer(p);
		}
		//if the message is a map update message
		else if(msg.GetMessageType() == MessageType.MAP_UPDATE)
		{
			//casts the message
			MapUpdate mu = (MapUpdate)msg;
			//gets the map name
			String name = mu.GetMapName();
			//gets the map location
			String map = mu.GetMapLocation();
			//updates the local map.
			Controller.getInstance().SetMap(map, name);
		}
		//if the message is a player car update message
		else if(msg.GetMessageType() == MessageType.PLAYER_CAR_UPDATE)
		{
			//casts the message
			PlayerCarUpdate pcu = (PlayerCarUpdate)msg;
			//updates the players car
			Controller.getInstance().UpdatePlayerCar(pcu.GetPlayerID(), pcu.GetCarLocation());
		}
		//if the message is a player name update message
		else if(msg.GetMessageType() == MessageType.PLAYER_NAME_UPDATE)
		{
			//casts the message
			PlayerNameUpdate pnu = (PlayerNameUpdate)msg;
			//updates the player name
			Controller.getInstance().UpdatePlayerName(pnu.GetPlayerID(), pnu.GetPlayerName());
		}
		//if the message is a player ready update message
		else if(msg.GetMessageType() == MessageType.PLAYER_READY_UPDATE)
		{
			//casts the message
			PlayerReadyUpdate pru = (PlayerReadyUpdate)msg;
			//updates the players ready status
			Controller.getInstance().UpdatePlayerReady(pru.GetPlayerID(), pru.GetReady());
		}
		//if the message is a chat message message.
		else if(msg.GetMessageType() == MessageType.CHAT_MESSAGE)
		{
			//casts the message
			ChatMessage cm = (ChatMessage)msg;
			//updates the chat with the new message
			Controller.getInstance().UpdateChat(cm.GetPlayerID(), cm.GetMessage());
		}
		//if the message is a start game message
		else if(msg.GetMessageType() == MessageType.START_GAME)
		{
			//creates and starts the UDP listening thread
			ListenThreadUDP = new Thread(ListenUDP);
			ListenThreadUDP.start();
			//calls the controller to load the multiplayer game screen
			Controller.getInstance().StartMultiPlayerGame();
		}
		//if the message is a player position update message (sent via UDP) 
		else if(msg.GetMessageType() == MessageType.PLAYER_POS_UPDATE)
		{
			//casts the message
			VehiclePositionUpdate vpu = (VehiclePositionUpdate)msg;
			
			//gets the x and y positions from the message
			int x = vpu.GetX();
			int y = vpu.GetY();
			
			//searches the list of players to update their positions
			for(Player p : Controller.getInstance().GetPlayerList())
			{
				if(p.GetPlayerID() == vpu.GetPlayerID())
				{
					p.GetPlayerCar().GetVehicleLabel().setLocation(x, y);
					p.GetPlayerCar().setAngle(vpu.GetAngle());
				}
			}
		}
		//if the message is a end game message
		else if(msg.GetMessageType() == MessageType.END_GAME)
		{
			//casts the message
			EndGame eg = (EndGame)msg;
			
			//gets the name of the winning player
			String Winner = eg.GetWinningPlayer();
			//calls the end of the game.
			Controller.getInstance().FinishGame(Winner);
		}
		return true;
	}
	
	/**
	 * sends a network message to the server (TCP)
	 * @param msg the network message being sent to the server.
	 * @return true or false based on success.
	 */
	private synchronized boolean Send(NetworkMessage msg)
	{
		try
		{
			//calls the TCP outputstream to send the message
			_OutputStream.writeObject(msg);
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * sends a network message to the server (UDP)
	 * @param msg the message being sent to the server.
	 */
	private synchronized void SendOutUDP(VehiclePositionUpdate msg)
	{
		try
		{
			//creates a new outputbytearray
			_UDPOutputByteArray = new ByteArrayOutputStream();
			//creates the outpustream based on the outputbyte array
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			
			//writes the message object into the bytearray
			_UDPOutputStream.writeObject(msg);
			//cleans the outputstream
			_UDPOutputStream.flush();
			
			//creates a buffer with the contents of the outputbytearray
			byte[] buffer = _UDPOutputByteArray.toByteArray();
			//create a datagram packet with the contents of the buffer and the IP address of the server and the port from the socket.
			DatagramPacket p = new DatagramPacket(buffer, buffer.length, _Connection.getInetAddress(), _UDPSocket.getLocalPort() -1);
			//sends the udp message
			_UDPSocket.send(p);
		}
		catch(IOException e)
		{
			ErrorCount++;
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the local player
	 * @return the local player
	 */
	public Player GetPlayer()
	{
		return _LocalPlayer;
	}
	
	/**
	 * sets the local player
	 * @param p a player
	 */
	public void SetFinalLocalPlayer(Player p)
	{
		_LocalPlayer = p;
	}
	
	/**
	 * sets the ready status of the player
	 * @param ready ready status
	 */
	public void SetReady(boolean ready)
	{
		//sets the local client player as ready
		_LocalPlayer.SetReady(ready);
		
		//creates a player ready update message
		PlayerReadyUpdate pru = new PlayerReadyUpdate(MessageType.PLAYER_READY_UPDATE, _LocalPlayer.GetPlayerID(), _LocalPlayer.GetReady());
		
		try 
		{
			//sends it to the server.
			_OutputStream.writeObject(pru);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	/**
	 * sends out a map update message
	 * @param mapname the name of the map
	 * @param maplocation the location of the map
	 */
	public void UpdateMap(String mapname, String maplocation) 
	{
		//creates the map update message
		MapUpdate msg = new MapUpdate(MessageType.MAP_UPDATE, mapname, maplocation);
		try 
		{
			//sends it to the server
			_OutputStream.writeObject(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	/**
	 * updates the car location
	 * @param x the x cord of the car
	 * @param y the y cord of the car
	 * @param angle the angle of the car
	 */
	public void UpdateCarLocation(int x, int y, double angle)
	{
		//sets the local players car position to the x and y cords
		_LocalPlayer.GetPlayerCar().GetVehicleLabel().setLocation(x, y);
		//creates the vehicleupdate message
		VehiclePositionUpdate vpu = new VehiclePositionUpdate(MessageType.PLAYER_POS_UPDATE, _LocalPlayer.GetPlayerID(), x, y, angle); 
		//sends the vehicleupdate message (UDP)
		SendOutUDP(vpu);
	}

	/**
	 * Sends a playercar update message
	 * @param path the new path of the car
	 */
	public void UpdatePlayerCar(String path) 
	{
		//updates the local players car location
		_LocalPlayer.GetPlayerCar().setImageLocation(path);
		//creates a new player car update message
		PlayerCarUpdate pcu = new PlayerCarUpdate(MessageType.PLAYER_CAR_UPDATE, _LocalPlayer.GetPlayerID(), path);
		try 
		{
			//sends the message to the server
			_OutputStream.writeObject(pcu);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	/**
	 * sends a player name update message
	 * @param Name the new player name
	 */
	public void UpdatePlayerName(String Name)
	{
		//sets the local players name
		_LocalPlayer.SetPlayerName(Name);
		//creates the player name update message
		PlayerNameUpdate pnu = new PlayerNameUpdate(MessageType.PLAYER_NAME_UPDATE, _LocalPlayer.GetPlayerID(), Name);
		
		try 
		{
			//sends the player name update message
			_OutputStream.writeObject(pnu);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	/**
	 * Sends a chat message 
	 * @param playerid the id of the player sending the message
	 * @param message the message being sent
	 */
	public void SendChatMessage(int playerid, String message)
	{
		//creates the chat message.
		ChatMessage cm = new ChatMessage(MessageType.CHAT_MESSAGE,playerid, message);
		try 
		{
			//sends the message to the server
			_OutputStream.writeObject(cm);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	/**
	 * sends a player wins message
	 * @param name the name of the winning player
	 */
	public void LocalPlayerWins(String name) 
	{
		//creates an end game message
		EndGame eg = new EndGame(MessageType.END_GAME, name);
		try 
		{
			//sends the message to the server
			_OutputStream.writeObject(eg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}

	/**
	 * disconnects the client from the server
	 */
	public void Disconnect() 
	{
		//stops the player from listening to packets
		Listening = false;
		//creates a disconnect message
		NetworkMessage msg = new NetworkMessage(MessageType.DISCONNECT);
		try 
		{
			//sends it to the server
			_OutputStream.writeObject(msg);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
