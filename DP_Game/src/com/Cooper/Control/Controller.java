package com.Cooper.Control;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.Cooper.Game.Map;
import com.Cooper.Game.Player;
import com.Cooper.Network.Client;
import com.Cooper.Network.Server;
import com.Cooper.UI.UIControl;
import com.Cooper.UI.VehicleLabel;

/**
 * @author Ryan Cooper
 * @version 1.0
 */

public class Controller 
{
	//Singleton
	private static Controller instance;
	
	int _PlayerCount=0;
	private ArrayList<Player> _PlayerList;
	private Map _SelectedMap;
	//collision checker
	private Collision c;
	
	/*
	 * Returns the Controller Instance.
	 */
	public static Controller getInstance()
	{
		if(instance == null)
		{
			instance = new Controller();
		}
		return instance;
	}
	
	private Controller()
	{
		_SelectedMap = new Map("resources/Maps/map1.png");
		_PlayerList = new ArrayList<Player>();
		c = new Collision(_SelectedMap);
	}

	public ArrayList<Player> GetPlayerList()
	{
		return _PlayerList;
	}
	
	public Map GetMap()
	{
		return _SelectedMap;
	}
	
	public Collision GetCollision()
	{
		return c;
	}
	
	public void SetMap(String maploc, String mapname)
	{
		_SelectedMap.setMapLocation(maploc);
		_SelectedMap.SetName(mapname);
		c = new Collision(_SelectedMap);
		UIControl.getInstance().UpdateMap(maploc);
	}
	
	public void AddPlayer(Player p)
	{
		_PlayerList.add(p);
		UIControl.getInstance().NewPlayerConnected();
	}
	
	public void UpdatePlayerName(int id, String name)
	{
		String oldname="";
		for(Player player : _PlayerList)
		{
			if(player.GetPlayerID() == id)
			{
				oldname = player.GetPlayerName();
				player.SetPlayerName(name);
			}
		}
		UIControl.getInstance().UpdatePlayerName(oldname, name);
	}
	
	public void UpdatePlayerCar(int id, String carloc)
	{
		for(Player player : _PlayerList)
		{
			if(player.GetPlayerID() == id)
			{
				player.GetPlayerCar().setImageLocation(carloc);
			}
		}
	}
	
	public void UpdatePlayerReady(int id, boolean ready)
	{
		String isReady="";
		
		if(ready)
			isReady = "ready";
		else if(!ready)
			isReady = "not ready";
		for(Player player : _PlayerList)
		{
			if(player.GetPlayerID() == id)
			{
				player.SetReady(ready);
				UIControl.getInstance().AddChatMessage("System: ", player.GetPlayerName() + " is now " + isReady);
				UIControl.getInstance().UpdateReadyList();
			}
		}
	}
	
	public void UpdateChat(int id, String Message)
	{
		for(Player p : GetPlayerList())
		{
			if(id == p.GetPlayerID())
			{
				UIControl.getInstance().AddChatMessage(p.GetPlayerName(), Message);
			}
		}
	}
	
	/*
	 * Calls UIController to Load the main menu.
	 */
	public void LoadMainMenu()
	{
		UIControl.getInstance().LoadMainMenu();
	}
	
	public void StartMultiPlayerGame()
	{
		_SelectedMap.SetStartLocations();
		
		int startCount=0;
		for(Player player : _PlayerList)
		{
			player.GetPlayerCar().SetVehicleLabel(new VehicleLabel(player));
			player.GetPlayerCar().GetVehicleLabel().setIcon(new ImageIcon(player.GetPlayerCar().getImageIcon().getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			player.GetPlayerCar().GetVehicleLabel().setSize(50, 50);
			player.GetPlayerCar().GetVehicleLabel().setLocation(_SelectedMap.getStartLocations()[startCount][0], _SelectedMap.getStartLocations()[startCount][1]);
			startCount++;
			player.GetPlayerCar().SetMask(c.MakeCarMask(player.GetPlayerCar().getImage(), Color.WHITE, 254));
			player.GetPlayerCar().SetPath(c.MakeCarPath(player.GetPlayerCar().getMask()));
			
			if(player.GetPlayerID() == Client.GetInstance().GetPlayer().GetPlayerID())
			{
				Client.GetInstance().SetFinalLocalPlayer(player);
			}
		}
		UIControl.getInstance().LoadGame();
	}
	
	public void LoadConnect()
	{
		UIControl.getInstance().LoadConnect();
	}
	public void MultiplayerLoadLobby(boolean host)
	{
		if(host)
		{
			try 
			{
				Server.getInstance().StartServer();
				Client.GetInstance().Connect("127.0.0.1", 7050);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		UIControl.getInstance().MultiplayerLoadLobby(host);
	}
	public void Connect(String Address)
	{
		Client.GetInstance().Connect(Address, 7050);
		MultiplayerLoadLobby(false);
	}
	
	public Player GetLocalPlayer()
	{
		return Client.GetInstance().GetPlayer();
	}
	
	
	public void SendReady(boolean ready)
	{
		Client.GetInstance().SetReady(ready);
		System.out.println("Ready Sent");
	}
	
	public void SendPlayerNameChange(String name)
	{
		Client.GetInstance().UpdatePlayerName(name);
	}
	
	public void SendChatMessage(int playerid, String message)
	{
		Client.GetInstance().SendChatMessage(playerid, message);
	}

	public void UpdateMap(String name, String path) 
	{
		_SelectedMap.SetName(name);
		_SelectedMap.setMapLocation(path);
		c = new Collision(_SelectedMap);
		Client.GetInstance().UpdateMap(name , path);
	}

	public void PlayerWins(Player p) 
	{
		Client.GetInstance().LocalPlayerWins(p.GetPlayerName());
	}

	public void FinishGame(String Winner) 
	{
		Disconnect();
		JOptionPane.showMessageDialog(null, Winner + " Wins!", "InfoBox: " + "Game Over", JOptionPane.DEFAULT_OPTION);
		System.exit(0);
	}
	
	public void Disconnect()
	{
		System.out.println("DISCONNECT CALLED");
		Client.GetInstance().Disconnect();
	}
}
