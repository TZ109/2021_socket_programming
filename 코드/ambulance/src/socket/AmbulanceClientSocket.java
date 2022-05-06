package socket;

import domain.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;


public class AmbulanceClientSocket {
	AsynchronousChannelGroup channelGroup;
	AsynchronousSocketChannel socketChannel;
	List<EmergcyBtn> hospitals;
	String tempBufferString = null;
	SelectionButton selectedName;

	public AmbulanceClientSocket(List<EmergcyBtn> hospitals, SelectionButton selectedName) {

		this.hospitals = hospitals;
		this.selectedName = selectedName;
	}


	public boolean isChannelOpen() {
		return socketChannel.isOpen();
	}

	public void startClient() {
		try {
			channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
					Runtime.getRuntime().availableProcessors(),
					Executors.defaultThreadFactory());

			socketChannel = AsynchronousSocketChannel.open(channelGroup);

			//서버 연결요청
			socketChannel.connect(SocketInfo.getInstance().getInetSocketAddress(), null,
					new CompletionHandler<Void, Void>() {
						//연결 성공시 콜백되는 메소드
						@Override
						public void completed(Void result, Void attachment) {
							// TODO Auto-generated method stub
							System.out.println("클라이언트 연결 성공");
							//서버에서 데이터를 받겠다
							receive();
						}

						//연결 실패시 콜백되는 메소드
						@Override
						public void failed(Throwable exc, Void attachment) {
							// TODO Auto-generated method stub
							if(socketChannel.isOpen()){
								stopClient();
							}
						}

					});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopClient() {
		if (channelGroup != null && !channelGroup.isShutdown()) {
			//클라 종료
			try {
				channelGroup.shutdownNow();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void hospitalDataParse(String recvData) {
		// 응급차 처음 입장시, 새로운 병원 입장시
		//Server.Send.Hell.112.123
		//recvData = recvData.substring(0, recvData.length()-1);
		String info[] = recvData.split("\\.");
	}



	public void getHospitalInfo(String recvDate) {
		// 응급차가 병원에 대한 정보 요청시;
		//Server.Ack.name.accepted.incoming.empty.index;
		//recvDate = recvDate.substring(0, recvDate.length() - 1);
		String info[] = recvDate.split("\\.");
		int i = Integer.parseInt(info[6]);
		EmergcyBtn hospital = hospitals.get(i);

		if(RequestType.getInstance().getType() == selectedName.getType()) {

			if (info[2].equals(selectedName.getSelectedHospital())) {
				hospital.btn.setBackground(Color.RED);
				hospital.btn.setSelected(true);
			} else {
				hospital.btn.setBackground(Color.WHITE);
				hospital.btn.setSelected(false);
			}

		}
		else {
			hospital.btn.setBackground(Color.WHITE);
			hospital.btn.setSelected(false);
		}

		hospital.btn.setEnabled(!info[2].equals("NULL"));


		//병원 정보 수정
		hospital.setHospitalInfo(info[2], Integer.parseInt(info[3]), Integer.parseInt(info[4]), Integer.parseInt(info[5]));
		hospital.btn.setText(hospital.info.toString());


	}

	void receive() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(100);

		socketChannel.read(byteBuffer,byteBuffer,
				new CompletionHandler<Integer, ByteBuffer>() {
					//읽기 성공시
					@Override
					public void completed(Integer result, ByteBuffer attachment) {

						int readIndex=0;
						int bufferLimit=attachment.position();
						int tempIndex=0;
						//앞서 버퍼에서 읽은 것이 있다면


						while(readIndex!=bufferLimit)
						{

							if(attachment.get(readIndex)==';')
							{

								attachment.position(tempIndex);
								attachment.limit(readIndex+1);
								Charset charset = Charset.forName("UTF-8");
								String inWhileData = charset.decode(attachment).toString();

								//읽기 앞서 읽은게 있다면
								if(tempBufferString!=null)
								{

									inWhileData = tempBufferString + inWhileData;

									tempBufferString=null;
								}
								doReceiveMethod(inWhileData);

								tempIndex=readIndex+1;

								attachment.limit(bufferLimit);
							}
							readIndex++;
						}

						if (readIndex == 0) {
							doReceiveMethod(null);
						}

						//마지막이 ;가 아닐때
						else if(attachment.get(readIndex-1)!=';')
						{
							attachment.position(tempIndex);
							attachment.limit(readIndex);
							Charset charset = Charset.forName("UTF-8");
							tempBufferString = charset.decode(attachment).toString();
						}



						try{
						//계속 읽기 위해 자신 호출
						ByteBuffer byteBuffer = ByteBuffer.allocate(100);
						socketChannel.read(byteBuffer,byteBuffer,this);}
						catch (Exception ignored){}
					}

					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
						// TODO Auto-generated method stub
						stopClient();
					}


				});
	}

	private void doReceiveMethod(String data) {
		System.out.println("data = " + data);
		if (data == null) {
			JOptionPane.showMessageDialog(null, "서버 연결 끊김", "오류", JOptionPane.ERROR_MESSAGE);
			if (socketChannel.isOpen())
				stopClient();
			LoginInfo.getInstance().getMainFrame().setVisible(false);
			LoginInfo.getInstance().getLoginFrame().setVisible(true);
			SocketInfo.getInstance().setWelcomeMessage(false);
		} else {
			data = data.substring(0, data.length()-1);
			String info[] = data.split("\\.");
			//데이터 처리
			switch (info[1]) {
				case "Send":
					hospitalDataParse(data);
					break;
				case "Ack":
					getHospitalInfo(data);
					break;
				case "Hello":
					recvWelcomeMessage();
					break;
				default:
					System.out.println("[INFO] 규격에 맞지 않는 메시지 " + data);
					break;
			}
		}
	}

	private void recvWelcomeMessage() {
		SocketInfo.getInstance().setWelcomeMessage(true);
	}

	public void send(String data) {
		Charset charset = StandardCharsets.UTF_8;
		ByteBuffer byteBuffer = charset.encode(data);

		try {
			socketChannel.write(byteBuffer, null,
					new CompletionHandler<Integer, Void>() {
						//쓰기 성공시
						@Override
						public void completed(Integer result, Void attachment) {
							// TODO Auto-generated method stub
						}

						@Override
						public void failed(Throwable exc, Void attachment){
							// TODO Auto-generated method stub
							if(!socketChannel.isOpen()){
								stopClient();
							}
							if(socketChannel.isOpen()) {
								send(data);
							}
						}

					});
		} catch (WritePendingException ignored) {

		} catch (NotYetConnectedException | ShutdownChannelGroupException e ) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
//			System.out.println("[Warning] 서버연결 실패, 재연결 시도");
		}

	}


}
