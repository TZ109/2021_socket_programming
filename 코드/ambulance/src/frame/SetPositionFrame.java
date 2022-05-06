package frame;

import actionListener.MenuItemActionListener;
import actionListener.PositionSetActionListener;
import domain.AmbulanceInfo;
import util.SocketSendData;

import javax.swing.*;
import java.awt.*;

public class SetPositionFrame extends JFrame {

	Container container =  this.getContentPane();
	JPanel main = new JPanel();
	JPanel gridPanel = new JPanel();
	JPanel gridPanel2 = new JPanel();
	JPanel gridPanel3 = new JPanel();
	JButton confirmBtn = new JButton("확인");
	JButton cancelBtn = new JButton("취소");
	JLabel xPosLabel = new JLabel("X 좌표");
	JLabel yPosLabel = new JLabel("Y 좌표");
	JTextField xPosTextField = new JTextField();
	JTextField yPosTextField = new JTextField();
	PositionSetActionListener positionActionListen;
	AmbulanceInfo ambulanceInfo;
	SocketSendData socketSendData;

	public SetPositionFrame(AmbulanceInfo ambulanceInfo, SocketSendData socketSendData) {
		super();
		this.ambulanceInfo = ambulanceInfo;
		this.socketSendData = socketSendData;
		init();

	}

	private void init() {
		setSize(300, 100);
		setLocationRelativeTo(null);
		setResizable(false);
		positionActionListen = new PositionSetActionListener(xPosTextField, yPosTextField, ambulanceInfo, this, socketSendData);
		confirmBtn.setName("confirm");
		cancelBtn.setName("cancel");

		confirmBtn.addActionListener(positionActionListen);
		cancelBtn.addActionListener(positionActionListen);

		xPosLabel.setPreferredSize(new Dimension(70, 100));
		yPosLabel.setPreferredSize(new Dimension(70, 100));
		xPosLabel.setHorizontalAlignment(JLabel.CENTER);
		yPosLabel.setHorizontalAlignment(JLabel.CENTER);

		xPosTextField.setPreferredSize(new Dimension(30, 30));
		yPosTextField.setPreferredSize(new Dimension(30, 30));

		main.setLayout(new BorderLayout());
		gridPanel.setLayout(new GridLayout(1, 2));
		gridPanel2.setLayout(new GridLayout(2, 1));
		gridPanel3.setLayout(new GridLayout(2, 1));

		gridPanel.add(cancelBtn);
		gridPanel.add(confirmBtn);

		gridPanel2.add(xPosLabel);
		gridPanel2.add(yPosLabel);

		gridPanel3.add(xPosTextField);
		gridPanel3.add(yPosTextField);

		main.add(gridPanel2, BorderLayout.WEST);
		main.add(gridPanel3, BorderLayout.CENTER);
		main.add(gridPanel, BorderLayout.SOUTH);


		container.add(main);

		setVisible(true);

	}

}