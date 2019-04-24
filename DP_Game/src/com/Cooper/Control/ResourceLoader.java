package com.Cooper.Control;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author PursDios
 * @version 1.0
 */
final public class ResourceLoader 
{
	/**
	 * gets a resource as an inputstream
	 * @param path the path of the thing you are trying to access
	 * @return the inputstream of that file.
	 */
	public static InputStream load(String path)
	{
		InputStream input = ResourceLoader.class.getResourceAsStream(path);
		if(input == null)
			input = ResourceLoader.class.getResourceAsStream("/" + path);
		return input;
	}
	/**
	 * gets a resource as a bufferedreader (used to read text files)
	 * @param path the path of the file
	 * @return returns the 
	 */
	public static BufferedReader loadFile(String path)
	{
		return new BufferedReader(new InputStreamReader(ResourceLoader.class.getResourceAsStream(path)));
	}
	
	public static AudioInputStream loadAudio(String path)
	{
		InputStream input = ResourceLoader.load(path);
		InputStream buffer = new BufferedInputStream(input);
		try 
		{
			return AudioSystem.getAudioInputStream(buffer);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		} 
	}
}
