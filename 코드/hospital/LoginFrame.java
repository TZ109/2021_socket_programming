import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
	JButton connectBtn;
	JButton cancelBtn;

	LoginACationListener loginACationListener;

	JTextField hospitalName;
	JTextField hospitalXPosition;
	JTextField hospitalYPosition;
	JTextField serverTextField;

	JLabel hospitalNameLabel;
	JLabel hospitalXposLabel;
	JLabel hospitalYposLabel;
	JLabel serverLabel;

	Container ct;
	JPanel labelPanel = new JPanel();
	JPanel textFieldPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JPanel mainPanel = new JPanel();

	MyJFrame mainFrame;
	MyJFrame client;

	HospitalInfo hospitalInfo;

	public LoginFrame(MyJFrame mainFrame, HospitalInfo hospitalInfo) {
		super();
		this.mainFrame = mainFrame;
		this.client = client;
		this.hospitalInfo = hospitalInfo;
		ct = this.getContentPane();
		connectBtn = new JButton("연결");
		hospitalName = new JTextField();
		hospitalXPosition = new JTextField();
		hospitalYPosition = new JTextField();
		serverTextField = new JTextField("localhost:5001");

		hospitalNameLabel = new JLabel("병원 이름 입력");
		hospitalXposLabel = new JLabel("X좌표 입력 (정수)");
		hospitalYposLabel = new JLabel("Y좌표 입력 (정수)");
		serverLabel = new JLabel("서버주소:포트");
		hospitalNameLabel.setPreferredSize(new Dimension(120, 20));
		

		hospitalNameLabel.setHorizontalAlignment(JLabel.CENTER);
		hospitalXposLabel.setHorizontalAlignment(JLabel.CENTER);
		hospitalYposLabel.setHorizontalAlignment(JLabel.CENTER);
		serverLabel.setHorizontalAlignment(JLabel.CENTER);

		loginACationListener = new LoginACationListener(mainFrame, this, hospitalInfo);

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
		labelPanel.add(hospitalNameLabel);
		labelPanel.add(hospitalXposLabel);
		labelPanel.add(hospitalYposLabel);

		textFieldPanel.add(serverTextField);
		textFieldPanel.add(hospitalName);
		textFieldPanel.add(hospitalXPosition);
		textFieldPanel.add(hospitalYPosition);

		buttonPanel.add(connectBtn);

		mainPanel.add(labelPanel, BorderLayout.WEST);
		mainPanel.add(textFieldPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		ct.add(mainPanel);



	}

	public void clear() {
		this.hospitalName.setText("");
		this.hospitalXPosition.setText("");
		this.hospitalYPosition.setText("");
	}

	public String getHospitalName(){
		return this.hospitalName.getText();
	}

	public String getAmbulanceXpos(){
		return this.hospitalXPosition.getText();
	}

	public String getAmbulanceYPos(){
		return this.hospitalYPosition.getText();
	}

	public String getServerTextField() {
		return serverTextField.getText();
	}
}
