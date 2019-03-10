package com.Cooper.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import com.Cooper.Game.Player;

/**
 * @author Ryan Cooper
 * @version 1.0
 */
public class VehicleLabel extends JLabel
{
	private static final long serialVersionUID = 1L;
	//The player associated with this vehiclelabel.
	private Player _Player;
	
	/**
	 * The constructor for the VehicleLabel
	 * @param p The player associated with this VehicleLabel
	 */
	public VehicleLabel(Player p)
	{
		_Player = p;
	}
	/**
	 * Set the player associated with this VehicleLabel
	 * @param p The Player associated with this vehicle label.
	 */
	public void SetPlayer(Player p)
	{
		_Player = p;
	}
	/**
	 * returns the player
	 * @return The player associated with this VehicleLabel
	 */
	public Player GetPlayer()
	{
		return _Player;
	}
	/**
	 * Sets the player associated with this VehicleLabel to the one sent
	 * @param p: The Player associated with this vehicle label.
	 */
	public void Initalise(Player p)
	{
		_Player = p;
	}
	
	/**
	 * rotates the object before painting it.
	 */
	@Override
	public void paintComponent(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		
		int x = getWidth() / 2;
		int y = getHeight() / 2;
		
		//Sets the settings of the g2d.
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		
		//rotates the image (plus 90 to make upright instead of slanted).
		g2d.rotate(_Player.GetPlayerCar().getAngle() + Math.toRadians(90), x, y);
		
		super.paintComponent(g);
	}
}
