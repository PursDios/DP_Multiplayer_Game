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
	private JPanel _Container;
	private JButton _Connect;
	private JTextField _Textbox;
	private JLabel _ConnectLabel;
	
	public ConnectMenu()
	{
		this.setLayout(null);
		this.setVisible(true);
		ItemSettings();
		PanelSettings();
		add(_Container);
	}
	
	private void ItemSettings()
	{
		
		_Connect = new JButton("Connect");
		_Connect.setSize(180, 50);
		_Connect.setLocation(50, 50);
		_Connect.setFont(new Font("Consolas",Font.BOLD,14));
		_Connect.setBackground(Color.WHITE);
		_Connect.addActionListener(this);
		
		_ConnectLabel = new JLabel("Status: ");
		_ConnectLabel.setSize(180, 50);
		_ConnectLabel.setLocation(60, 85);
		_ConnectLabel.setFont(new Font("Consolas",Font.BOLD,10));
		_ConnectLabel.setBackground(Color.WHITE);
		_ConnectLabel.setVisible(false);
		
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
	
	private void PanelSettings()
	{
		_Container = new JPanel();
		_Container.setBounds(0, 0, 400, 300);
		_Container.setLayout(null);
		_Container.add(_Textbox);
		_Container.add(_ConnectLabel);
		_Container.add(_Connect);
		_Container.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		// TODO Auto-generated method stub
		if(ae.getSource().getClass() == JButton.class)
		{
			System.out.println("Connect Called");
			_ConnectLabel.setText("Status: Connecting...");
			_ConnectLabel.setVisible(true);
			Controller.getInstance().Connect(_Textbox.getText());
		}
	}
}
