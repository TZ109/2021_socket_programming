package domain;

import domain.type.BloodType1;
import domain.type.BloodType2;
import domain.type.SexType;

public class PersonalInfo {
	private static PersonalInfo personalInfo = new PersonalInfo();

	private SexType sex;
	private BloodType1 bloodType1;
	private BloodType2 bloodType2;
	private String AmbulanceName;
	String others;
	private Boolean isFilled = false;

	private PersonalInfo(){}

	public static PersonalInfo getInstance(){
		return personalInfo;
	}

	public void clearInfo(){
		this.setSex(SexType.Male);
		this.setBloodType1(BloodType1.RH_PLUS);
		this.setBloodType2(BloodType2.A);
		this.setOthers("");
		this.isFilled = false;
	}

	@Override
	public String toString(){
		String info = "";

		if(isFilled == false){
			info = ";";
		}
		else {
			info =  "." +sex + "."
					+ bloodType1 + "."
					+ bloodType2 + "."
					+ others
					+";";
		}
		return info;
	}

	public void setAmbulanceName(String name){
		this.AmbulanceName = name;
	}

	public void setIsFilled() {
		this.isFilled = true;
	}

	public boolean getFilled() {
		return this.isFilled;
	}

	public static PersonalInfo getPersonalInfo() {
		return personalInfo;
	}

	public static void setPersonalInfo(PersonalInfo personalInfo) {
		PersonalInfo.personalInfo = personalInfo;
	}

	public SexType getSex() {
		return sex;
	}

	public void setSex(SexType sex) {
		this.sex = sex;
	}

	public BloodType1 getBloodType1() {
		return bloodType1;
	}

	public void setBloodType1(BloodType1 bloodType1) {
		this.bloodType1 = bloodType1;
	}

	public BloodType2 getBloodType2() {
		return bloodType2;
	}

	public void setBloodType2(BloodType2 bloodType2) {
		this.bloodType2 = bloodType2;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

}
