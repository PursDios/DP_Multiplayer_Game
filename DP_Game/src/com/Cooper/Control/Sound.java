package com.Cooper.Control;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class Sound 
{
	private static Sound _instance;
	private Clip _crash;
	
	private Sound()
	{
		try 
		{
			_crash = AudioSystem.getClip();
		} 
		catch (LineUnavailableException e) 
		{
			e.printStackTrace();
		}
		AudioInputStream _crashStream = ResourceLoader.loadAudio("SoundFiles/Crash.wav");
		try
		{
			_crash.open(_crashStream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Sound getInstance()
	{
		if(_instance == null)
			_instance = new Sound();
		return _instance;
	}
	
	public void PlayCrash()
	{
		if(!_crash.isRunning())
		{
			_crash.setFramePosition(0);
			_crash.start();
		}
	}
}
