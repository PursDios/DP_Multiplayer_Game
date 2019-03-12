package com.Cooper.UI.Menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.Cooper.Control.Controller;
import com.Cooper.Control.ResourceLoader;
import com.Cooper.Game.Player;
import com.Cooper.Network.Client;

public class MultiplayerLobbyMenu extends JPanel implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//container for all of the display elements
	private JPanel _Container;
	//all the buttons used on the panel
	private JButton _Ready, _CarPrev, _CarNext, _MapPrev, _MapNext, _SendMessage, _ChangeName;
	//the imageicons for the car and map
	private ImageIcon _CarIcon, _MapIcon;
	//the labels for the panel (for displaying images or text)
	private JLabel _Car, _Map, _PlayerNameLabel, _ReadyListLabel;
	//Area for chat messages
	private JTextArea _Chat;
	//scroll bar for the chat
	private JScrollPane _ChatScroll;
	//textboxes for the user to input text
	private JTextField _Message, _PlayerName;
	//a list of players for the jlist
	private DefaultListModel<String> _ReadyPlayers;
	//the list of ready players
	private JList<String> _ReadyList;
	//the local player
	private Player p;
	
	private ArrayList<String> cars, maps;
	private int SelectedCar=1, SelectedMap=1;
	private boolean _Host;

	/**
	 * Constructor for the multiplayer lobby
	 * @param host whether the player is the host or not
	 */
	public MultiplayerLobbyMenu(boolean host) 
	{
		_Host = host;
		Initalise();
	}
	
	private void Initalise()
	{
		this.setLayout(null);
		this.setVisible(true);
		GetVehicles();
		GetMaps();
		ItemSettings();
		PanelSettings();
		add(_Container);
	}
	
	/**
	 * Gets all of the vehicles currently in the program
	 */
	private void GetVehicles() 
	{
		//arraylist of cars
		cars = new ArrayList<String>();
		//loop status
		boolean loop = true;
		//number of the car
		int i=0;
		do
		{
			//if the resource loader can find a car named cari (i being the number of the car we're looking for next)
			if(ResourceLoader.load("Cars/car" + i + ".png") !=null)
			{
				//add the car to the list
				cars.add("Cars/car" + i + ".png");
			}
			//if not stop looping
			else
				loop = false;
			i++;
		}while(loop == true);
	}
	
	/**
	 * gets all of the maps
	 */
	private void GetMaps()
	{
		//arraylist of maps.
		maps = new ArrayList<String>();
		//loop status
		boolean loop = true;
		//the number of the map we are looking for next
		int i=0;
		do
		{
			//if the resourceloader can find a map by the name of mapi (where i is the number of the map we are looking for next)
			if(ResourceLoader.load("Maps/map" + i + ".png") !=null)
			{
				//add it to the list
				maps.add("Maps/map" + i + ".png");
			}
			//if not stop looping
			else
				loop = false;
			i++;
		}while(loop == true);
	}

	/**
	 * Loads all of the settings for the ui elements of the program.
	 */
	private void ItemSettings() 
	{
		//gets the local player from the controller.
		p = Controller.getInstance().GetLocalPlayer();
		
		//Makes the button and gives it the text 'Ready'
		_Ready = new JButton("Ready");
		//Sets the size of the button to be 180 width by 50 height
		_Ready.setSize(180, 50);
		//sets the location of the button to be 690pixels right by 900 pixels down.
		_Ready.setLocation(150, 900);
		//sets the font of the button to be Consolas and sets the font size to 14 and bold.
		_Ready.setFont(new Font("Consolas", Font.BOLD, 14));
		//Sets the background colour of the button to be white.
		_Ready.setBackground(Color.WHITE);
		//Sets the name of the button (Used in actionPerformed method)
		_Ready.setName("_Ready");
		//Adds an actionListener to the button.
		_Ready.addActionListener(this);

		// Settings for the Previous Car button
		_CarPrev = new JButton("<");
		_CarPrev.setSize(50, 50);
		_CarPrev.setLocation(20, 300);
		_CarPrev.setFont(new Font("Consolas", Font.BOLD, 14));
		_CarPrev.setBackground(Color.WHITE);
		_CarPrev.setName("_CarPrev");
		_CarPrev.addActionListener(this);

		// Settings for the Next Car button
		_CarNext = new JButton(">");
		_CarNext.setSize(50, 50);
		_CarNext.setLocation(410, 300);
		_CarNext.setFont(new Font("Consolas", Font.BOLD, 14));
		_CarNext.setBackground(Color.WHITE);
		_CarNext.setName("_CarNext");
		_CarNext.addActionListener(this);

		// Settings for the Previous Map button
		_MapPrev = new JButton("<");
		_MapPrev.setSize(50, 50);
		_MapPrev.setLocation(20, 675);
		_MapPrev.setFont(new Font("Consolas", Font.BOLD, 14));
		_MapPrev.setBackground(Color.WHITE);
		_MapPrev.setName("_MapPrev");
		if(!_Host)
			_MapPrev.setEnabled(false);
		_MapPrev.addActionListener(this);

		// Settings for the Next Map button
		_MapNext = new JButton(">");
		_MapNext.setSize(50, 50);
		_MapNext.setLocation(410, 675);
		_MapNext.setFont(new Font("Consolas", Font.BOLD, 14));
		_MapNext.setBackground(Color.WHITE);
		_MapNext.setName("_MapNext");
		if(!_Host)
			_MapNext.setEnabled(false);
		
		_MapNext.addActionListener(this);

		//
		_ChangeName = new JButton("Change");
		_ChangeName.setSize(100, 29);
		_ChangeName.setLocation(310, 70);
		_ChangeName.setFont(new Font("Consolas", Font.BOLD, 14));
		_ChangeName.setBackground(Color.WHITE);
		_ChangeName.setName("_ChangeName");
		_ChangeName.addActionListener(this);
		
		// Default Location for car 1
		_CarIcon = new ImageIcon("Cars/car0.png");
		// Give the label the ImageIcon
		_Car = new JLabel(_CarIcon);
		// set the location of the label
		_Car.setLocation(130, 220);
		// set the size of the label.
		_Car.setSize(200, 200);
		// Sets the Icon for the label to display but scaled (This couldn't be done at creation)
		_Car.setIcon(ScaleImage("Cars/car1.png", 200,200));

		// Default Location for map 1
		try 
		{
			_MapIcon = new ImageIcon(ImageIO.read(ResourceLoader.load("Maps/map1.png")));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		// Give the label the ImageIcon
		_Map = new JLabel(_MapIcon);
		// set the location of the label
		_Map.setLocation(90, 550);
		// set the size of the label.
		_Map.setSize(300, 300);
		// Sets the Icon for the label to display but scaled (This couldn't be done at creation)
		_Map.setIcon(ScaleImage("Maps/map1.png", 300, 300));
		
		_Chat = new JTextArea("You have Joined!\nWelcome to the game lobby!\n\n");
		_Chat.setEditable(false);
		_Chat.setLineWrap(true);
		_Chat.setAutoscrolls(true);
		_Chat.setLocation(640, 10);
		_Chat.setSize(445,900);
		_Chat.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		_ChatScroll = new JScrollPane(_Chat);
		_ChatScroll.setLocation(740,10);
		_ChatScroll.setSize(445,900);
		_ChatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		_ChatScroll.setAutoscrolls(true);
		
		_Message = new JTextField();
		_Message.setLocation(740,920);
		_Message.setSize(350, 30);
		
		_SendMessage = new JButton("Send");
		_SendMessage.setLocation(1085, 920);
		_SendMessage.setSize(100,29);
		_SendMessage.setName("_SendMessage");
		_SendMessage.setBackground(Color.WHITE);
		_SendMessage.addActionListener(this);
		
		_PlayerNameLabel = new JLabel("Your Name");
		_PlayerNameLabel.setText("Your Name");
		_PlayerNameLabel.setLocation(160,45);
		_PlayerNameLabel.setSize(200,40);
		_PlayerNameLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
		
		_ReadyListLabel = new JLabel("Ready List");
		_ReadyListLabel.setText("Ready List");
		_ReadyListLabel.setLocation(600,20);
		_ReadyListLabel.setSize(200,40);
		_ReadyListLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		_ReadyPlayers = new DefaultListModel<String>();
		
		for(Player p : Controller.getInstance().GetPlayerList())
		{
			_ReadyPlayers.addElement(p.GetPlayerName() + " " + p.GetReady());
		}
		
		_ReadyList = new JList<String>(_ReadyPlayers);
		_ReadyList.setName("_ReadyList");
		_ReadyList.setLocation(540, 50);
		_ReadyList.setSize(190, 500);
		_ReadyList.setFont(new Font("Consolas", Font.PLAIN, 10));
		
		_PlayerName = new JTextField();
		_PlayerName.setName("_PlayerName");
		_PlayerName.setLocation(160,70);
		_PlayerName.setSize(150,30);
		_PlayerName.setText("Player " + p.GetPlayerID());
		
		UpdateReadyList();
	}

	/// Adds all the items to the container as well as settings the bounds and
	/// visibility of everything to true. Allows for the easy management of display
	/// items.
	private void PanelSettings() 
	{
		_Container = new JPanel();
		_Container.setBounds(0, 0, 1500, 1000);
		_Container.setLayout(null);
		_Container.setVisible(true);
		_Container.add(_Ready);
		_Container.add(_CarPrev);
		_Container.add(_CarNext);
		_Container.add(_MapPrev);
		_Container.add(_MapNext);
		_Container.add(_Car);
		_Container.add(_Map);
		//Only the chat scroll needs to be shown and not the chat because the ChatScroll contains the chat
		_Container.add(_ChatScroll);
		_Container.add(_Message);
		_Container.add(_SendMessage);
		_Container.add(_PlayerName);
		_Container.add(_PlayerNameLabel);
		_Container.add(_ChangeName);
		_Container.add(_ReadyList);
		_Container.add(_ReadyListLabel);
	}

	/// Whenever an action is performed gets what item called it and performs the
	/// appropriate tasks.
	@Override
	public void actionPerformed(ActionEvent a) 
	{
		//if the actionEvent is a button.
		if (a.getSource().getClass() == JButton.class) 
		{
			//cast the source to a button.
			JButton temp = (JButton) a.getSource();

			//get the name of the button.
			switch (temp.getName()) 
			{
			//if the name of the button is '_Ready'
			case "_Ready":
				if(p.GetReady())
				{
					p.SetReady(false);
					_Chat.append("You are now not ready\n");
				}
				else if(!p.GetReady())
				{
					p.SetReady(true);
					_Chat.append("You are now ready\n");
				}
				Controller.getInstance().UpdatePlayerReady(p.GetPlayerID(), p.GetReady());
				Controller.getInstance().SendReady(p.GetReady());
				UpdateReadyList();
				break;
				//if the name of the button is '_CarPrev'
			case "_CarPrev":
				System.out.println("Previous Car");
				SelectedCar--;
				CarManage();
				break;
				//if the name of the button is '_CarNext'
			case "_CarNext":
				System.out.println("Next Car");
				SelectedCar++;
				CarManage();
				break;
				//if the name of the button is '_MapPrev'
			case "_MapPrev":
				System.out.println("Previous Map");
				SelectedMap--;
				MapManage();
				break;
				//if the name of the button is '_MapNext'
			case "_MapNext":
				System.out.println("Next Map");
				SelectedMap++;
				MapManage();
				break;
			case "_SendMessage":
				_Chat.append(p.GetPlayerName() + " " + _Message.getText() + "\n");
				Controller.getInstance().SendChatMessage(p.GetPlayerID(), _Message.getText());
				_Message.setText("");
				break;
			case "_ChangeName":
				_Chat.append("You have changed your name to: " + _PlayerName.getText() + "\n");
				Controller.getInstance().SendPlayerNameChange(_PlayerName.getText());
				break;
			}
		}
	}

	///Scales the ImageIcon sent to it
	private ImageIcon ScaleImage(String path, int Width, int Height) 
	{
		Image i=null;
		try 
		{
			i = ImageIO.read(ResourceLoader.load(path));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		//new image icon
		ImageIcon ii = new ImageIcon(i.getScaledInstance(Width, Height, java.awt.Image.SCALE_SMOOTH));
		//return the imageicon
		return ii;
	}

	/**
	 * Changes the car displayed in the lobby when the next or previous buttons are pressed.
	 */
	private void CarManage() 
	{
		//it it's less than 0 set it to the max
		if(SelectedCar == -1)
			SelectedCar = cars.size() -1;
		//if it's 1 higher than the max set it to the minimum
		else if(SelectedCar == cars.size())
			SelectedCar = 0;
		
		//sets the icon.
		_Car.setIcon(ScaleImage(cars.get(SelectedCar),200,200));
		System.out.println(cars.get(SelectedCar));
		
		//updates all other players to the update.
		Controller.getInstance().UpdatePlayerCar(p.GetPlayerID(), cars.get(SelectedCar));
		//updates the controller instance of the car.
		Client.GetInstance().UpdatePlayerCar(cars.get(SelectedCar));
	}
	
	/**
	 * Changes the map displayed in the lobby when the next or previous buttons are pressed.
	 */
	private void MapManage()
	{
		//it it's less than 0 set it to the max
		if(SelectedMap == -1)
			SelectedMap = maps.size() -1;
		//if it's 1 higher than the max set it to the minimum
		else if(SelectedMap == maps.size())
			SelectedMap = 0;
		//sets the icon.
		_Map.setIcon(ScaleImage(maps.get(SelectedMap), 300, 300));
		
		//updates the controller instance of the map
		Controller.getInstance().UpdateMap("map" + SelectedMap, maps.get(SelectedMap));
		//updates all other players to the update.
		Client.GetInstance().UpdateMap("map" + SelectedMap , maps.get(SelectedMap));
	}

	/**
	 * Updates the map image
	 * @param maplocation the maplocation.
	 */
	public void UpdateMap(String maplocation)
	{
		_Map.setIcon(ScaleImage(maplocation, 300, 300));
	}
	
	/**
	 * Adds a message to the chat
	 * @param playername the name of the player writing the message
	 * @param message the message being written
	 */
	public void AddMessage(String playername, String message) 
	{
		_Chat.append(playername + " " + message + "\n");
	}
	
	/**
	 * Updates the player ready list
	 */
	public void UpdateReadyList()
	{
		//removes everything from the list 
		_ReadyPlayers.clear();
		String readyline;
		//for each player
		for(Player p : Controller.getInstance().GetPlayerList())
		{
			//the name of the player + :
			readyline = p.GetPlayerName() + ": ";
			//if the player is ready add ready to the end of the message
			if(p.GetReady())
				readyline += " ready";
			//if the player isn't ready add not ready to the end of the message
			else if(!p.GetReady())
				readyline += " not ready";
			
			//add at line of text to the readlist.
			_ReadyPlayers.addElement(readyline);
		}
	}
}
