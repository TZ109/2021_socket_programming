package actionListener;

import domain.AmbulanceInfo;
import util.SocketSendData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PositionSetActionListener implements ActionListener {
	JTextField xTextField;
	JTextField yTextField;
	AmbulanceInfo ambulanceInfo;
	JFrame jFrame;
	SocketSendData socketSendData;

	public PositionSetActionListener(JTextField xTextField, JTextField yTextField, AmbulanceInfo ambulanceInfo, JFrame jFrame, SocketSendData socketSendData) {
		this.xTextField = xTextField;
		this.yTextField = yTextField;
		this.ambulanceInfo = ambulanceInfo;
		this.jFrame = jFrame;
		this.socketSendData = socketSendData;


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		switch (btn.getName()) {
			case "confirm":
				try {
					ambulanceInfo.setPos(Integer.parseInt(xTextField.getText()), Integer.parseInt(xTextField.getText()));
					socketSendData.sendSetPosition();
				} catch (Exception ignored) {}
				finally {
					jFrame.dispose();
				}
				break;
			case "cancel":
				jFrame.dispose();
				break;
			default:
				System.out.println("[info] 알수 없는 버튼");
				break;
		}
	}
}
