package com.Cooper.Network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.Cooper.Game.Map;
import com.Cooper.Game.Player;
import com.Cooper.Network.Message.MessageTypes.MessageType;
import com.Cooper.Network.Message.Messages.NetworkMessage;

/**
 * @author PursDios
 * @version 1.2
 */
public class Server
{
	//if the server is running or not
	private boolean IsRunning=false;
	//the server instance
	private static Server Instance;
	//the server socket.
	private ServerSocket _ServerSocket;
	//the port the server is running on
	private int _PortNumber;
	//the array list of sessions (each player has a session)
	private ArrayList<ServerSession> _SessionList;
	//the listening thread for TCP
	private Thread ListenThread;
	//the list of client threads
	private ExecutorService _ClientThreads;
	//the map being used for the game.
	private Map _Map;
	
	/**
	 * Ensures that there is never more than one server.
	 * @return Returns the server instance.
	 */
	public static Server getInstance()
	{
		if(Instance == null)
		{
			Instance = new Server();
		}
		return Instance;
	}
	
	/**
	 * returns a list of sessions
	 * @return returns a list of sessions
	 */
	public ArrayList<ServerSession> GetClientSessions()
	{
		return _SessionList;
	}
	
	/**
	 * removes a session from the list
	 * @param s the session being removed
	 */
	public void RemoveClient(ServerSession s)
	{
		_SessionList.remove(s);
	}
	
	/**
	 * returns the list of players.
	 * @return the list of players
	 */
	public ArrayList<Player> GetPlayers()
	{
		//creates a new arraylist of players
		ArrayList<Player> Players = new ArrayList<Player>();
		
		//for each server session
		for(ServerSession s : _SessionList)
		{
			//add the serversession instance of the player
			Players.add(s.GetPlayer());
		}
		//return the arraylist of players
		return Players;
	}
	
	/**
	 * Starts the server.
	 * @throws IOException 
	 */
	public void StartServer() throws IOException
	{
		//sets the port number
		_PortNumber = 7050;
		
		//creates a new map with the default map of 1
		_Map = new Map("Maps/map1.png");
		_Map.SetName("map1");
		
		//starts the server socket with the given port number
		_ServerSocket = new ServerSocket(_PortNumber);
		
		//creates a new sessionlist
		_SessionList = new ArrayList<ServerSession>();
		//creates a new listening thread
		ListenThread = new Thread(Listen);
		//creates a newlist of sessions
		_ClientThreads = Executors.newCachedThreadPool();
		//sets is running to true
		IsRunning = true;
		//starts the thread listening.
		ListenThread.start();
	}
	
	/**
	 * https://dzone.com/articles/java-8-lambda-functions-usage-examples
	 * Creates a new runnable function that performs these tasks.
	 */
	private Runnable Listen = () ->
	{
		//while the server is running
		while(IsRunning)
		{
			try
			{
				//accept a new player (and store their socket information)
				Socket NewClient = _ServerSocket.accept();
				//create a serversession with that socket information
				ServerSession ClientSession = new ServerSession(NewClient);
				
				//open the connection with that serversession
				ClientSession.OpenConnection();
				//add the sessopm to the list
				_SessionList.add(ClientSession);
				//add a new thread to the list.
				_ClientThreads.submit(ClientSession);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * Broadcasts a message to all of the other clients
	 * @param msg the message being sent
	 * @param sender the sender of the message (so it isnt sent to themselves)
	 */
	public void Broadcast(NetworkMessage msg, ServerSession sender)
	{
		//for each session
		for(ServerSession s : _SessionList)
		{
			//where the session isn't the sender
			if(s != sender)
			{
				//send the message
				s.SendMessage(msg);
			}
		}
	}
	
	/**
	 * Broadcasts a udp message
	 * @param msg the message being sent
	 * @param sender the sender of the message
	 */
	public void BroadcastUDP(NetworkMessage msg, ServerSession sender)
	{
		//for each sessi8on
		for(ServerSession s : _SessionList)
		{
			//where the session isn't the sender
			if(s != sender)
			{
				//send a udp message
				s.SendMessageUDP(msg);
			}
		}
	}
	
	/**
	 * checks for the start of the game (all players are ready)
	 */
	public void CheckForStart()
	{
		//start is true
		boolean start = true;
		
		//for each session
		for(ServerSession s : _SessionList)
		{
			//if the player isn't ready set start to false
			if(!s._ClientPlayer.GetReady())
			{
				start = false;
			}
		}
		//if start is still true all the players are ready
		if(start)
		{
			//create a start game message
			NetworkMessage msg = new NetworkMessage(MessageType.START_GAME);
			
			//fro each session
			for(ServerSession s : _SessionList)
			{
				//start listening for UDP packets
				s.StartUDPListen();
				//send the start game message
				s.SendMessage(msg);
			}
		}
	}
	
	/**
	 * sets the map to be the same as the one sent
	 * @param name the name of the map
	 * @param loc the location of the map
	 */
	public void SetMap(String name, String loc)
	{
		//sets the map name
		_Map.SetName(name);
		//sets the map location
		_Map.setMapLocation(loc);
	}
	
	/**
	 * returns the local map
	 * @return returns the local map
	 */
	public Map GetMap()
	{
		//returns the map object
		return _Map;
	}
}
