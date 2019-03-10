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
	private JPanel _Container2;
	private JButton _Connect, _Host;
	
	public MainMenu()
	{
		setLayout(null);
		ItemSettings();
		PanelSettings();
		add(_Container2);
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent a) 
	{
		if(a.getSource().getClass() == JButton.class)
		{
			JButton temp = (JButton)a.getSource();
			
			switch(temp.getText())
			{
			case "Connect":
				System.out.println("Connect");
				Controller.getInstance().LoadConnect();
				break;
			case "Host":
				System.out.println("Host");
				Controller.getInstance().MultiplayerLoadLobby(true);
				break;
			}
		}
		
	}
	
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
	
	private void PanelSettings()
	{
		
		_Container2 = new JPanel();
		_Container2.setBounds(0, 0, 400, 300);
		_Container2.setLayout(null);
		_Container2.add(_Connect);
		_Container2.add(_Host);
		_Container2.setVisible(true);
	}
}
