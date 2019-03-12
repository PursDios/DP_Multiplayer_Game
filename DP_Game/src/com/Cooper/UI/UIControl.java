package com.Cooper.UI;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.Cooper.Network.Client;
import com.Cooper.UI.Menus.ConnectMenu;
import com.Cooper.UI.Menus.MainMenu;
import com.Cooper.UI.Menus.MultiplayerLobbyMenu;

/**
 * @author Ryan Cooper
 * @version 1.0
 */

public class UIControl extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//singleston UIControl Instance.
	private static UIControl _Instance;
	//container for all the JPanels.
	private final Container _Container;
	//arraylist of all the JPanels.
	private ArrayList<JPanel> _JPanelList;
	private boolean IsConnected;
	
	//main menu instance
	private MainMenu _MainMenuScreen;
	//game instance
	private Game _GameScreen;
	//Connect menu instance
	private ConnectMenu _ConnectScreen;
	//MultiplayerLobbyMenu
	private MultiplayerLobbyMenu _MultiplayerLobbyScreen;
	
	/**
	 * returns the singleston UIControl or makes it if it doesn't exist.
	 * @return
	 */
	public static UIControl getInstance()
	{
		if(_Instance == null)
		{
			_Instance = new UIControl();
		}
		return _Instance;
	}
	
	/**
	 * Constructor for the UIControl.
	 */
	private UIControl()
	{
		//sets the title of the frame.
		setTitle("gokart.GAME");
		//sets it so that it can't be resized.
		setResizable(false);
		//sets the bounds of the UIControl to be 1000,1000
		setBounds(0,0,1000,1000);
		//sets the locationrealtiveto to null.
		setLocationRelativeTo(null);
		//sets the default close operation to close the window.
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		//creates the jpanellist
		_JPanelList = new ArrayList<JPanel>();
		//creates the container.
		_Container = getContentPane();
		
		StartWindowListener();
	}
	
	/**
	 * Listens for the window being closed.
	 */
	public void StartWindowListener()
	{
		this.addWindowListener(new WindowAdapter()
		{
			@Override
            public void windowClosing(WindowEvent e) 
			{
                System.out.println("Window Closing...");
                //if the player is currently connected to a server
                if(IsConnected)
                {
                	//disconnect
                	Client.GetInstance().Disconnect();
                }
                //close the program.
                System.exit(0);
            }
		});
	}
	
	/**
	 * Hides all the other screens and displays the mainmenu screen.
	 */
	public void LoadMainMenu() 
	{
		HideAllScreens();
		
		if (_MainMenuScreen == null)
		{
			_MainMenuScreen = new MainMenu();
			_JPanelList.add(_MainMenuScreen);
			_Container.add(_MainMenuScreen);
		}
		this.setSize(400, 250);
		_MainMenuScreen.setVisible(true);
		this.setVisible(true);
	}
	
	/**
	 * Loads the connect menu
	 */
	public void LoadConnect()
	{
		HideAllScreens();
		if(_ConnectScreen == null)
		{
			_ConnectScreen = new ConnectMenu();
			_JPanelList.add(_ConnectScreen);
			_Container.add(_ConnectScreen);
		}
		this.setSize(290,150);
		_ConnectScreen.setVisible(true);
		this.setVisible(true);
	}
	
	/**
	 * loads the multiplayer lobby menu
	 * @param host whether the user is the host or not
	 */
	public void MultiplayerLoadLobby(boolean host)
	{
		HideAllScreens();
		if(_MultiplayerLobbyScreen == null)
		{
			_MultiplayerLobbyScreen = new MultiplayerLobbyMenu(host);
			_JPanelList.add(_MultiplayerLobbyScreen);
			_Container.add(_MultiplayerLobbyScreen);
		}
		this.setSize(1210,1000);
		_MultiplayerLobbyScreen.setVisible(true);
		this.setVisible(true);
		IsConnected = true;
	}
	
	/**
	 * loads the game.
	 */
	public void LoadGame()
	{
		HideAllScreens();
		if(_GameScreen == null)
		{
			_GameScreen = new Game();
			_JPanelList.add(_GameScreen);
			_Container.add(_GameScreen);
		}
		this.setSize(1500, 1000);
		_GameScreen.setVisible(true);
		this.setVisible(true);
	}
	
	/**
	 * Updates the multiplayer lobby's map image
	 * @param maploc the location of the mapimage
	 */
	public void UpdateMap(String maploc)
	{
		//if the multiplayer lobby screen isn't null
		if(_MultiplayerLobbyScreen != null)
		{
			//update the multiplayerlobby screen map.
			_MultiplayerLobbyScreen.UpdateMap(maploc);
		}
	}
	
	/**
	 * adds a chat message to the multiplayer lobby screen.
	 * @param playername the name of the player sending the message
	 * @param message the message being sent by the player
	 */
	public void AddChatMessage(String playername, String message)
	{
		//sends the message to the chat.
		_MultiplayerLobbyScreen.AddMessage(playername + ": ", message);
	}
	
	/**
	 * writes a message to the chat and updates the player ready list.
	 */
	public void NewPlayerConnected() 
	{
		if(_MultiplayerLobbyScreen !=null)
		{
			//sends a message to the chat
			_MultiplayerLobbyScreen.AddMessage("System: ", " a new player has joined!");
			//updates the player ready list
			this.UpdateReadyList();
		}
	}
	
	/**
	 * update the ready list
	 */
	public void UpdateReadyList() 
	{
		if(_MultiplayerLobbyScreen != null)
		{
			//updates the multiplayer lobby screen ready list.
			_MultiplayerLobbyScreen.UpdateReadyList();
		}
	}
	
	/**
	 * hides all the panels in the jpanellist.
	 */
	private void HideAllScreens() 
	{
		_JPanelList.stream().forEach(s -> s.setVisible(false));
		//for(JPanel jp : _JPanelList)
		//{
		//	jp.setVisible(false);
		//}
	}
	
	//Required by the extend.
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
	}

	/**
	 * Updates the player name
	 * @param oldname the previous name of the player
	 * @param playername the new name of the player
	 */
	public void UpdatePlayerName(String oldname, String playername) 
	{
		if(_MultiplayerLobbyScreen != null)
		{
			//writes a message to the chat
			_MultiplayerLobbyScreen.AddMessage(oldname, " has changed their name to " + playername);
			//updates the ready list (to account for the new name)
			_MultiplayerLobbyScreen.UpdateReadyList();
		}
	}
}
