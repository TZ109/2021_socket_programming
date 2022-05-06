

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.nio.channels.UnresolvedAddressException;


public class ServerMain {

	public static void main(String[] args) {
		AsyncServer server = new AsyncServer();
		ServerLoginFrame serverLoginFrame = new ServerLoginFrame(server);
	}

}


class ServerLoginActionListener implements ActionListener {

	private JTextField portTextField;
	private JTextField addrTextField;
	private JButton startBtn;
	private JButton stopBtn;
	private AsyncServer asyncServer;

	public ServerLoginActionListener(AsyncServer asyncServer, JTextField portTextField, JTextField addrTextField, JButton startBtn, JButton stopBtn) {
		this.asyncServer = asyncServer;
		this.portTextField = portTextField;
		this.addrTextField = addrTextField;
		this.startBtn = startBtn;
		this.stopBtn = stopBtn;
	}

	public void setLoginMode(){
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);
		portTextField.setEnabled(false);
		addrTextField.setEnabled(false);
	}

	public void setLogoutMode(){
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		portTextField.setEnabled(true);
		addrTextField.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();

		switch (btn.getName()) {
			case "start":
				if(checkValidField()){
					try{
						asyncServer.startServer();
						Thread.sleep(100);
						if (asyncServer.isServerOpen()) {
							setLoginMode();
						}
					}
					catch (UnresolvedAddressException addressException){
						JOptionPane.showMessageDialog(null, "주소 형식 오류", "주소 오류", JOptionPane.ERROR_MESSAGE);
						setLogoutMode();
					}

					catch (SocketException exception){
						JOptionPane.showMessageDialog(null, exception.toString(), "소켓 오류", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception exception){
						JOptionPane.showMessageDialog(null, exception.toString(), "오류", JOptionPane.ERROR_MESSAGE);
						if(asyncServer.isServerOpen())
							asyncServer.stopServer();
						setLogoutMode();
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "옵션값이 잘못되었습니다.", "포트 오류", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "stop":
				try{
					setLogoutMode();
					asyncServer.stopServer();
				}
				catch (Exception ignored){}
				break;


			default:
				System.out.println("알 수 없는 버튼");
				break;

		}
	}

	public boolean checkValidField() {
		try {
			ServerAddressInformation.getInstance().setIpAddr(addrTextField.getText());
			ServerAddressInformation.getInstance().setPort(Integer.parseInt(portTextField.getText()));
			if (ServerAddressInformation.getInstance().getInetSocketAddress() != null) {
				return true;
			}
		} catch (Exception ignored){}
		return false;
	}

}

class ServerLoginFrame extends JFrame {
	JButton startBtn;
	JButton stopBtn;
	JTextField portTextField;
	JTextField serverAddressTextField;

	ServerLoginActionListener serverLoginActionListener;
	AsyncServer asyncServer;
	JLabel portLabel;
	JLabel serverLabel;

	Container ct;
	JPanel labelPanel = new JPanel();
	JPanel textFieldPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel mainPanel = new JPanel();

	public ServerLoginFrame(AsyncServer asyncServer) {
		super();
		this.asyncServer = asyncServer;
		ct = this.getContentPane();
		startBtn = new JButton("서버 시작");
		stopBtn = new JButton("서버 종료");
		portTextField = new JTextField("5001");
		serverAddressTextField = new JTextField("localhost");

		this.serverLoginActionListener = new ServerLoginActionListener(asyncServer, portTextField, serverAddressTextField, startBtn, stopBtn);
		startBtn.addActionListener(serverLoginActionListener);
		stopBtn.addActionListener(serverLoginActionListener);
		stopBtn.setEnabled(false);
		portLabel = new JLabel("포트 번호");
		serverLabel = new JLabel("서버주소");
		portLabel.setPreferredSize(new Dimension(120, 20));


		portLabel.setHorizontalAlignment(JLabel.CENTER);
		serverLabel.setHorizontalAlignment(JLabel.CENTER);

		startBtn.setName("start");
		stopBtn.setName("stop");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiInit();
		setResizable(false);
		setVisible(true);

	}

	private void guiInit() {
		setSize(400, 150);
		this.setTitle("서버 시작");
		mainPanel.setLayout(new BorderLayout());
		labelPanel.setLayout(new GridLayout(2, 1));
		textFieldPanel.setLayout(new GridLayout(2, 1));
		buttonPanel.setLayout(new GridLayout(1, 2));

		startBtn.setPreferredSize(new Dimension(10, 50));

		labelPanel.add(serverLabel);
		labelPanel.add(portLabel);

		textFieldPanel.add(serverAddressTextField);
		textFieldPanel.add(portTextField);

		buttonPanel.add(startBtn);
		buttonPanel.add(stopBtn);

		mainPanel.add(labelPanel, BorderLayout.WEST);
		mainPanel.add(textFieldPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		ct.add(mainPanel);
	}

	public JButton getStartBtn() {
		return startBtn;
	}

	public JButton getStopBtn() {
		return stopBtn;
	}

	public JTextField getPortTextField() {
		return portTextField;
	}

	public JTextField getServerAddressTextField() {
		return serverAddressTextField;
	}
}
