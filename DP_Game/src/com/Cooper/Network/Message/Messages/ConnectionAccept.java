package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class ConnectionAccept extends NetworkMessage implements Serializable
{
	private static final long serialVersionUID = -6267197333606682275L;
	/**
	 * 
	 */
	int _PlayerID;
	String _MapName, _MapLocation;
	
	public ConnectionAccept(MessageType type, int id,String mapname, String maplocation) 
	{
		super(type);
		_PlayerID = id;
		_MapName = mapname;
		_MapLocation = maplocation;
	}

	public int GetPlayerID()
	{
		return _PlayerID;
	}
	
	public String GetMapName()
	{
		return _MapName;
	}
	
	public String GetMapLocation()
	{
		return _MapLocation;
	}
}
