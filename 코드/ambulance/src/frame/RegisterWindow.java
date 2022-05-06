package frame;

import domain.EmergcyBtn;
import domain.PersonalInfo;
import domain.SelectionButton;
import domain.type.BloodType1;
import domain.type.BloodType2;
import domain.type.SexType;
import socket.AmbulanceClientSocket;
import util.SocketSendData;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterWindow extends JDialog {
	AmbulanceClientSocket client;
	SocketSendData sendData;
	JButton registerBtn;
	JTextArea othersTextF = new JTextArea(12, 28);
	JRadioButton maleSel = new JRadioButton("남자");
	JRadioButton femaleSel = new JRadioButton("여자");
	JRadioButton rhPlus = new JRadioButton("RH+");
	JRadioButton rhMinus = new JRadioButton("RH-");
	JRadioButton blood_A = new JRadioButton("A");
	JRadioButton blood_B = new JRadioButton("B");
	JRadioButton blood_AB = new JRadioButton("AB");
	JRadioButton blood_O = new JRadioButton("O");
	ButtonGroup BloodGroup1 = new ButtonGroup();
	ButtonGroup BloodGroup2 = new ButtonGroup();
	ButtonGroup sexGroup = new ButtonGroup();
	SelectionButton selectionName;

	public RegisterWindow(AmbulanceClientSocket client, SocketSendData sendData, SelectionButton selectionName){
		super();
		super.setTitle("신상 등록");
		this.client = client;
		this.sendData = sendData;
		registerBtn = new JButton("등록");
		registerBtn.setPreferredSize(new Dimension(350, 100));
		init();
		clearInfo();
		setSize(350, 600);;
		registerBtn.addActionListener(new RegisterActionListener());
		this.selectionName = selectionName;
	}

	private void init(){
		Container ct = this.getContentPane();
		JPanel main  = new JPanel();
		JPanel sub1 = new JPanel();
		JPanel part1 = new JPanel();
		JPanel part2 = new JPanel();
		JPanel sexPart = new JPanel();
		JPanel sexSelPanel = new JPanel();



		JScrollPane scrollPane = new JScrollPane(othersTextF);
		othersTextF.setEditable(true);

		JLabel sexLabel = new JLabel("성별");
		JLabel bloodLabel = new JLabel("혈액형");
		bloodLabel.setHorizontalAlignment(JLabel.CENTER);
		sexLabel.setHorizontalAlignment(JLabel.CENTER);

		sexGroup.add(maleSel);
		sexGroup.add(femaleSel);
		BloodGroup1.add(rhPlus);
		BloodGroup1.add(rhMinus);
		BloodGroup2.add(blood_A);
		BloodGroup2.add(blood_B);
		BloodGroup2.add(blood_AB);
		BloodGroup2.add(blood_O);

		JPanel bloodPart = new JPanel();
		JPanel bloodSelPanel = new JPanel();

		JPanel bPart1 = new JPanel();
		JPanel bPart2 = new JPanel();

		bPart1.setBorder(new BevelBorder(BevelBorder.RAISED));

		bPart1.setLayout(new GridLayout(1, 2));
		bPart2.setLayout(new GridLayout(2, 2));

		bPart1.add(rhPlus);
		bPart1.add(rhMinus);

		bPart2.add(blood_A);
		bPart2.add(blood_B);
		bPart2.add(blood_AB);
		bPart2.add(blood_O);

		bloodSelPanel.setLayout(new GridLayout(2, 1));
		bloodSelPanel.add(bPart1);
		bloodSelPanel.add(bPart2);

		main.setLayout(new BorderLayout());
		sub1.setLayout(new GridLayout(2, 1));

		sexPart.setLayout(new BorderLayout());
		sexSelPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
		sexSelPanel.setLayout(new GridLayout(1, 2));
		sexSelPanel.add(maleSel);
		sexSelPanel.add(femaleSel);


		sexPart.add(sexLabel, BorderLayout.WEST);
		sexLabel.setPreferredSize(new Dimension(70, 100));
		sexLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		sexPart.add(sexSelPanel, BorderLayout.CENTER);

		bloodPart.setLayout(new BorderLayout());
		bloodPart.add(bloodLabel, BorderLayout.WEST);
		bloodLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
		bloodLabel.setPreferredSize(new Dimension(70, 100));
		bloodPart.add(bloodSelPanel, BorderLayout.CENTER);
		bloodSelPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

		part1.setLayout(new GridLayout(2, 1));
		part1.add(sexPart);
		part1.add(bloodPart);

		part2.setBorder(new TitledBorder("특이사항"));
		part2.add(scrollPane);

		sub1.add(part1);
		sub1.add(part2);

		main.add(registerBtn, BorderLayout.SOUTH);
		main.add(sub1, BorderLayout.CENTER);
		ct.add(main);

	}

	public void clearInfo(){
		maleSel.setSelected(true);
		rhPlus.setSelected(true);
		blood_A.setSelected(true);
		othersTextF.setText("");
	}


	class RegisterActionListener implements ActionListener {

		public void bloodType1(){
			if( rhPlus.isSelected()){
				PersonalInfo.getInstance().setBloodType1(BloodType1.RH_PLUS);
			}else{
				PersonalInfo.getInstance().setBloodType1(BloodType1.RH_MINUS);
			}
		}

		public void bloodType2(){
			if(blood_A.isSelected()){
				PersonalInfo.getPersonalInfo().setBloodType2(BloodType2.A);
			} else if (blood_B.isSelected()) {
				PersonalInfo.getPersonalInfo().setBloodType2(BloodType2.B);
			} else if (blood_AB.isSelected()) {
				PersonalInfo.getPersonalInfo().setBloodType2(BloodType2.AB);
			} else {
				PersonalInfo.getPersonalInfo().setBloodType2(BloodType2.O);
			}
		}

		public void sexType(){
			if(maleSel.isSelected()){
				PersonalInfo.getPersonalInfo().setSex(SexType.Male);
			} else{
				PersonalInfo.getPersonalInfo().setSex(SexType.Female);
			}
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			bloodType1();
			bloodType2();
			sexType();
			PersonalInfo.getPersonalInfo().setOthers(othersTextF.getText());
			PersonalInfo.getPersonalInfo().setIsFilled();
//			System.out.println("PersonalInfo.getInstance().getBloodType2() = " + PersonalInfo.getInstance().getBloodType2());
//			System.out.println("PersonalInfo.getInstance().getSex() = " + PersonalInfo.getInstance().getSex());

			if(selectionName.getSelectedHospital().isEmpty());
			else sendData.sendSelectedHospital();
			setVisible(false);
		}
	}


}
