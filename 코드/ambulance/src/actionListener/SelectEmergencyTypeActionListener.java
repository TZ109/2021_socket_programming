package actionListener;

import domain.SelectionButton;
import socket.AmbulanceClientSocket;
import domain.RequestType;
import util.SocketSendData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectEmergencyTypeActionListener implements ActionListener {
	JButton[] btnList;
	AmbulanceClientSocket client;
	SelectionButton selectionName;
	SocketSendData socketSendData;

	public SelectEmergencyTypeActionListener(JButton[] btnList, AmbulanceClientSocket client, SelectionButton selectionName, SocketSendData sendData) {
		this.btnList = btnList;
		this.client = client;
		this.selectionName = selectionName;
		this.socketSendData = sendData;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(JButton obj : btnList)
		{
			if(e.getSource() == obj)
			{
				System.out.println("Clicked : " + obj.getText());
				obj.setBackground(Color.GRAY);
				RequestType.getInstance().setType(obj.getText());
				socketSendData.sendRequestHospitalInfo();

			}
			else{
				obj.setBackground(Color.WHITE);
			}
		}
	}

}
