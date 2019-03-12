package com.Cooper.Game;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.Cooper.Control.ResourceLoader;

/**
 * @author Ryan Cooper
 * @version 1.0
 */

public class Map 
{
	//The name of the map.
	private String _Name;
	//The location of the map.
	private String _MapLocation;
	//The image of the map.
	private BufferedImage _Map;
	//the array of all the starting locations for a map.
	private int[][] _StartLocations;
	//The checkpoints for this map.
	private Checkpoint[] _Checkpoints;
	
	/**
	 * Constructor for the Map.
	 * @param MapLocation: The location of the mapfile we are using.
	 */
	public Map(String MapLocation)
	{
		_MapLocation = MapLocation;
	}
	/**
	 * Returns an array of checkpoints.
	 * @return returns an array of checkpoints.
	 */
	public Checkpoint[] GetCheckPoints()
	{
		return _Checkpoints;
	}
	public String GetName()
	{
		return _Name;
	}
	/**
	 * Sets the name of the player
	 * @param Name of the player.
	 */
	public void SetName(String Name)
	{
		_Name = Name;
	}
	/**
	 * Returns the map location (as a string)
	 * @return returns the map location
	 */
	public String getMapLocation()
	{
		return _MapLocation;
	}
	/**
	 * sets the map location
	 * @param MapLocation: A string file location.
	 */
	public void setMapLocation(String MapLocation)
	{
		_MapLocation = MapLocation;
	}
	/**
	 * reads the map location and returns a bufferedImage
	 * @return buffered image map.
	 */
	public BufferedImage getMapImage()
	{
		//If the map is null. Create a new one and set it to the image from the map location.
		try 
		{
			_Map = ImageIO.read(ResourceLoader.load(getMapLocation()));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return _Map;
	}
	/**
	 * Gets the startLocations
	 * @return returns a 2 dimentional int array with the x and y starting locations.
	 */
	public int[][] getStartLocations()
	{
		return _StartLocations;
	}
	/**
	 * Populates the startLocation depending on what map is being used.
	 */
	public void SetStartLocations()
	{
		//if the StartLocations object has not been made.
		if(_StartLocations == null)
		{
			_StartLocations = new int[6][2];
		}
		try 
		{
			//Used to split the string after it's been read.
			String LineResult[];
			//Reads in the text file that's the same name as the map.
			BufferedReader br = ResourceLoader.loadFile("/GameData/" + _Name + ".txt");
			//gets the player locations for all 6 players (map specific)
			for(int i=0;i<6;i++)
			{
				String temp = br.readLine();
				LineResult = temp.split(",");
				_StartLocations[i][0] = Integer.parseInt(LineResult[0]);
				_StartLocations[i][1] = Integer.parseInt(LineResult[1]);
			}
			//Creates the two checkpoints (the start and the one half way across the map)
			_Checkpoints = new Checkpoint[2];
			
			//Gets the location of the two checkpoints (map specific)
			for(int i=0;i<2;i++)
			{
				//0=x cord 1=y cord 2=width/height
				int[] pos = new int[3];
				//reads in the line.
				String temp = br.readLine();
				//Splits the string on ","'s 
				LineResult = temp.split(",");
				//sets pos 0
				pos[0] = Integer.parseInt(LineResult[0]);
				//sets pos 1
				pos[1] = Integer.parseInt(LineResult[1]);
				//sets pos 2
				pos[2] = Integer.parseInt(LineResult[2]);
				
				//creates a new checkpoint object.
				_Checkpoints[i] = new Checkpoint(i);
				//sets the TrackHeightWidth
				_Checkpoints[i].SetTrackHeightWidth(pos[2]);
				//sets the position of the checkpoint.
				_Checkpoints[i].SetPosition(pos);
			}
			//closes the reader.
			br.close();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
}
