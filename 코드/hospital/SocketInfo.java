
import java.net.InetSocketAddress;

public class SocketInfo {

	private String ipAddr;
	private int port;

	private static final SocketInfo socketInfo = new SocketInfo();
	private boolean welcomeMessage = false;


	public static SocketInfo getInstance() {
		return socketInfo;
	}

	public SocketInfo() {
		this.ipAddr = "localhost";
		this.port = 5001;
	}

	public void setWelcomeMessage(boolean welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public boolean isWelcomeMessage() {
		return welcomeMessage;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public InetSocketAddress getInetSocketAddress() {
		try {
			 return  new InetSocketAddress(this.ipAddr, this.port);
		} catch (Exception ignored) {
			return null;
		}
	}
}
