package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class NewPlayerConnected extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6839033617569444751L;
	private int PlayerID;
	
	public NewPlayerConnected(MessageType type, int id) 
	{
		super(type);
		PlayerID = id;
	}
	
	public int GetPlayerID()
	{
		return PlayerID;
	}
}
