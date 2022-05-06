import java.net.InetSocketAddress;

public class ServerAddressInformation {

	private String ipAddr;
	private int port;

	private static final ServerAddressInformation addressInformation = new ServerAddressInformation();

	public static ServerAddressInformation getInstance() {
		return addressInformation;
	}

	public ServerAddressInformation() {
		this.ipAddr = "localhost";
		this.port = 5001;
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