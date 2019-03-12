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

public class Server
{
	private boolean IsRunning=false;
	private static Server Instance;
	private ServerSocket _ServerSocket;
	private int _PortNumber;
	private ArrayList<ServerSession> _SessionList;
	private Thread ListenThread;
	private ExecutorService _ClientThreads;
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
	
	public ArrayList<ServerSession> GetClientSessions()
	{
		return _SessionList;
	}
	
	public void RemoveClient(ServerSession s)
	{
		_SessionList.remove(s);
	}
	
	public ArrayList<Player> GetPlayers()
	{
		ArrayList<Player> Players = new ArrayList<Player>();
		
		for(ServerSession s : _SessionList)
		{
			Players.add(s.GetPlayer());
		}
		return Players;
	}
	
	public void StartServer() throws IOException
	{
		_PortNumber = 7050;
		
		_Map = new Map("resources/Maps/map1.png");
		_Map.SetName("map1");
		
		_ServerSocket = new ServerSocket(_PortNumber);
		
		_SessionList = new ArrayList<ServerSession>();
		ListenThread = new Thread(Listen);
		_ClientThreads = Executors.newCachedThreadPool();
		IsRunning = true;
		ListenThread.start();
	}
	
	/**
	 * https://dzone.com/articles/java-8-lambda-functions-usage-examples
	 * Creates a new runnable function that performs these tasks.
	 */
	private Runnable Listen = () ->
	{
		while(IsRunning)
		{
			try
			{
				Socket NewClient = _ServerSocket.accept();
				
				ServerSession ClientSession = new ServerSession(NewClient);
				
				ClientSession.OpenConnection();
				
				_SessionList.add(ClientSession);
				_ClientThreads.submit(ClientSession);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	
	public void Broadcast(NetworkMessage msg, ServerSession sender)
	{
		for(ServerSession s : _SessionList)
		{
			if(s != sender)
			{
				s.SendMessage(msg);
			}
		}
	}
	
	public void BroadcastUDP(NetworkMessage msg, ServerSession sender)
	{
		for(ServerSession s : _SessionList)
		{
			if(s != sender)
			{
				s.SendMessageUDP(msg);
			}
		}
	}
	
	public void CheckForStart()
	{
		boolean start = true;
		
		for(ServerSession s : _SessionList)
		{
			if(!s._ClientPlayer.GetReady())
			{
				start = false;
			}
		}
		if(start)
		{
			NetworkMessage msg = new NetworkMessage(MessageType.START_GAME);
			
			for(ServerSession s : _SessionList)
			{
				s.StartUDPListen();
				s.SendMessage(msg);
			}
		}
	}
	
	public void SetMap(String name, String loc)
	{
		_Map.SetName(name);
		_Map.setMapLocation(loc);
	}
	
	public Map GetMap()
	{
		return _Map;
	}
}
