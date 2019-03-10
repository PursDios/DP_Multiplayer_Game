package com.Cooper.Game;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JLabel;

/**
 * @author Ryan Cooper
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Checkpoint extends JLabel
{
	//the width of the checkpoint (thickness)
	int TrackSize = 20;
	//the default track height/width (the other factor of the checkpoint)
	int TrackHeightWidth= 100;
	//the position of the checkpoint.
	Point _Position;
	//the checkpoint number.
	int CheckpointNum;
	
	/**
	 * The Constructor for the checkpoint.
	 * @param num The checkpoint number.
	 */
	public Checkpoint(int num)
	{
		//sets the checkpoint number 
		CheckpointNum = num;
		//creates the point object.
		_Position = new Point();
		//sets the checkpoint to visible.
		this.setVisible(true);
		//sets the size (this SHOULD be updated later).
		this.setSize(TrackSize,TrackHeightWidth);
		//the background of the checkpoint (dark gray to stand out on the track without being an eye-sore)
		this.setBackground(Color.DARK_GRAY);
		//sets it to opaque so it's actually visible.
		this.setOpaque(true);
	}
	
	/**
	 * Returns the checkpoint number
	 * @return integer checkpoint number
	 */
	public int GetNumber()
	{
		return CheckpointNum;
	}
	/**
	 * Sets the TrackHeightWidth and updates the size of the checkpoint
	 * @param trackHW the TrackHeightWidth of the track
	 */
	public void SetTrackHeightWidth(int trackHW)
	{
		//sets the TrackHeightWidth
		TrackHeightWidth = trackHW;
		//Updates the checkpoint with the new size.
		this.setSize(TrackSize,TrackHeightWidth);
	}
	/**
	 * Sets the position of the track
	 * @param pos Int array containing the x and y cords of the checkpoint (in 0 and 1st elements)
	 */
	public void SetPosition(int[] pos)
	{
		//updates x with pos 0.
		_Position.x = pos[0];
		//updates y with pos 1.
		_Position.y = pos[1];
		//sets the location of the checkpoint.
		this.setLocation((int)_Position.getX(), (int)_Position.getY());
	}
	/**
	 * Reutns the position of the checkpoint.
	 * @return returns a point with the x and y positions of the checkpoint.
	 */
	public Point GetPosition()
	{
		return _Position;
	}
}
