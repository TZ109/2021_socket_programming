package frame;

import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;

import actionListener.*;
import domain.*;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

public class AmbulanceClientJFrame extends JFrame{

	final int HospitalCnt = 5;
	final int EmergencyKingCnt = 8;

	//클라이언트
	AmbulanceClientSocket client;
	AmbulanceInfo ambulanceInfo;


	SelectionButton selectedName;
	List<EmergcyBtn> hospitals = new Vector<>();

	JButton[] emKindArry = new JButton[EmergencyKingCnt];

	JPanel main = new JPanel();
	JPanel sub = new JPanel();
	JPanel list = new JPanel();
	JPanel btnList = new JPanel();

	RegisterWindow registerWindow;
	SelectEmergencyDepartActionListener selectEmergencyDepartActionListener;
	SelectEmergencyTypeActionListener selectEmergencyTypeActionListener;
	MenuItemActionListener menuItemActionListener;
	ControlActionListener controlActionListener;
	SocketSendData sendData;
	LoginFrame loginFrame;



	public AmbulanceClientJFrame() {

		super();
		ambulanceInfo = new AmbulanceInfo("NULL", 0, 0);
		PersonalInfo.getInstance().setAmbulanceName(ambulanceInfo.getName());
		selectedName = new SelectionButton();
		client = new AmbulanceClientSocket(hospitals, selectedName);
		sendData = new SocketSendData(client, ambulanceInfo, selectedName);

		registerWindow = new RegisterWindow(client, sendData, selectedName);
		selectEmergencyDepartActionListener = new SelectEmergencyDepartActionListener(hospitals, client, sendData, selectedName);
		selectEmergencyTypeActionListener = new SelectEmergencyTypeActionListener(emKindArry, client, selectedName, sendData);
		loginFrame = new LoginFrame(this, client, sendData, ambulanceInfo);
		LoginInfo.getInstance().setLoginFrame(loginFrame);
		LoginInfo.getInstance().setMainFrame(this);
		menuItemActionListener = new MenuItemActionListener(client, ambulanceInfo, sendData, this, loginFrame);
		controlActionListener = new ControlActionListener(registerWindow, hospitals, emKindArry,client, selectedName, sendData);
		setLocationRelativeTo(null);
		registerWindow.setVisible(false);


		//JFrame Init

		guiInit();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		setSize(700, 500);
		setVisible(false);

		setTitle("응급실 상황판");
		setResizable(false);


		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}
	
	public void guiInit()
	{
		Container ct = this.getContentPane();
		TitledBorder title = new TitledBorder("병원 목록");
		JButton rstBtn = new  JButton("초기화");
		JButton recvBtn = new JButton("도착");
		JButton regBtn = new JButton("등록");
		JMenu mainMenu = new JMenu("Main");
		JMenuItem menuExit = new JMenuItem("Exit");
		JMenuItem menuInfo = new JMenuItem("Info");
		JMenu editMenu = new JMenu("Edit");
		JMenuItem setPosition = new JMenuItem("Set Position");
		JMenuBar jMenuBar = new JMenuBar();

		menuExit.setName("exit");
		menuInfo.setName("info");
		setPosition.setName("setPos");


		rstBtn.setPreferredSize(new Dimension(100, 40));
		recvBtn.setPreferredSize(new Dimension(100, 40));
		regBtn.addActionListener(controlActionListener);
		rstBtn.addActionListener(controlActionListener);
		recvBtn.addActionListener(controlActionListener);
		menuExit.addActionListener(menuItemActionListener);
		menuInfo.addActionListener(menuItemActionListener);
		setPosition.addActionListener(menuItemActionListener);

		mainMenu.add(menuExit);
		mainMenu.add(menuInfo);
		editMenu.add(setPosition);

		jMenuBar.add(mainMenu);
		jMenuBar.add(editMenu);

		setJMenuBar(jMenuBar);


		sub.setLayout(new BorderLayout());
		sub.setBorder(new LineBorder(Color.gray, 1, true));
		sub.add(rstBtn, BorderLayout.WEST);
		sub.add(recvBtn, BorderLayout.EAST);
		sub.add(regBtn, BorderLayout.CENTER);

		main.setLayout(new GridLayout(1, 2, 20, 0));
		list.setLayout(new GridLayout(HospitalCnt,1));

		for(int i =0; i<HospitalCnt; i++){
			hospitals.add(new EmergcyBtn());
			hospitals.get(i).btn.addActionListener(selectEmergencyDepartActionListener);
			hospitals.get(i).btn.setBorder(new BevelBorder(BevelBorder.RAISED));
			hospitals.get(i).btn.setEnabled(false);
			list.add(hospitals.get(i).btn);

		}


		btnList.setLayout(new GridLayout(4, 2));
		list.setBorder(BorderFactory.createTitledBorder(title));

		for(int i =0; i<EmergencyKingCnt; i++){
			{
				emKindArry[i] = new JButton();
				emKindArry[i].addActionListener(selectEmergencyTypeActionListener);

				switch(i)
				{
					case 0:
						emKindArry[i].setText("[중환자실] 일반");
						break;
					case 1:
						emKindArry[i].setText("[중환자실] 내과");
						break;
					case 2:
						emKindArry[i].setText("[중환자실] 외과");
						break;
					case 3:
						emKindArry[i].setText("[중환자실] 음압격리");
						break;
					case 4:
						emKindArry[i].setText("[중환자실] 신경과");
						break;
					case 5:
						emKindArry[i].setText("[중환자실] 흉부외과");
						break;
					case 6:
						emKindArry[i].setText("[중환자실] 신생아");
						break;
					case 7:
						emKindArry[i].setText("[중환자실] 화상");
						break;
				}

			}
			btnList.add(emKindArry[i]);
		}

		ct.setLayout(new BorderLayout());
		ct.add(sub, BorderLayout.NORTH);
		main.add(btnList);
		main.add(list);

		ct.add(main, BorderLayout.CENTER);
	}

}
