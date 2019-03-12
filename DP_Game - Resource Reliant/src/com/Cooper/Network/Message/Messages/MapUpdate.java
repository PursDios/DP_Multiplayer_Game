package com.Cooper.Network.Message.Messages;

import java.io.Serializable;

import com.Cooper.Network.Message.MessageTypes.MessageType;

public class MapUpdate extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4404175012029313409L;
	private String _MapName, _MapLocation;
	
	public MapUpdate(MessageType type, String mapname, String maplocation) 
	{
		super(type);
		_MapName = mapname;
		_MapLocation = maplocation;
	}
	public String GetMapLocation()
	{
		return _MapLocation;
	}
	public String GetMapName()
	{
		return _MapName;
	}
}
