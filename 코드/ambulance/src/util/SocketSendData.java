package util;

import domain.AmbulanceInfo;
import domain.PersonalInfo;
import domain.RequestType;
import domain.SelectionButton;
import socket.AmbulanceClientSocket;


public class SocketSendData {
	private final AmbulanceClientSocket client;
	private final SelectionButton selectionButton;
	private final AmbulanceInfo ambulanceInfo;


	public SocketSendData(AmbulanceClientSocket client, AmbulanceInfo ambulanceInfo, SelectionButton selectionButton) {
		this.client = client;
		this.selectionButton = selectionButton;
		this.ambulanceInfo = ambulanceInfo;
	}

	public void sendSelectedHospital(){

		client.send("Client.To." + selectionButton.getSelectedHospital() + PersonalInfo.getPersonalInfo().toString());
		System.out.println("[송신]Client.To." + selectionButton.getSelectedHospital() + PersonalInfo.getPersonalInfo().toString());

	}

	public void sendHelloMessage(){
		System.out.printf("[SEND][%s] Client.TYPE:2.%d.%d;%n", ambulanceInfo.getName(), ambulanceInfo.getxPos(), ambulanceInfo.getyPos());
		client.send("Client.TYPE:2." + ambulanceInfo.getName() + "." + ambulanceInfo.getxPos() + "." + ambulanceInfo.getyPos() + ";");
	}

	public void sendRequestHospitalInfo() {
		System.out.printf("[SEND][%s]: Client.Request.%d;%n", ambulanceInfo.getName(), RequestType.getInstance().getType());
		if(RequestType.getInstance().getType() > -1) {
			client.send("Client.Request." + RequestType.getInstance().getType() + ";");
		}
	}

	public void sendSetPosition(){
		System.out.printf("[SEND][%s] Client.SetPos.%d.%d;", ambulanceInfo.getName(), ambulanceInfo.getxPos(), ambulanceInfo.getyPos());
		client.send("Client.SetPos." + ambulanceInfo.getxPos()+"."+ambulanceInfo.getyPos()+";");
	}

	public void sendClearMessage(){
		System.out.printf("[SEND][%s] Client.Reset", ambulanceInfo.getName());
		client.send("Client.Reset;");
	}


	public void sendRecvMessage(){
		System.out.printf("[SEND][%s] Client.Success.%s", ambulanceInfo.getName(), selectionButton.getSelectedHospital());
		client.send("Client.Success."+selectionButton.getSelectedHospital()+";");
	}


}
