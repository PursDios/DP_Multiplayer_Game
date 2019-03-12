package com.Cooper.Network.Message.Messages;

import java.io.Serializable;
import java.util.ArrayList;

import com.Cooper.Network.Message.MessageTypes.MessageType;

/**
 * @author PursDios
 * @version 1.0
 */
public class CurrentPlayers extends NetworkMessage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1944857097835197882L;
	ArrayList<Integer> _OtherPlayerIds;
	ArrayList<String> _OtherPlayerNames;
	ArrayList<String> _OtherPlayerCarLocs;
	ArrayList<Boolean> _OtherPlayerReady;
	
	public CurrentPlayers(MessageType type, ArrayList<Integer> ids, ArrayList<String> names, ArrayList<String> carlocs, ArrayList<Boolean> playerready) 
	{
		super(type);
		_OtherPlayerIds = ids;
		_OtherPlayerNames = names;
		_OtherPlayerCarLocs = carlocs;
		_OtherPlayerReady = playerready;
	}
	
	public ArrayList<Integer> GetIds()
	{
		return _OtherPlayerIds;
	}
	
	public ArrayList<String> GetNames()
	{
		return _OtherPlayerNames;
	}
	
	public ArrayList<String> GetCarLocs()
	{
		return _OtherPlayerCarLocs;
	}
	
	public ArrayList<Boolean> GetReady()
	{
		return _OtherPlayerReady;
	}
}
