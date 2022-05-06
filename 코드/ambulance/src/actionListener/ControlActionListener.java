package actionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import domain.EmergcyBtn;
import domain.PersonalInfo;
import domain.RequestType;
import domain.SelectionButton;
import frame.RegisterWindow;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

public class ControlActionListener implements ActionListener {
	RegisterWindow window;
	JButton[] emKindArry;
	List<EmergcyBtn>hospitals;
	AmbulanceClientSocket client;
	SelectionButton selectionButton;
	SocketSendData socketSendData;

	Boolean flag = false; //초기화가 진행되었을 떄 True

	public ControlActionListener(RegisterWindow window, List<EmergcyBtn> hospitals, JButton[] emKindArry, AmbulanceClientSocket client, SelectionButton selectedName, SocketSendData sendData) {
		this.window = window;
		this.emKindArry = emKindArry;
		this.hospitals = hospitals;
		this.client = client;
		this.selectionButton = selectedName;
		this.socketSendData = sendData;
	}

	private void btnClear(){
		for (JButton jButton : emKindArry) {
			jButton.setBackground(Color.WHITE);
		}

		for (EmergcyBtn hospital : hospitals) {
			hospital.btn.setBackground(Color.WHITE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton eventBtn = (JButton)e.getSource();
		switch (eventBtn.getText()){

			case "초기화":
				System.out.println("Reset eventBtn.getText() = " + eventBtn.getText());
				int result = JOptionPane.showConfirmDialog(null, "정말 초기화 하시겠습니까?");
				if(result == JOptionPane.CLOSED_OPTION);
				else if (result == JOptionPane.YES_OPTION) {
					PersonalInfo.getInstance().clearInfo();
					btnClear();
					selectionButton.clear();
					RequestType.getInstance().setClear();
					socketSendData.sendClearMessage();
					flag = true;
				}else
				break;
			case "등록":
				System.out.println("Reg eventBtn.getText() = " + eventBtn.getText());
				if(flag) {
					window.clearInfo();
					flag = false;
				} else {
					window.setVisible(true);
				}
				break;

			case "도착":
				System.out.println("eventBtn.getText() = " + eventBtn.getText());
				for (EmergcyBtn hospital : hospitals) {
					if(hospital.info.getSelected()){
						socketSendData.sendRecvMessage();
						hospital.info.setSelected(false);
						selectionButton.clear();
						RequestType.getInstance().setClear();
						break;
					}
				}
				PersonalInfo.getInstance().clearInfo();
				btnClear();
				break;
			default:
				System.out.println("Unknown Button");
		}
	}
}
