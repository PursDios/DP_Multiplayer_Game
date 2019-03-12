package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

/**
 * @author PursDios
 * @version 1.0
 */
public class PlayerNameUpdate extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6849109318960539615L;
	private int _PlayerID;
	private String _PlayerName;
	public PlayerNameUpdate(MessageType type, int playerid, String name) 
	{
		super(type);
		_PlayerID = playerid;
		_PlayerName = name;
	}
	public int GetPlayerID()
	{
		return _PlayerID;
	}
	public String GetPlayerName()
	{
		return _PlayerName;
	}
}
