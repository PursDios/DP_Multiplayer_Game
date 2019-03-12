package com.Cooper.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.Cooper.Control.Collision;
import com.Cooper.Control.Controller;
import com.Cooper.Game.Checkpoint;
import com.Cooper.Game.Map;
import com.Cooper.Game.Player;

/**
 * @author Ryan Cooper
 * @version 1.0
 */

public class Game extends JPanel implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//arraylist of all the players in the game.
	private Player _CurrentPlayer;
	//the map being used.
	private Map _Map;
	private Collision c;
	//Image of the map.
	private BufferedImage _MapImage;
	//What buttons are pressed (excluding W and S which are stored elsewhere because they are used for speed calculations)
	private boolean _DPressed, _APressed;
	//timer for updates.
	private Timer _UpdateTimer;
	
	//size of the world x and y
	private int WORLD_SIZE_X, WORLD_SIZE_Y;
	private int _camX, _camY;
	private final int VIEWPOINT_SIZE_X= 1500, VIEWPOINT_SIZE_Y= 1000;
	
	/**
	 * Constructor for the single player game.
	 * @param p player 1
	 * @param p2 player 2
	 * @param Map map being used.
	 */
	public Game()
	{
		//creates all the items
		CreateItems();
		//sets the layout to null
		this.setLayout(null);
		//makes the panel focusable.
		this.setFocusable(true);
		//sets the panel to visible
		this.setVisible(true);
		//repaints the world.
		repaint();
	}
	/**
	 * Creates all the visual elements and their settings.
	 * @param p player 1
	 * @param p2 player 2
	 * @param m map being used.
	 */
	private void CreateItems()
	{
		//Sets the map
		this._Map = Controller.getInstance().GetMap();
		//Sets the collision
		this.c = Controller.getInstance().GetCollision();
		//Sets the player
		this._CurrentPlayer = Controller.getInstance().GetLocalPlayer();
		//Sets the image of the map.
		this._MapImage = _Map.getMapImage();
		
		//for each player in the list of players
		for(Player player : Controller.getInstance().GetPlayerList())
		{
			//if the player id doesn't equal the current players id
			if(player.GetPlayerID() != _CurrentPlayer.GetPlayerID())
			{
				//get set the vehiclelabel to visible.
				player.GetPlayerCar().GetVehicleLabel().setVisible(true);
				//adds the players label to the current panel
				this.add(player.GetPlayerCar().GetVehicleLabel());
			}
			else
			{
				//sets the CURRENTPLAYERS vehiclelabel to true
				_CurrentPlayer.GetPlayerCar().GetVehicleLabel().setVisible(true);
				//adds the currentplayers label to the panel.
				this.add(_CurrentPlayer.GetPlayerCar().GetVehicleLabel());
			}
		}
		
		//Creates the timer
		_UpdateTimer = new Timer(20,this);
		
		//adds all the checkpoints to the map.
		for(Checkpoint check : _Map.GetCheckPoints())
		{
			//add it to the map.
			this.add(check);
		}
		//adds a listener to the panel.
		addKeyListener(this);
		//starts the update timer.
		_UpdateTimer.start();
	}
	
	//https://gamedev.stackexchange.com/questions/44256/how-to-add-a-scrolling-camera-to-a-2d-java-game
	private void DrawWorld(Graphics g)
	{
		//Casts graphics g into Grpahics2D;
		Graphics2D g2d = (Graphics2D)g;
		AffineTransform at = new AffineTransform();
		
		if(_Map == null)
		{
			//If a map somehow isn't set then set it to the default.
			//(Shouldn't happen but just incase)
			_Map = new Map("Maps/map0.png");
		}
		//gets the x and y size of the map and makes it 4 times larger.
		WORLD_SIZE_X = _MapImage.getWidth(null) * 4;
		WORLD_SIZE_Y = _MapImage.getHeight(null) * 4;
		
		//calculates the offsets.
		int offsetMaxX = WORLD_SIZE_X - VIEWPOINT_SIZE_X;
		int offsetMaxY = WORLD_SIZE_Y - VIEWPOINT_SIZE_Y;
		int offsetMinX = 0;
		int offsetMinY = 0;
		
		//calcualtes the camera location
		_camX = (_CurrentPlayer.GetPlayerCar().GetVehicleLabel().getX() + (_CurrentPlayer.GetPlayerCar().GetVehicleLabel().getWidth() / 2)) - VIEWPOINT_SIZE_X / 2;
		_camY = (_CurrentPlayer.GetPlayerCar().GetVehicleLabel().getY() + (_CurrentPlayer.GetPlayerCar().GetVehicleLabel().getHeight() / 2)) - VIEWPOINT_SIZE_Y / 2;
	
		if (_camX > offsetMaxX)
			_camX = offsetMaxX;
		if (_camY > offsetMaxY)
			_camY = offsetMaxY;
		if (_camX < offsetMinX)
			_camX = offsetMinX;
		if (_camY < offsetMinY)
			_camY = offsetMinY;
		
		//sets the camera loation
		at.translate(-_camX, -_camY);
		
		//transforms the camera location
		g2d.transform(at);
		//draws the scaled image.
		g2d.drawImage(_MapImage, 0, 0, (int)WORLD_SIZE_X, (int)WORLD_SIZE_Y, null);
	}
	
	/**
	 * Draws the world
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		DrawWorld(g);
	}
	/**
	 * When a key is pressed.
	 */
	@Override
	public void keyPressed(KeyEvent ke) 
	{
		// TODO Auto-generated method stub
		//if the button is w
		if(ke.getKeyCode() == KeyEvent.VK_W)
		{
			_CurrentPlayer.GetPlayerCar().SetW(true);
		}
		//if the button is d
		if(ke.getKeyCode() == KeyEvent.VK_D)
		{
			_DPressed = true;
		}
		//if the button is a
		if(ke.getKeyCode() == KeyEvent.VK_A)
		{
			_APressed = true;
		}
		//if the button is s
		if(ke.getKeyCode() == KeyEvent.VK_S)
		{
			_CurrentPlayer.GetPlayerCar().SetS(true);
		}
		
		//closes the game
		if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
	}
	/**
	 * if the key is released
	 */
	@Override
	public void keyReleased(KeyEvent kr) 
	{
		// TODO Auto-generated method stub
		//if w is released
		if(kr.getKeyCode() == KeyEvent.VK_W)
		{
			_CurrentPlayer.GetPlayerCar().SetW(false);
		}
		//if s is released
		if(kr.getKeyCode() == KeyEvent.VK_S)
		{
			_CurrentPlayer.GetPlayerCar().SetS(false);
		}
		//if d is released
		if(kr.getKeyCode() == KeyEvent.VK_D)
		{
			_DPressed = false;
		}
		//if a is released
		if(kr.getKeyCode() == KeyEvent.VK_A)
		{
			_APressed = false;
		}
	}
	
	/**
	 * irrelevent but required for the implements keylistener.
	 */
	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * When an action is performed this is called.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		//requests focus.
		this.requestFocus();
		//if the source is a timer
		if(ae.getSource().getClass() == Timer.class)
		{
			//call update.
			Update();
		}
	}
	
	/**
	 * Updates the current players locations.
	 */
	private void Update()
	{
		//if d is pressed change the angle of the car.
		if(_DPressed)
		{
			_CurrentPlayer.GetPlayerCar().setAngle(_CurrentPlayer.GetPlayerCar().getAngle() + 0.10);
		}
		//if a is pressed and d isn't change the angle of the car.
		else if(_APressed)
		{
			_CurrentPlayer.GetPlayerCar().setAngle(_CurrentPlayer.GetPlayerCar().getAngle() - 0.10);
		}
		//repaint the world.
		repaint();
		
		//move each player (update their locations.
		//move the car (checks for collisions first)
		_CurrentPlayer.GetPlayerCar().Move(c, Controller.getInstance().GetPlayerList());
	}
}
