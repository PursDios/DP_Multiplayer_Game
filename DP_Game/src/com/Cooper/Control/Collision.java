package com.Cooper.Control;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.Cooper.Game.Checkpoint;
import com.Cooper.Game.Map;
import com.Cooper.Game.Player;
/**
 * 
 * @author Ryan Cooper
 * @version 1.0
 */
public class Collision 
{
	//The map being played on
	private Map _Map;
	//The black and white map (Map Mask)
	private BufferedImage _BlackWhiteMap;
	//The Path2D (outline) of the Map
	private Path2D _MapPath;
	
	private boolean Finished=false;
	
	/**
	 * Constructor for the Collision Manager (this)
	 * @param map: The map being used to play the game
	 */
	public Collision(Map map)
	{
		_Map = map;
		CreateMasks();
	}
	
	/**
	 * Calls the methods that will create masks.
	 */
	private void CreateMasks()
	{
		MakeMapMask(Color.BLACK, 20);
	}
	/**
	 * Creates a mask based on an image (for the map)
	 * @param target: The target colour (e.g. black)
	 * @param tolerance: The tolerance range of the color (e.g. within 20 red intensity)
	 */
	private void MakeMapMask(Color target, int tolerance)
	{
		//Loads the maps image into a local variable map
		BufferedImage map = _Map.getMapImage();
		
		//sets _BlackWhiteMap to be the same size as map and sets it to be a binary image (0's and 1's only)
		_BlackWhiteMap = new BufferedImage(map.getWidth(),map.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
		
		//for each pixel (width)
		for(int x=0; x<map.getWidth(); x++)
		{
			//for each pixel (height)
			for(int y=0; y<map.getHeight(); y++)
			{
				//pixel is the colour of the pixel at x,y
				Color pixel = new Color(map.getRGB(x, y));
				//set write's colour as black
				Color write = Color.black;
				//if the result of checkpixel is true
				if(CheckPixel(pixel, target, tolerance))
				{
					//set the pixel colour to white
					write = Color.white;
				}
				//write the colour of the pixel to _blackwhitemap.
				_BlackWhiteMap.setRGB(x,y,write.getRGB());
			}
		}
		//Creates a path from the black and white mask (An outline of the black and white mask) 
        _MapPath = getOutline(Color.BLACK, _BlackWhiteMap);
	}
	
	/**
	 *https://stackoverflow.com/questions/22391353/get-color-of-each-pixel-of-an-image-using-bufferedimages
	 * Checks the colour of the pixel 
	 * @param pixel: the pixel being checked
	 * @param target: the target colour
	 * @param tolerance: the tolerance number
	 * @return returns true or false depending on if the pixel meets the conditions. 
	 */
	private boolean CheckPixel(Color pixel, Color target, int tolerance)
	{
		//gets the red value of the target colour
		int targetRed = target.getRed();
		//gets the green value of the target colour
		int targetGreen = target.getGreen();
		//gets the blue value of the target colour
		int targetBlue = target.getBlue();
		
		//gets the red value of the actual image
		int pixelRed = pixel.getRed();
		//gets the green value of the actual image
		int pixelGreen = pixel.getGreen();
		//gets the blue value of the actual image
		int pixelBlue = pixel.getBlue();
		
		//if the pixel is in the correct range for R(red) G(green) and B(blue)
		if((pixelRed - tolerance <= targetRed) && (pixelGreen - tolerance <= targetGreen) && (pixelGreen - tolerance <= targetGreen) 
		&& (targetRed <= pixelRed+tolerance) && (targetGreen <= pixelGreen+tolerance) && (targetBlue <= pixelBlue+tolerance))
		{
			return true;
		}
		else
			return false;
	}
	/**
	 * Creates a mask from an image (intended for the car)
	 * @param car: the image of the car.
	 * @param target: the target colour.
	 * @param tolerance: the tolerance of the colour.
	 * @return
	 */
	public BufferedImage MakeCarMask(BufferedImage car, Color target, int tolerance)
	{
		//Make a new buffered image and set it to be the same size as the car image.
        BufferedImage _BlackWhiteCar = new BufferedImage(car.getWidth(),car.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
        
        //for each x cord on the car
        for(int x=0; x<car.getWidth(); x++)
		{
        	//for each y cord on the car
			for(int y=0; y<car.getHeight(); y++)
			{
				//get the pixel color at that cord
				Color pixel = new Color(car.getRGB(x, y));
				//set the color we are going to write to black (default)
				Color write = Color.black;
				//Check the pixel (taking into account the target and tolerance range)
				if(CheckPixel(pixel, target, tolerance))
				{
					//if it meets the conditions set the pixel colour to white.
					write = Color.white;
				}
				//set the pixel of the bufferedimage at the cord to that colour.
				_BlackWhiteCar.setRGB(x,y,write.getRGB());
			}
		}
        //return the black and white mask we made.
        return _BlackWhiteCar;
	}
	/**
	 * Creates a Path2D from the mask provided.
	 * @param mask the black and white mask of the car.
	 * @return The path2D for the car.
	 */
	public Path2D MakeCarPath(BufferedImage mask)
	{
		Path2D _CarPath = getOutline(Color.WHITE, mask);
		return _CarPath;
	}
	
	/*
	 * https://stackoverflow.com/questions/7218309/smoothing-a-jagged-path
	 * Return GeneralPath instead of Area because GP allows for the comparison of
	 * two different GP's meanwhile you cannot do the same for area.
	 */
	public GeneralPath getOutline(Color target, BufferedImage bi) 
	{
        // construct the GeneralPath
        GeneralPath path = new GeneralPath();

        boolean validColour = false;
        int targetRGB = target.getRGB();
        //for each pixel (width)
        for (int x=0; x<bi.getWidth(); x++) 
        {
        	//for each pixel (height)
            for (int y=0; y<bi.getHeight(); y++) 
            {
            	//If the colour matches the target Colour (White or Black)
                if (bi.getRGB(x,y)==targetRGB) 
                {
                	//If the colour is valid and the starting position set.
                    if (validColour) 
                    {
                    	//Draw a line to x/y
                        path.lineTo(x,y);
                        //draw a line next to x/y
                        path.lineTo(x+1,y);
                        //Apparently a x,y+1 isn't needed.
                    } 
                    else 
                    {
                        path.moveTo(x,y);
                    }
                    
                    /*
                     * First Valid move will be used to set the starting position
                     * The Final move will finish this 
                     * OR the closePath() will ensure that the first move is still counted in the path
                     */
                    validColour = true;
                } 
                else 
                {
                	//if colour is not the same as the target. set the valid colour to false.
                    validColour = false;
                }
            }
            //resets valid colour to false.
            validColour = false;
        }
        //Finishes the path (connects it back to the starting location.
        path.closePath();
        return path;
    }
	/**
	 * Checks if the player has had an impact
	 * @param p: The player requesting the check
	 * @param x: The supposed new position of the car (x)
	 * @param y: The supposed new position of the car (y)
	 * @param angle: The angle of the car
	 * @param OtherPlayers: All the other players currently in the game.
	 * @return true or false based on if there was an impact.
	 */
	public boolean CheckImpact(Player p, int x, int y, int angle, ArrayList<Player> OtherPlayers)
	{
		boolean ImpactMap=false, ImpactCars= false;;
		
		//Checks if the player has hit a wall
		ImpactMap = CheckWallImpact(p, x, y, angle);
		//Checks if the player has hit another car.
		ImpactCars = CheckCarsImpact(p, x, y, angle, OtherPlayers);
		//Checks to see if the player has passed a checkpoint.
		CheckpointCheck(p);
		if(ImpactMap || ImpactCars)
			return true;
		else
			return false;
	}
	
	/**
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/geom/Path2D.Double.html
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/geom/PathIterator.html
	 *https://stackoverflow.com/questions/8290232/how-to-check-for-shape-intersection-using-generalpath-java
	 *https://docs.oracle.com/javase/7/docs/api/java/awt/geom/GeneralPath.html
	 * Checks if the car/player has hit a wall.
	 * @param p: player requesting the check
	 * @param x: The players supposed new position of the car (x)
	 * @param y: The players supposed new position of the car (y)
	 * @param angle: The angle of the car
	 * @return true or false based on if there was an impact.
	 */
	@SuppressWarnings("static-access")
	private boolean CheckWallImpact(Player p, int x, int y, int angle)
	{
		AffineTransform at = new AffineTransform();
		//readability
		Path2D path = p.GetPlayerCar().GetPath();
		
		if(p.GetPlayerCar().GetPath() != null)
		{
			//rotate the car 
			at.rotate(angle, path.getBounds2D().getCenterX(), path.getBounds2D().getCenterY());
			
			//turn the maps path into a PathIterator (required for the intersects method)
			PathIterator PI = _MapPath.getPathIterator(null);
			/*
			 * If the path of the car (outline) intersects with the PathIterator (outline)
			 * of the map then return true/false
			 * 
			 * Note: x / 1.3 and y /1.3 is to account for the scaling done.
			 * any other numbers are just to correctly align the mask with the map (outliers)
			 * Please note these outliers will be consistant across all maps.d
			 */
			return path.intersects(PI, (int)(x / 4) + 7, (int)(y / 4) + 7, 1, 1);
		}
		return false;
	}
	
	/**
	 * https://tutorialedge.net/gamedev/aabb-collision-detection-tutorial/
	 * Checks if the car/player has hit another player
	 * @param p: the player requesting the check
	 * @param Newx: the supposed new position of the car (x)
	 * @param Newy: the supposed new position of the car (y)
	 * @param angle: the angle of the car.
	 * @param OtherPlayers: All of the other players.
	 * @return true or false based on if there was an impact.
	 */
	private boolean CheckCarsImpact(Player p, int Newx, int Newy, int angle, ArrayList<Player> OtherPlayers)
	{
		//for each player
		for(int i=0; i<OtherPlayers.size(); i++)
		{	
			//set the width and height of the player requesting.
			int w = p.GetPlayerCar().GetVehicleLabel().getWidth();
			int h = p.GetPlayerCar().GetVehicleLabel().getHeight();
			
			//if the players id isn't the car requesting (don't want it comparing with itself).
			if(p.GetPlayerCar().GetCarID() != OtherPlayers.get(i).GetPlayerCar().GetCarID())
			{
				//set the x y width and height of the other player (readability)
				int x2 = OtherPlayers.get(i).GetPlayerCar().GetVehicleLabel().getX();
				int y2 = OtherPlayers.get(i).GetPlayerCar().GetVehicleLabel().getY();
				int w2 = OtherPlayers.get(i).GetPlayerCar().GetVehicleLabel().getWidth();
				int h2 = OtherPlayers.get(i).GetPlayerCar().GetVehicleLabel().getHeight();
				
				//if they intersect.
				if(Newx < x2 + w2 && Newx + w > x2 && Newy < y2 + h2 && Newy + h > y2)
				{
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * https://tutorialedge.net/gamedev/aabb-collision-detection-tutorial/
	 * Checks if the player has collided with a checkpoint.
	 * @param p The player being checked.
	 */
	private void CheckpointCheck(Player p)
	{
		//get the players x y width and height
		int x = p.GetPlayerCar().GetVehicleLabel().getX();
		int y = p.GetPlayerCar().GetVehicleLabel().getY();
		int w = p.GetPlayerCar().GetVehicleLabel().getWidth();
		int h = p.GetPlayerCar().GetVehicleLabel().getHeight();
		
		//for each checkpoint
		for(Checkpoint c : _Map.GetCheckPoints())
		{
			//if they intersect
			if(x < c.getX() + c.getWidth() && x + w > c.getX() && y < c.getY() + c.getHeight() && y + h > c.getY())
			{
				//if the checkpoint number is 1
				if(c.GetNumber() == 1)
					//set the number of checkpoints the user has passed to.
					p.SetCheckPointsPassed(1);
				//if the checkpoint number is 0
				if(c.GetNumber() == 0)
				{
					//and the player has already passed the other checkpoint
					if(p.GetCheckPointsPassed() == 1)
					{
						//the player wins.
						if(!Finished)
						{
							Controller.getInstance().PlayerWins(p);
							Controller.getInstance().FinishGame(p.GetPlayerName());
							Finished = true;
						}
					}
				}
			}
		}
	}
}

