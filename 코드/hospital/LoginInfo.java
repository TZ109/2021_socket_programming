
public class LoginInfo {
	LoginFrame loginFrame;
	MyJFrame mainFrame;

	private static final LoginInfo loginInfo = new LoginInfo();

	public static LoginInfo getInstance() {
		return loginInfo;}


	public void setLoginFrame(LoginFrame loginFrame) {
		this.loginFrame = loginFrame;
	}

	public LoginFrame getLoginFrame() {
		return loginFrame;
	}

	public MyJFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(MyJFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
}
