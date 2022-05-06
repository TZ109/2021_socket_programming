package frame;

import actionListener.LoginACationListener;
import domain.AmbulanceInfo;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
	JButton connectBtn;
	JButton cancelBtn;

	LoginACationListener loginACationListener;

	JTextField ambulanceName;
	JTextField ambulanceXPosition;
	JTextField ambulanceYPosition;
	JTextField serverTextField;

	JLabel ambulanceNameLabel;
	JLabel ambulanceXposLabel;
	JLabel ambulanceYposLabel;
	JLabel serverLabel;

	Container ct;
	JPanel labelPanel = new JPanel();
	JPanel textFieldPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel mainPanel = new JPanel();

	AmbulanceClientJFrame mainFrame;
	AmbulanceClientSocket client;
	SocketSendData socketSendData;
	AmbulanceInfo ambulanceInfo;

	public LoginFrame(AmbulanceClientJFrame mainFrame, AmbulanceClientSocket client, SocketSendData socketSendData, AmbulanceInfo ambulanceInfo) {
		super();
		this.mainFrame = mainFrame;
		this.client = client;
		this.socketSendData = socketSendData;
		this.ambulanceInfo = ambulanceInfo;
		ct = this.getContentPane();
		connectBtn = new JButton("연결");
		ambulanceName = new JTextField();
		ambulanceXPosition = new JTextField();
		ambulanceYPosition = new JTextField();
		serverTextField = new JTextField("localhost:5001");

		ambulanceNameLabel = new JLabel("차량번호 입력 (숫자)");
		ambulanceXposLabel = new JLabel("X좌표 입력 (정수)");
		ambulanceYposLabel = new JLabel("Y좌표 입력 (정수)");
		serverLabel = new JLabel("서버주소:포트");
		ambulanceNameLabel.setPreferredSize(new Dimension(120, 20));
		

		ambulanceNameLabel.setHorizontalAlignment(JLabel.CENTER);
		ambulanceXposLabel.setHorizontalAlignment(JLabel.CENTER);
		ambulanceYposLabel.setHorizontalAlignment(JLabel.CENTER);
		serverLabel.setHorizontalAlignment(JLabel.CENTER);

		loginACationListener = new LoginACationListener(mainFrame, this, client, socketSendData, ambulanceInfo);

		connectBtn.addActionListener(loginACationListener);

		connectBtn.setName("connect");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiInit();
		setResizable(false);
		setVisible(true);

	}

	private void guiInit() {
		setSize(400, 200);
		this.setTitle("서버 연결");
		mainPanel.setLayout(new BorderLayout());
		labelPanel.setLayout(new GridLayout(4, 1));
		textFieldPanel.setLayout(new GridLayout(4, 1));
		buttonPanel.setLayout(new GridLayout(1, 1));

		connectBtn.setPreferredSize(new Dimension(10, 50));

		labelPanel.add(serverLabel);
		labelPanel.add(ambulanceNameLabel);
		labelPanel.add(ambulanceXposLabel);
		labelPanel.add(ambulanceYposLabel);

		textFieldPanel.add(serverTextField);
		textFieldPanel.add(ambulanceName);
		textFieldPanel.add(ambulanceXPosition);
		textFieldPanel.add(ambulanceYPosition);

		buttonPanel.add(connectBtn);

		mainPanel.add(labelPanel, BorderLayout.WEST);
		mainPanel.add(textFieldPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		ct.add(mainPanel);



	}

	public void clear() {
		this.ambulanceName.setText("");
		this.ambulanceXPosition.setText("");
		this.ambulanceYPosition.setText("");
	}

	public String getAmbulanceName(){
		return this.ambulanceName.getText();
	}

	public String getAmbulanceXpos(){
		return this.ambulanceXPosition.getText();
	}

	public String getAmbulanceYPos(){
		return this.ambulanceYPosition.getText();
	}

	public String getServerTextField() {
		return serverTextField.getText();
	}
}
