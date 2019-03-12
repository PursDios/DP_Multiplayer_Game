package com.Cooper.Network.Message;

/**
 * @author PursDios
 * @version 1.1
 */
public class MessageTypes
{
	//https://dzone.com/articles/using-java-enums
	//https://www.geeksforgeeks.org/enum-in-java/
	public enum MessageType
	{
		HELLO, CONNECTION_ACCEPT, PLAYER_POS_UPDATE, MAP_UPDATE, START_GAME, NEW_PLAYER_CONNECTED, CURRENT_PLAYERS, PLAYER_READY_UPDATE, PLAYER_CAR_UPDATE, PLAYER_NAME_UPDATE, CHAT_MESSAGE, END_GAME, DISCONNECT;
	}
}
