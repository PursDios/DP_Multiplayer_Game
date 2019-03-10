package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class ChatMessage extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3246190560055504874L;
	private int _PlayerID;
	private String _Message;
	public ChatMessage(MessageType type, int playerid, String message) 
	{
		super(type);
		_PlayerID = playerid;
		_Message = message;
	}
	
	public int GetPlayerID()
	{
		return _PlayerID;
	}
	
	public String GetMessage()
	{
		return _Message;
	}
}
