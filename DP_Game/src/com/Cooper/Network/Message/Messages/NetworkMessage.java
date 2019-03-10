package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2915128673270443786L;
	MessageType _MessageType;
	int _SenderID;
	
	public NetworkMessage(MessageType type)
	{
		_MessageType = type;
	}
	
	public MessageType GetMessageType()
	{
		return _MessageType;
	}
}
