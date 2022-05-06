package domain;

import javax.swing.*;

public class EmergcyBtn {
	public EmergencyInfo info;
	public JButton btn;
	public boolean isValid = false;



	public EmergcyBtn() {
		info = new EmergencyInfo();
		btn = new JButton();
		btn.setText(info.toString());
	}

	public String getHospitalName(){
		return this.info.name;
	}

	public void setHospitalName(String name){
		this.info.name = name;
	}



	public void setHospitalInfo(String name, int accepted, int incoming,  int empty){
		info.name = name;
		info.incoming = incoming;
		info.accepted = accepted;
		info.empty = empty;
	}

	public void setHospitalInfo(String name, int xPos, int yPos){
		info.name = name;
		info.xPos = xPos;
		info.yPos = yPos;
	}

	public void upDateHospitalInfo(int accepted, int incoming, int empty){
//		info.name = name;
		info.accepted = accepted;
		info.incoming = incoming;
		info.empty = empty;
	}

}
