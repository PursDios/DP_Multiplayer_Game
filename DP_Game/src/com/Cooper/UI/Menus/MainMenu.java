package com.Cooper.UI.Menus;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.Cooper.Control.Controller;

public class MainMenu extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//container for all the ui elements.
	private JPanel _Container2;
	//the connect and host buttons
	private JButton _Connect, _Host;
	
	public MainMenu()
	{
		setLayout(null);
		ItemSettings();
		PanelSettings();
		add(_Container2);
		this.setVisible(true);
	}
	
	/**
	 * if an action is performed.
	 */
	@Override
	public void actionPerformed(ActionEvent a) 
	{
		//if a button is pressed.
		if(a.getSource().getClass() == JButton.class)
		{
			//casts the action event as a button.
			JButton temp = (JButton)a.getSource();
			
			//if the text of the button is...
			switch(temp.getText())
			{
			//if it's connect
				case "Connect":
					System.out.println("Connect");
					//calls the loadconnect method in the controller.
					Controller.getInstance().LoadConnect();
					break;
			//if it's host
				case "Host":
					System.out.println("Host");
					//calls the mutliplayerloadlobby method in the controller.
					Controller.getInstance().MultiplayerLoadLobby(true);
					break;
			}
		}
		
	}
	
	/**
	 * sets all of the ui elements settings
	 */
	private void ItemSettings()
	{
		_Connect = new JButton("Connect");
		_Connect.setSize(300, 75);
		_Connect.setLocation(50, 10);
		_Connect.setFont(new Font("Consolas",Font.BOLD,14));
		_Connect.setBackground(Color.WHITE);
		_Connect.addActionListener(this);
		
		_Host = new JButton("Host");
		_Host.setSize(300, 75);
		_Host.setLocation(50, 100);
		_Host.setFont(new Font("Consolas",Font.BOLD,14));
		_Host.setBackground(Color.WHITE);
		_Host.addActionListener(this);
	}
	/**
	 * Sets the panel settings for the menu
	 */
	private void PanelSettings()
	{
		_Container2 = new JPanel();
		_Container2.setBounds(0, 0, 400, 300);
		_Container2.setLayout(null);
		//adds all of the ui elements to the container before settings them as visible.
		_Container2.add(_Connect);
		_Container2.add(_Host);
		_Container2.setVisible(true);
	}
}
