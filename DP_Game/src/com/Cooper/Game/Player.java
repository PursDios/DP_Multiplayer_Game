package com.Cooper.Game;

/**
 * @author Ryan Cooper
 * @version  1.1
 */

public class Player 
{
	//The vehicle the player is using.
	private String _PlayerName;
	private int _PlayerID;
	private Vehicle _PlayerCar;
	private int CheckPointsPassed;
	private boolean _Ready;
	
	/**
	 * The construactor for the Player class.
	 * @param ID an integer represnting what number player they are. 
	 */
	public Player(int ID, String playername)
	{
		_PlayerCar = new Vehicle(ID);
		_PlayerID = ID;
		this._PlayerName = playername;
		_PlayerCar.setImageLocation("Cars/car1.png");
	}
	/**
	 * Returns the players name.
	 * @return String players name 
	 */
	public String GetPlayerName()
	{
		return _PlayerName;
	}
	/**
	 * Sets the players name
	 * @param playername string of the players name
	 */
	public void SetPlayerName(String playername)
	{
		_PlayerName = playername;
	}
	/**
	 * Gets the players id
	 * @return integer of the players id
	 */
	public int GetPlayerID()
	{
		return _PlayerID;
	}
	/**
	 * Gets the ready status of the player
	 * @return returns the ready status of the player
	 */
	public boolean GetReady()
	{
		return _Ready;
	}
	/**
	 * Sets the players ready status
	 * @param ready the ready status
	 */
	public void SetReady(boolean ready)
	{
		_Ready = ready;
	}
	/**
	 * returns the vehicle.
	 * @return returns a vehicle object.
	 */
	public Vehicle GetPlayerCar()
	{
		return _PlayerCar;
	}
	/**
	 * sets the players vehicle image location.
	 * @param Path sets the players vehicle image location
	 */
	public void SetPlayerCar(String Path) 
	{
		_PlayerCar.setImageLocation(Path);
	}
	/**
	 * Returns the number of checkpoints the user has passed.
	 * @return int number of checkpoints passed.
	 */
	public int GetCheckPointsPassed() 
	{
		return CheckPointsPassed;
	}
	/**
	 * Sets the number of checkpoints this car has passed
	 * @param checkPointsPassed: NumberOfCheckPointsPassed
	 */
	public void SetCheckPointsPassed(int checkPointsPassed) 
	{
		CheckPointsPassed = checkPointsPassed;
	}
}
