package domain;

import frame.AmbulanceClientJFrame;
import frame.LoginFrame;

public class LoginInfo {
	LoginFrame loginFrame;
	AmbulanceClientJFrame mainFrame;

	private static final LoginInfo loginInfo = new LoginInfo();

	public static LoginInfo getInstance() {
		return loginInfo;}


	public void setLoginFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	public LoginFrame getLoginFrame() {
		return loginFrame;
	}

	public AmbulanceClientJFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(AmbulanceClientJFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
