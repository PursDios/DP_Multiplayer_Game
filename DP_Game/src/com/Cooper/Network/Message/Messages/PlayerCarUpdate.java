package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

/**
 * @author PursDios
 * @version 1.0
 */
public class PlayerCarUpdate extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -738824577418126143L;
	private String _CarLocation;
	private int _PlayerID;
	public PlayerCarUpdate(MessageType type, int playerid, String carlocation) 
	{
		super(type);
		_PlayerID = playerid;
		_CarLocation = carlocation;
	}
	public int GetPlayerID()
	{
		return _PlayerID;
	}
	public String GetCarLocation()
	{
		return _CarLocation;
	}
}
