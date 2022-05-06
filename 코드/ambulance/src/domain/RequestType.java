package domain;

public class RequestType {

	private static RequestType requestType = new RequestType();
	private int type = -1;

	private RequestType() {
	}

	static public RequestType getInstance() {
		return requestType;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String strType){
		this.type = convertNameToID(strType);
	}

	public void setClear() {
		this.type = -1;
	}

	private int convertNameToID(String name) {
		int code = -1;
		switch (name) {
			case "[중환자실] 일반":
				code = 0;
				break;
			case "[중환자실] 내과":
				code = 1;
				break;
			case "[중환자실] 외과":
				code = 2;
				break;
			case "[중환자실] 음압격리":
				code = 3;
				break;
			case "[중환자실] 신경과":
				code = 4;
				break;
			case "[중환자실] 흉부외과":
				code = 5;
				break;
			case "[중환자실] 신생아":
				code = 6;
				break;
			case "[중환자실] 화상":
				code = 7;
				break;
			default:
				code = -1;
				System.out.println("Unknown Name :" + name);
		}
		return code;
	}
}
