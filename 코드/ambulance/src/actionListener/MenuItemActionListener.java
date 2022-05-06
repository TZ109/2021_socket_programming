package actionListener;

import domain.AmbulanceInfo;
import domain.SocketInfo;
import frame.AmbulanceClientJFrame;
import frame.LoginFrame;
import frame.SetPositionFrame;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemActionListener implements ActionListener {

	AmbulanceClientSocket client;
	AmbulanceInfo ambulanceInfo;
	SocketSendData socketSendData;
	AmbulanceClientJFrame mainFrame;
	LoginFrame loginFrame;

	public MenuItemActionListener(AmbulanceClientSocket client, AmbulanceInfo ambulanceInfo, SocketSendData socketSendData, AmbulanceClientJFrame ambulanceClientJFrame, LoginFrame loginFrame) {
		this.client = client;
		this.ambulanceInfo = ambulanceInfo;
		this.socketSendData = socketSendData;
		this.mainFrame = ambulanceClientJFrame;
		this.loginFrame = loginFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JMenuItem btn = (JMenuItem) e.getSource();

		switch (btn.getName()){
			case "exit":
				client.stopClient();
				mainFrame.setVisible(false);
				ambulanceInfo.clear();
				loginFrame.clear();
				loginFrame.setVisible(true);
				SocketInfo.getInstance().setWelcomeMessage(false);
				break;

			case "info":
				JOptionPane.showMessageDialog(null,
						ambulanceInfo.getInfo(),
						"차량 정보",
						JOptionPane.INFORMATION_MESSAGE);
				break;

			case "setPos":
				new SetPositionFrame(ambulanceInfo, socketSendData);
				break;

			default:
				System.out.println("[INFO] 등록되지 않은 버튼");
				break;
		}

	}
}
