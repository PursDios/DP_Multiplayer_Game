package com.Cooper.UI.Menus;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.Cooper.Control.Controller;

public class ConnectMenu extends JPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//container for all the ui elements of the form
	private JPanel _Container;
	//the connect button
	private JButton _Connect;
	//the ip address text box
	private JTextField _Textbox;
	//the text label
	private JLabel _ConnectLabel;
	
	public ConnectMenu()
	{
		this.setLayout(null);
		this.setVisible(true);
		//loads the item settings
		ItemSettings();
		//loads the panel settings
		PanelSettings();
		//adds the ui elements to the panel
		add(_Container);
	}
	
	/**
	 * Sets the settings for all the UI elements
	 */
	private void ItemSettings()
	{
		//ui settings for the connect button
		_Connect = new JButton("Connect");
		_Connect.setSize(180, 50);
		_Connect.setLocation(50, 50);
		_Connect.setFont(new Font("Consolas",Font.BOLD,14));
		_Connect.setBackground(Color.WHITE);
		_Connect.addActionListener(this);
		
		//ui settings for the text label
		_ConnectLabel = new JLabel("Status: ");
		_ConnectLabel.setSize(180, 50);
		_ConnectLabel.setLocation(60, 85);
		_ConnectLabel.setFont(new Font("Consolas",Font.BOLD,10));
		_ConnectLabel.setBackground(Color.WHITE);
		_ConnectLabel.setVisible(false);
		
		//ui settings for the text box.
		_Textbox = new JTextField();
		_Textbox.setSize(180, 20);
		_Textbox.setLocation(50, 10);
		_Textbox.setFont(new Font("Consolas",Font.BOLD,14));
		_Textbox.setBackground(Color.WHITE);
		_Textbox.setText("127.0.0.1");
		
		_Textbox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(_Textbox.getText().contains("127.0.0.1"))
					_Textbox.setText("");
			}

		});
	}
	
	/**
	 * Sets the panel settings for this menu
	 */
	private void PanelSettings()
	{
		_Container = new JPanel();
		_Container.setBounds(0, 0, 400, 300);
		_Container.setLayout(null);
		//adds all of the ui elements to the container before making them visible.
		_Container.add(_Textbox);
		_Container.add(_ConnectLabel);
		_Container.add(_Connect);
		_Container.setVisible(true);
	}
	
	/**
	 * if an action is performed.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		//if the button is pressed
		if(ae.getSource().getClass() == JButton.class)
		{
			System.out.println("Connect Called");
			_ConnectLabel.setText("Status: Connecting...");
			_ConnectLabel.setVisible(true);
			//attempt to connect to the server.
			boolean connected = Controller.getInstance().Connect(_Textbox.getText());
			if(!connected)
				_ConnectLabel.setText("Server Full");
		}
	}
}
