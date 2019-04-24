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
 * @version 1.1
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
	
	/**
	 * Constructor for the controller
	 */
	private Controller()
	{
		//sets the Selected map to the default.
		_SelectedMap = new Map("/Maps/map1.png");
		//creates a new list of players
		_PlayerList = new ArrayList<Player>();
		//Creates a new collision object with the selected map
		c = new Collision(_SelectedMap);
	}

	/**
	 * returns the arraylist of players
	 * @return arraylist of players
	 */
	public ArrayList<Player> GetPlayerList()
	{
		return _PlayerList;
	}
	
	/**
	 * returns the selected map
	 * @return String location of the selected map.
	 */
	public Map GetMap()
	{
		return _SelectedMap;
	}
	
	/**
	 * returns the collision object
	 * @return collision object.
	 */
	public Collision GetCollision()
	{
		return c;
	}
	
	/**
	 * Sets the local map
	 * @param maploc the location of the map
	 * @param mapname the name of the map
	 */
	public void SetMap(String maploc, String mapname)
	{
		//sets the location of the map
		_SelectedMap.setMapLocation(maploc);
		//sets the name of the map
		_SelectedMap.SetName(mapname);
		//remakes the collision object (to recreate the masks required for the map)
		c = new Collision(_SelectedMap);
		//updates the multiplayer lobby's image.
		UIControl.getInstance().UpdateMap(maploc);
	}
	
	/**
	 * adds a new player to the list
	 * @param p Player object
	 */
	public void AddPlayer(Player p)
	{
		//adds the player to the list
		_PlayerList.add(p);
		//updates the Multiplayer UI to account for the new player.
		UIControl.getInstance().NewPlayerConnected();
	}
	/**
	 * Updates the name of the player
	 * @param id the id of the player who's name has changed
	 * @param name the new name of the player
	 */
	public void UpdatePlayerName(int id, String name)
	{
		//string to store the old name of the player
		String oldname="";
		//searches for the player
		for(Player player : _PlayerList)
		{
			//if the id is the same as the id of the player who's name is being changed.
			if(player.GetPlayerID() == id)
			{
				//save the players old name
				oldname = player.GetPlayerName();
				//sets the players new name.
				player.SetPlayerName(name);
			}
		}
		//Updates the multiplayer lobby to show the new players name.
		UIControl.getInstance().UpdatePlayerName(oldname, name);
	}
	
	/**
	 * Updates the players car.
	 * @param id the id of the player
	 * @param carloc the location of the car.
	 */
	public void UpdatePlayerCar(int id, String carloc)
	{
		//searches for the player
		for(Player player : _PlayerList)
		{
			//if the player id is the same as the id we are looking for.
			if(player.GetPlayerID() == id)
			{
				//sets the players car location.
				player.GetPlayerCar().setImageLocation(carloc);
			}
		}
	}
	
	/**
	 * Updates the players ready status
	 * @param id the id of the player being updated
	 * @param ready the ready status (boolean)
	 */
	public void UpdatePlayerReady(int id, boolean ready)
	{
		//string to change true or false to ready or not ready
		String isReady="";
		
		//changes the text depending on the ready status
		if(ready)
			isReady = "ready";
		else if(!ready)
			isReady = "not ready";
		
		//searches for the player
		for(Player player : _PlayerList)
		{
			//if the id is the same as the id we're searching for
			if(player.GetPlayerID() == id)
			{
				//updates the players ready status
				player.SetReady(ready);
				//sends a message to the lobby chat.
				UIControl.getInstance().AddChatMessage("System: ", player.GetPlayerName() + " is now " + isReady);
				//Updates the ready list.
				UIControl.getInstance().UpdateReadyList();
			}
		}
	}
	
	/**
	 * Updates the chat with a message
	 * @param id the id of the sender
	 * @param Message the message the user is trying to send
	 */
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
	
	/**
	 * Starts the multiplayer game.
	 */
	public void StartMultiPlayerGame()
	{
		//reads in the start locations for the currently selected maps.
		_SelectedMap.SetStartLocations();
		//the startlocation being read in
		int startCount=0;
		//for each player in the list
		for(Player player : _PlayerList)
		{
			//creates a new vehiclelabel for the player 
			player.GetPlayerCar().SetVehicleLabel(new VehicleLabel(player));
			//sets the icon of that vehilelabel to the vehicleicon.
			player.GetPlayerCar().GetVehicleLabel().setIcon(new ImageIcon(player.GetPlayerCar().getImageIcon().getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
			//sets the size of the vehiclelabel
			player.GetPlayerCar().GetVehicleLabel().setSize(50, 50);
			//sets the location of the vehiclelabel to be the startlocation of the map (first player gets the first start position, 2nd the second etc.)
			player.GetPlayerCar().GetVehicleLabel().setLocation(_SelectedMap.getStartLocations()[startCount][0], _SelectedMap.getStartLocations()[startCount][1]);
			//increments the startlocation number
			startCount++;
			//the creates a mask of the car (black and white image)
			player.GetPlayerCar().SetMask(c.MakeCarMask(player.GetPlayerCar().getImage(), Color.WHITE, 254));
			//creates a path of the car (outline used for collision)
			player.GetPlayerCar().SetPath(c.MakeCarPath(player.GetPlayerCar().getMask()));
			
			//if the player is the same as the localplayer
			if(player.GetPlayerID() == Client.GetInstance().GetPlayer().GetPlayerID())
			{
				//update the local player.
				Client.GetInstance().SetFinalLocalPlayer(player);
			}
		}
		//load the game UI.
		UIControl.getInstance().LoadGame();
	}
	
	/**
	 * Loads the connect menu
	 */
	public void LoadConnect()
	{
		UIControl.getInstance().LoadConnect();
	}
	/**
	 * loads the multiplayer lobby
	 * @param host if the player is the host or not.
	 */
	public void MultiplayerLoadLobby(boolean host)
	{
		//if the player is the host
		if(host)
		{
			try 
			{
				//start the server
				Server.getInstance().StartServer();
				//connect to the server.
				Client.GetInstance().Connect("127.0.0.1", 7050);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		} 
		//load the multiplayer lobby.
		UIControl.getInstance().MultiplayerLoadLobby(host);
	}
	
	/**
	 * Connects to a server
	 * @param Address the IP address of the server.
	 * @return 
	 */
	public boolean Connect(String Address)
	{
		//connects to the server using the port 7050 and the IP address given.
		boolean successful = Client.GetInstance().Connect(Address, 7050);
		if(successful)
		{
			//loads the multiplayer lobby.
			MultiplayerLoadLobby(false);
			return true;
		}
		return false;
	}
	
	/**
	 * returns the local player
	 * @return the local player.
	 */
	public Player GetLocalPlayer()
	{
		return Client.GetInstance().GetPlayer();
	}
	
	/**
	 * sends the ready status of the local player
	 * @param ready whether the player is ready or not 
	 */
	public void SendReady(boolean ready)
	{
		//sends the ready check
		Client.GetInstance().SetReady(ready);
		System.out.println("Ready Sent");
	}
	
	/**
	 * Sends out a player name change.
	 * @param name the name of the player.
	 */
	public void SendPlayerNameChange(String name)
	{
		//sends out the player name update.
		Client.GetInstance().UpdatePlayerName(name);
	}
	
	/**
	 * sends out a chat message
	 * @param playerid the player id sending the message
	 * @param message the message being sent.
	 */
	public void SendChatMessage(int playerid, String message)
	{
		//sends the chat message.
		Client.GetInstance().SendChatMessage(playerid, message);
	}

	/**
	 * Updates the map
	 * @param name the name of the map
	 * @param path the location of the map.
	 */
	public void UpdateMap(String name, String path) 
	{
		//sets the name of the map.
		_SelectedMap.SetName(name);
		//sets the maplocation
		_SelectedMap.setMapLocation(path);
		//updates the collision map.
		c = new Collision(_SelectedMap);
		//sends out the server update message.
		Client.GetInstance().UpdateMap(name , path);
	}

	/**
	 * Sends out a player wins update.
	 * @param p the player that has won.
	 */
	public void PlayerWins(Player p) 
	{
		//sends out a player wins update.
		Client.GetInstance().LocalPlayerWins(p.GetPlayerName());
	}

	/**
	 * This is run when the game has been won by someone.
	 * @param Winner the name of the player who won.
	 */
	public void FinishGame(String Winner) 
	{
		//disconnects the player from the server.
		Disconnect();
		//notifies the player that someone has won.
		JOptionPane.showMessageDialog(null, Winner + " Wins!", "InfoBox: " + "Game Over", JOptionPane.DEFAULT_OPTION);
		//closes the game
		System.exit(0);
	}
	
	/**
	 * disconnects the player from the server.
	 */
	public void Disconnect()
	{
		System.out.println("DISCONNECT CALLED");
		Client.GetInstance().Disconnect();
	}
}
