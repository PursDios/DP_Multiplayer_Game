package com.Cooper.Control;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
