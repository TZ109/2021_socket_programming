package domain;

import java.net.InetSocketAddress;

public class SocketInfo {

	private String ipAddr;
	private int port;
	private boolean welcomeMessage = false;

	private static final SocketInfo socketInfo = new SocketInfo();

	public static SocketInfo getInstance() {
		return socketInfo;
	}

	public SocketInfo() {
		this.ipAddr = "localhost";
		this.port = 5001;
	}

	public boolean isWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(boolean welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
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
