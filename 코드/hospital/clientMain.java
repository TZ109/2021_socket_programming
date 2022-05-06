public class clientMain {

	public static void main(String[] args) {
		HospitalInfo hospitalInfo = new HospitalInfo(null, 0, 0);
		MyJFrame hospitalMain = new MyJFrame(hospitalInfo);
		LoginFrame loginFrame = new LoginFrame(hospitalMain, hospitalInfo);
	}


}
