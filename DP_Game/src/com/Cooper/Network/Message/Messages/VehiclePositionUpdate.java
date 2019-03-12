package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

/**
 * @author PursDios
 * @version 1.0
 */
public class VehiclePositionUpdate extends NetworkMessage implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7866645697065093465L;
	private int x;
	private int y;
	private double angle;
	private int PlayerID;
	
	public VehiclePositionUpdate(MessageType type, int PlayerID, int x, int y, double angle) 
	{
		super(type);
		this.x = x;
		this.y = y;
		this.PlayerID = PlayerID;
		this.angle = angle;
	}
	
	public int GetX()
	{
		return x;
	}
	public int GetY()
	{
		return y;
	}
	public double GetAngle()
	{
		return angle;
	}
	public int GetPlayerID()
	{
		return PlayerID;
	}
}
