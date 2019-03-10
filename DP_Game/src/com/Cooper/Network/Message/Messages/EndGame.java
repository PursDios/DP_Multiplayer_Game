package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class EndGame extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4955437351959565017L;
	private String _WinningPlayer;
	public EndGame(MessageType type, String name) 
	{
		super(type);
		_WinningPlayer = name;
	}
	
	public String GetWinningPlayer()
	{
		return _WinningPlayer;
	}
}
