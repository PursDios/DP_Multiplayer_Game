package com.Cooper.Game;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.Cooper.Control.Collision;
import com.Cooper.Network.Client;
import com.Cooper.UI.VehicleLabel;

/**
 * @author Ryan Cooper
 * @version 1.0
 */

public class Vehicle 
{
	//if the w or s button is pressed.
	private boolean _WPressed, _SPressed;
	
	//The car's ID
	private int CarID;
	//The image location for the vehicle
	private String _ImageLocation;
	//the imageicon for the vehicle.
	private ImageIcon _ImageIcon;
	//the 2D path of the vehicle
	private Path2D _VehiclePath;
	//The Image of the car / The black and white mask of the vehicle.
	private BufferedImage _Image, _Mask;
	//the vehicle label associated with this vehicle.
	private VehicleLabel _VehicleLabel;
	//The angle of the vehicle / the speed of the vehicle
	private double _Angle, _Speed;
	
	/**
	 * Constructor for the vehicle class
	 * @param carid the id for the vehicle (to identify the vehicle)
	 */
	public Vehicle(int carid)
	{
		CarID = carid;
	}
	/**
	 * Returns the CarID
	 * @return integer with the ID of the vehicle
	 */
	public int GetCarID()
	{
		return CarID;
	}
	/**
	 * Returns the black and white image of the mask
	 * @return bufferedimage mask for the vehicle
	 */
	public BufferedImage getMask()
	{
		return _Mask;
	}
	/**
	 * Sets the mask for the vehicle
	 * @param mask of the vehicle. (black and white image)
	 */
	public void SetMask(BufferedImage Mask)
	{
		_Mask = Mask;
	}
	/**
	 * gets the path of the vehicle
	 * @return returns the Path2D
	 */
	public Path2D GetPath()
	{
		return _VehiclePath;
	}
	/**
	 * Sets the vehicle path
	 * @param path the Path2D of the vehicle
	 */
	public void SetPath(Path2D path)
	{
		_VehiclePath = path;
	}
	/**
	 * sets the vehicle label associated with this vehicle object.
	 * @param vl the vehicle label.
	 */
	public void SetVehicleLabel(VehicleLabel vl)
	{
		_VehicleLabel = vl;
	}
	/**
	 * returns the vehiclelabel of the vehicke
	 * @return the vehiclelabel associated with this object.
	 */
	public VehicleLabel GetVehicleLabel()
	{
		return _VehicleLabel;
	}
	/**
	 * Sets Wpressed to true/false
	 * @param isTrue true / false depending on if the user has pressed W.
	 */
	public void SetW(boolean isTrue)
	{
		_WPressed = isTrue;
	}
	/**
	 * Sets Spressed to true/false
	 * @param isTrue true / false depending on if the user has pressed S.
	 */
	public void SetS(boolean isTrue)
	{
		_SPressed = isTrue;
	}
	/**
	 * Returns the imageLocation of the car.
	 * @return String file location of the car.
	 */
	public String getImageLocation()
	{
		return _ImageLocation;
	}
	/**
	 * sets the image location of the vehicle image.
	 * @param ImageLocation String imagelocation of the vehicle image.
	 */
	public void setImageLocation(String ImageLocation)
	{
		_ImageLocation = ImageLocation;
	}
	/**
	 * Reads in the image and returns it as a bufferedimage
	 * @return BufferedImage of the car.
	 */
	public BufferedImage getImage()
	{
		if(_Image == null)
		{
			try 
			{
				//read in the image from the file location.
				_Image = ImageIO.read(new File(_ImageLocation));
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _Image;
	}
	/**
	 * returns the angle of the car.
	 * @return returns the angle of the car
	 */
	public double getAngle()
	{
		return _Angle;
	}
	/**
	 * sets the angle of the car
	 * @param the angle of the car.
	 */
	public void setAngle(double Angle)
	{
		_Angle = Angle;
	}
	/**
	 * returns the imageicon of the car.
	 * @return the imageicon of the car.
	 */
	public ImageIcon getImageIcon()
	{
		if(_ImageIcon == null)
		{
			if(_ImageLocation == null)
			{
				_ImageLocation = "resources/Cars/NotAudi/audi0.png";
			}
			_ImageIcon = new ImageIcon(_ImageLocation);
		}
		return _ImageIcon;
	}
	/**
	 * Calculates the speed and updates the location of the player.
	 * @param c collision object
	 * @param players arraylist of players
	 */
	public void Move(Collision c, ArrayList<Player> players)
	{
		CalculateSpeed();
		LocationUpdate(c, players);
	}
	/**
	 * Calls methods depending on what buttons are pressed by the user.
	 */
	public void CalculateSpeed()
	{
		if(!_WPressed && !_SPressed)
			Slow();
		else if(_WPressed && !_SPressed)
			Accelerate();
		else if(_SPressed && !_WPressed)
			Reverse();
	}
	/**
	 * Reduces the speed by a percentage of the current speed
	 */
	private void Slow() 
	{
		if(_Speed <= 1.5)
		{
			_Speed = 0;
		}
		else
		{
			_Speed *= 0.85d;
		}
	}
	/**
	 * Increases the speed by a percentage dependant on the users current speed
	 */
	private void Accelerate() 
	{
		// TODO Auto-generated method stub
		double SpeedLimit = 50;
		
		//if the speed is greater than the speedlimit
		if(_Speed > SpeedLimit)
		{
			//set the speed equal to the limit
			_Speed = SpeedLimit;
		}
		else
		{
			//if the speedlimit is greater.
			if(_Speed < SpeedLimit)
			{
				//Can't switch statement double values :(
				
				//If the car isn't moving set the speed to 4
				if(_Speed == 0)
					_Speed = 4;
				//if the speed is less than the speedlimit * 0.3 (e.g. 20 * 0.3 = 6)
				else if(_Speed < SpeedLimit * 0.3)
				{
					_Speed *= 1.04;
				}
				//if the speed is less than the speedlimit * 0.3 (e.g. 20 * 0.6 = 12)
				else if(_Speed < SpeedLimit * 0.6)
				{
					_Speed *= 1.02;
				}
				//increase the speed slightly.
				else
				{
					
					_Speed *= 1.01;
				}
			}
			//Checks to see if the new speed is greater than the speed limit if so sets it to the limit.
			if(_Speed > SpeedLimit)
				_Speed = SpeedLimit;
		}
	}
	/**
	 * sets the speed to -5.
	 */
	private void Reverse() 
	{
		//sets the speed limit to -5
		double SpeedLimit = -5;
		//sets the speed = to the speed limit.
		_Speed = SpeedLimit;
	}
	
	//https://gamedev.stackexchange.com/questions/36046/how-do-i-make-an-entity-move-in-a-direction
	/**
	 * Updates the location depending on if there are collisions or not.
	 * @param c collision object
	 * @param OtherPlayers arraylist of other players.
	 */
	private void LocationUpdate(Collision c, ArrayList<Player> OtherPlayers)
	{
		//sets it to the players current location.
		Point NewLocation = _VehicleLabel.getLocation();
		
		//calculates the new position
		double y = (_Speed / 2) * Math.sin(_Angle);
	    double x = (_Speed / 2 ) * Math.cos(_Angle);
	    
	    //sets the new position to be the current position + the calcualted x and y increment. 
	    NewLocation.x += Math.floor(x);
	    NewLocation.y += Math.floor(y);
	    
	    //if the car isn't going to crash into anything.
	    if(!c.CheckImpact(_VehicleLabel.GetPlayer(), NewLocation.x, NewLocation.y, (int)Math.floor(_Angle), OtherPlayers))
	    {
	    	//updates the vehicles position.
		    _VehicleLabel.setLocation(NewLocation);
		    Client.GetInstance().UpdateCarLocation(NewLocation.x, NewLocation.y, _Angle);
	    }
	    else
	    {
	    	//if they would crash set their speed to 0.
	    	_Speed = 0;
	    }
	}
}
