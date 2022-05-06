package domain;

public class SelectionButton {
	private String selectedHospital;
	private int type;

	public SelectionButton() {
		this.selectedHospital = "";
		this.type = -1;
	}

	public String getSelectedHospital() {
		return selectedHospital;
	}

	public void setSelectedHospital(String selectedHospital) {
		this.selectedHospital = selectedHospital;
	}

	public int getType() {
		return type;
	}


	//selectionButton.setSelectedHospital("");
	//					selectionButton.setType(-1);

	public void clear() {
		setSelectedHospital("");
		setType(-1);

	}

	public void setType(int type) {
		this.type = type;
	}
}
