package domain;

public class EmergencyInfo {
	protected int incoming;
	protected int accepted;
	protected int empty;
	private Boolean isSelected = false;
	protected String name;
	protected int xPos;
	protected int yPos;
	protected double distance;
	private Boolean isValid = false;

	public EmergencyInfo() {
		this.name = "NULL";
		incoming = 0;
		accepted = 0;
		empty = 0;
		xPos = 0;
		yPos = 0;
		distance = 0;
	}

	public EmergencyInfo(String name) {
		this.name = name;
		incoming = 0;
		accepted = 0;
		empty = 0;
		xPos = 0;
		yPos = 0;
		distance = 0;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public boolean getIsValid() {
		return this.isValid;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}


	public EmergencyInfo(String name, int xPos, int yPos) {
		this.name = name;
		incoming = 0;
		accepted = 0;
		empty = 0;
		this.xPos = xPos;
		this.yPos = xPos;
	}

	public EmergencyInfo(int incoming, int accepted, int empty, String name, int xPos, int yPos) {
		this.incoming = incoming;
		this.accepted = accepted;
		this.empty = empty;
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public Boolean getSelected() {
		return isSelected;
	}

	public void setSelected(Boolean selected) {
		isSelected = selected;
	}

	public String getName(){
		return this.name;
	}

	public void setEmergencyInfo(String name, int xPos, int yPos, int incoming, int ableCnt, int totalCnt){
		this.incoming = incoming;
		this.accepted = ableCnt;
		this.empty = totalCnt;
		this.name = name;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public void setEmergencyInfo( int incoming, int ableCnt, int totalCnt){
		this.incoming = incoming;
		this.accepted = ableCnt;
		this.empty = totalCnt;
	}

	double getDistance(int x, int y, int dstX, int dstY){
		return Math.sqrt(Math.pow(Math.abs(x-dstX), 2) + Math.pow(Math.abs(x-dstY),2));
	}

	@Override
	public String toString() {

		String str = "["+this.name+"]" + " 수용인원: " + accepted + "   이송 중: " + incoming + "   빈 병상 " + empty;
		return str;
	}
}
