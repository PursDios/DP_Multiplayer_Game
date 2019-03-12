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

/**
 * @author PursDios
 * @Version 1.2
 */
public class ServerSession implements Runnable
{
	//the local player
	Player _ClientPlayer;
	//the clients IP
	InetAddress _ClientIP;
	//the number of errors encountered
	int ErrorCount=0;
	
	//TCP STUFF
	//input and output streams (for sending and recieving messages
	ObjectOutputStream _OutputStream;
	ObjectInputStream _InputStream;
	//the socket information for sending/receieving from the client.
	Socket _ClientSocket;
	
	//UDP STUFF
	//the thread used to listen for incoming UDP packets (TCP is the run method)
	private Thread UDPListenThread;
	//the UDP input and outputstreams
	private ObjectOutputStream _UDPOutputStream;
	private ObjectInputStream _UDPInputStream;
	//the udp input and output byte arrays (required for UDP communication
	private ByteArrayOutputStream _UDPOutputByteArray;
	private ByteArrayInputStream _UDPInputByteArray;
	//the socket information for sending and reciving udp packets.
	private DatagramSocket _UDPSocket;
	
	/**
	 * Constructor for a ServerSession
	 * @param NewClient the socket information for a new client
	 */
	public ServerSession(Socket NewClient)
	{
		//sets the local clientsocket fo the socket information sent
		_ClientSocket = NewClient;
		//calcualtes the new players id based on the number of players already playing.
		int i = Server.getInstance().GetClientSessions().size() + 1;
		//creates a new clientplayer based on that id
		_ClientPlayer = new Player(i, "Player " + i);
		//creates a new vehiclelabel
		_ClientPlayer.GetPlayerCar().SetVehicleLabel(new VehicleLabel(_ClientPlayer));
		//sets the clients IP address.
		_ClientIP = NewClient.getInetAddress();
	}
	
	/**
	 * Initial configuration for connectivity
	 */
	public void OpenConnection()
	{
		//Set Player ID here?
		try 
		{
			//TCP 
			//creates new output and inputstreams
			_OutputStream = new ObjectOutputStream(_ClientSocket.getOutputStream());
			_InputStream = new ObjectInputStream(_ClientSocket.getInputStream());
			
			//UDP
			//creates new outputbytearrays and outputstreams
			_UDPOutputByteArray = new ByteArrayOutputStream();
			_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
			
			//calcualtes the udp socket the player will be using based on the players id
			int i = _ClientPlayer.GetPlayerID() *2;
			//sets the udp socket information
			_UDPSocket = new DatagramSocket(7050 + i -1, _ClientSocket.getInetAddress());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			ErrorCount++;
		}
	}
	
	/**
	 * Listens for TCP packets 
	 */
	@Override
	public void run() 
	{
		//if the streams are null (which they should never be)
		if(_OutputStream == null || _InputStream == null)
		{
			//this is here as a precaution but should never actually be run.
			System.out.println("Something has gone awfully wrong.\nThe Input and Output streams have not be created!");
			return;
		}
		//stores the network message
		NetworkMessage msg;
		
		while(!_ClientSocket.isClosed() && ErrorCount < 3)
		{
			try
			{
				//sets the message content to be equal to the network message object sent.
				msg = (NetworkMessage)_InputStream.readObject();
				//checks the content of the message.
				MessageCheck(msg);
			}
			catch(Exception e)
			{
				ErrorCount++;
				e.printStackTrace();
			}
		}
		
		//if the socket is closed or there are more than 3 errors disconnect the client
		DisconnectClient();
	}
	
	/**
	 * disconnects the client
	 */
	private void DisconnectClient()
	{
		//tells the client to remove this session
		Server.getInstance().RemoveClient(this);
	}
	/**
	 * returns the client player
	 * @return the client player
	 */
	public Player GetPlayer()
	{
		//returns the client player
		return _ClientPlayer;
	}
	
	/**
	 * Used to check the content of various network messages
	 * @param msg a network message.
	 */
	private void MessageCheck(NetworkMessage msg)
	{
		//if the message is type hello.
		if(msg.GetMessageType() == MessageType.HELLO)
		{
			try 
			{
				//the size will be the number of the player id (because the size counts 0 as 1
				int i = Server.getInstance().GetClientSessions().size();
				//gets the map currently in use name
				String mapname = Server.getInstance().GetMap().GetName();
				//gets the maps location
				String maploc = Server.getInstance().GetMap().getMapLocation();
				
				//sends the map and the new players id to them
				_OutputStream.writeObject(new ConnectionAccept(MessageType.CONNECTION_ACCEPT, i, mapname, maploc));
				
				//Updates other players to the new player
				NewPlayerConnected npc = new NewPlayerConnected(MessageType.NEW_PLAYER_CONNECTED, i);
				//broadcasts that a new player has connected to all other players.
				Server.getInstance().Broadcast(npc, this);
				
				//arraylists storing all the current players details
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ArrayList<String> names = new ArrayList<String>();
				ArrayList<String> carlocs = new ArrayList<String>();
				ArrayList<Boolean> ready = new ArrayList<Boolean>();
				
				//for each session.
				for(ServerSession s : Server.getInstance().GetClientSessions())
				{
					//sets all the current players details.
					ids.add(s.GetPlayer().GetPlayerID());
					names.add(s.GetPlayer().GetPlayerName());
					carlocs.add(s.GetPlayer().GetPlayerCar().getImageLocation());
					ready.add(s.GetPlayer().GetReady());
				}
				
				//creates a new currentplayers network message
				CurrentPlayers cp = new CurrentPlayers(MessageType.CURRENT_PLAYERS, ids, names, carlocs, ready);
				//sends the current players message.
				_OutputStream.writeObject(cp);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				ErrorCount++;
			
			}
		}
		//if the message is a map update message
		else if(msg.GetMessageType() == MessageType.MAP_UPDATE)
		{
			//casts the message
			MapUpdate mu = (MapUpdate)msg;
			
			//updates the server map
			Server.getInstance().SetMap(mu.GetMapName(), mu.GetMapLocation());
			//broadcasts the message to all of the other players.
			Server.getInstance().Broadcast(mu, this);
		}
		//the message is a player ready update.
		else if(msg.GetMessageType() == MessageType.PLAYER_READY_UPDATE)
		{
			//casts the message
			PlayerReadyUpdate pru = (PlayerReadyUpdate)msg;
			//sets updates the client players ready status
			_ClientPlayer.SetReady(pru.GetReady());
			//broadcasts the change to all other players
			Server.getInstance().Broadcast(pru, this);
			//checks to start the game
			Server.getInstance().CheckForStart();
		}
		//if the message is a player car update.
		else if(msg.GetMessageType() == MessageType.PLAYER_CAR_UPDATE)
		{
			//casts the message as a player car update message
			PlayerCarUpdate pcu = (PlayerCarUpdate)msg;
			//sets the client players car image location
			_ClientPlayer.GetPlayerCar().setImageLocation(pcu.GetCarLocation());
			//broadcasts the playercar update message to all the other players
			Server.getInstance().Broadcast(pcu, this);
		}
		//if the message is a player name update message
		else if(msg.GetMessageType() == MessageType.PLAYER_NAME_UPDATE)
		{
			//cast the message 
			PlayerNameUpdate pnu = (PlayerNameUpdate)msg;
			//sets the client players name
			_ClientPlayer.SetPlayerName(pnu.GetPlayerName());
			//broadcasts the update to all other players
			Server.getInstance().Broadcast(pnu, this);
		}
		//if the message is a chat message
		else if(msg.GetMessageType() == MessageType.CHAT_MESSAGE)
		{
			//cast the message as a chat message
			ChatMessage cm = (ChatMessage)msg;
			//broadcasts the chat message to all the other players.
			Server.getInstance().Broadcast(cm, this);
		}
		//if the message is a player position update message
		else if(msg.GetMessageType() == MessageType.PLAYER_POS_UPDATE)
		{
			//casts the message
			VehiclePositionUpdate ppu = (VehiclePositionUpdate)msg;
			//updates the client players location
			_ClientPlayer.GetPlayerCar().GetVehicleLabel().setLocation(ppu.GetX(), ppu.GetY());
			//broadcasts the update to all the other players
			Server.getInstance().BroadcastUDP(msg, this);
		}
		//if the message type is end game
		else if(msg.GetMessageType() == MessageType.END_GAME)
		{
			//boradcast this update to all other players
			Server.getInstance().Broadcast(msg, this);
		}
		//if the message type is disconnect
		else if(msg.GetMessageType() == MessageType.DISCONNECT)
		{
			//disconnect the client
			DisconnectClient();
		}
	}
	/**
	 * sends a tcp message
	 * @param msg the network message being sent
	 */
	public void SendMessage(NetworkMessage msg)
	{
		try
		{
			//sends the network message
			_OutputStream.writeObject(msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();    
			ErrorCount++;
		}
	}
	/**
	 * sends UDP messages
	 * @param msg the network message
	 */
	public synchronized void SendMessageUDP(NetworkMessage msg)
	{
		//if the clientsocket is not closed.
		if(!_ClientSocket.isClosed())
		{
			try
			{
				//create an outputbytearray
				_UDPOutputByteArray = new ByteArrayOutputStream();
				//creates a new outputstream with the outputbytearray
				_UDPOutputStream = new ObjectOutputStream(_UDPOutputByteArray);
				
				//writes the message to the outputbytearray
				_UDPOutputStream.writeObject(msg);
				//flushes the outputstream.
				_UDPOutputStream.flush();
				
				//sets the bytearray information to the buffer
				byte[] buffer = _UDPOutputByteArray.toByteArray();
				
				//creates a new datagram packet with the buffer information and the network information
				DatagramPacket p = new DatagramPacket(buffer, buffer.length, _ClientSocket.getInetAddress(), _UDPSocket.getLocalPort() + 1);
				//sends the udp message
				_UDPSocket.send(p);
				//resets the bytearray
				_UDPOutputByteArray.reset();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				ErrorCount++;
			}
		}
	}
	
	/**
	 * Starts listening for UDP packets
	 */
	public void StartUDPListen()
	{
		UDPListenThread = new Thread(UDPListen);
		UDPListenThread.start();
	}
	
	/**
	 * Listens for UDP packets
	 */
	private Runnable UDPListen = () ->
	{	
		//while there are less than 3 errors and the client socket isn't closed.
		while(ErrorCount < 3 && !_ClientSocket.isClosed())
		{
			//creates a buffer (required for the datagram packet)
			byte[] buf = new byte[1024];
			//creates the datagram packet using the buffer
			DatagramPacket dp = new DatagramPacket(buf, buf.length);
			try
			{
				//recieves the data into a datagram packet
				_UDPSocket.receive(dp);
				//gets the data from the datagram packet and stores it into the inputbytearray
				 _UDPInputByteArray = new ByteArrayInputStream(dp.getData());
				 
				 //creates a new inputstream using the inputbytearray
				 _UDPInputStream = new ObjectInputStream(_UDPInputByteArray);
				 //reads the object from the inputstream into a network message
				 NetworkMessage msg = (NetworkMessage) _UDPInputStream.readObject();
				 //closes the inputstream
				 _UDPInputStream.close();
				 //checks the contents of the message
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
