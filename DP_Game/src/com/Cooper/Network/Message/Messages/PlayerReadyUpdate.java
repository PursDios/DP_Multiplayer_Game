package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class PlayerReadyUpdate extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5166362179891063850L;
	int _PlayerID;
	boolean _PlayerReady;
	public PlayerReadyUpdate(MessageType type, int playerid, boolean ready) 
	{
		super(type);
		_PlayerID = playerid;
		_PlayerReady = ready;
	}
	
	public int GetPlayerID()
	{
		return _PlayerID;
	}
	
	public boolean GetReady()
	{
		return _PlayerReady;
	}
}
