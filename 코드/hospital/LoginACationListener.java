
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.UnresolvedAddressException;

public class LoginACationListener implements ActionListener {
	MyJFrame mainFrame;
	LoginFrame loginFrame;
	HospitalInfo hospitalInfo;
	MyJFrame.MyAsyncClient client;

	public LoginACationListener(MyJFrame mainFrame, LoginFrame loginFrame, HospitalInfo hospitalInfo) {
		this.mainFrame = mainFrame;
		this.loginFrame = loginFrame;
		this.hospitalInfo = hospitalInfo;
		this.client = mainFrame.client;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();

		switch (btn.getName()) {
			case "connect":
				if(setSocket() && checkValid()){
					try {
						client.startClient();
						Thread.sleep(100);
						if (client.isChannelOpen()) {
							client.send("Client.TYPE:1."+hospitalInfo.getName()+"."+hospitalInfo.getxPos()+"."+hospitalInfo.getyPos()+";");
							Thread.sleep(100);
							if(SocketInfo.getInstance().isWelcomeMessage()) {
								mainFrame.setSubtitle();
								mainFrame.setVisible(true);
								loginFrame.setVisible(false);
							}
							else{
								JOptionPane.showMessageDialog(null, "서버와의 연결에 실패했습니다.", "서버 닫힘", JOptionPane.ERROR_MESSAGE);
							}
						} else{
							JOptionPane.showMessageDialog(null, "서버와의 연결에 실패했습니다.", "서버 오류", JOptionPane.ERROR_MESSAGE);
						}
					} catch(UnresolvedAddressException addressException){
						JOptionPane.showMessageDialog(null, "주소 형식 잘못되었습니다.", "주소 오류", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception ignored) {}
				} else{
					JOptionPane.showMessageDialog(null, "옵션값이 잘못되었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "cancel":
				try {
					if (client.isChannelOpen()) {
						client.stopClient();
					}
				}catch (NullPointerException exception){
					return ;
				}
				break;

			default:
				System.out.println("[INFO] 알수 없는 버튼");
				break;
		}
	}

	public boolean checkValid(){
		try{
			hospitalInfo.setPos(Integer.parseInt(loginFrame.getAmbulanceXpos()), Integer.parseInt(loginFrame.getAmbulanceYPos()));
			hospitalInfo.setName(loginFrame.getHospitalName());
		}catch(Exception exception){
			return false;
		}

		return true;
	}

	public boolean setSocket() {
		try {
			String[] info = loginFrame.getServerTextField().split(":");
			SocketInfo.getInstance().setIpAddr(info[0]);
			SocketInfo.getInstance().setPort(Integer.parseInt(info[1]));
			if (SocketInfo.getInstance().getInetSocketAddress() != null) {
				return true;
			}
		} catch (Exception ignored) {}
		return false;
	}

}
