package actionListener;

import domain.EmergcyBtn;
import domain.PersonalInfo;
import domain.RequestType;
import domain.SelectionButton;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SelectEmergencyDepartActionListener implements ActionListener {
	List<EmergcyBtn> btnList;
	AmbulanceClientSocket client;
	SocketSendData socketSendData;
	SelectionButton selectedName;



	public SelectEmergencyDepartActionListener(List<EmergcyBtn> btnList, AmbulanceClientSocket client, SocketSendData sendData, SelectionButton selectedName) {
		this.btnList = btnList;
		this.client = client;
		this.socketSendData = sendData;
		this.selectedName = selectedName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for(EmergcyBtn obj : btnList)
		{
			if(e.getSource() == obj.btn)
			{
				System.out.println("Clicked : " + obj.btn.getText());
				obj.btn.setBackground(Color.RED);
				obj.info.setSelected(true);
				selectedName.setSelectedHospital(obj.getHospitalName());
				selectedName.setType(RequestType.getInstance().getType());
				System.out.println(selectedName.getSelectedHospital());
				socketSendData.sendSelectedHospital();
			}
			else{
				obj.btn.setBackground(Color.WHITE);
				obj.info.setSelected(false);
			}
		}
	}
}