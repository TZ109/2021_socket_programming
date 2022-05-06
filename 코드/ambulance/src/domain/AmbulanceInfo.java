package domain;

public class AmbulanceInfo {
	private String name;
	private int xPos;
	private int yPos;

	public void setInfo(String name, int xPos, int yPos) {
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public AmbulanceInfo(String name, int xPos, int yPos) {
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public String getInfo() {
		return "차랑이름 : " + this.name +
				"  현재 위치 정보 :  " + this.xPos +
				" , " + this.yPos;
	}

	@Override
	public String toString() {
		return "AmbulanceInfo{" +
				"name='" + name + '\'' +
				", xPos=" + xPos +
				", yPos=" + yPos +
				'}';
	}

	public void clear(){
		this.name = "NULL";
		this.xPos = 0;
		this.yPos = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getxPos() {
		return xPos;
	}

	public void setPos(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public int getyPos() {
		return yPos;
	}

}
