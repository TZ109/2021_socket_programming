
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.WritePendingException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;




public class MyJFrame extends JFrame{

	
	Color defaultColor;

	HospitalInfo hospitalInfo;
	
	JPanel main1 = new JPanel();
	JPanel sub1 = new JPanel();
	JPanel topsub1 = new JPanel();
	
	
	JPanel[] myTable = new JPanel[8];
	JButton[] myLabel = new JButton[8];
	
	
	//인원수 표시할 곳
	JPanel[] myCountPanel = new JPanel[8];
	
	JLabel[] myCount = new JLabel[8];//현재 수용한 인원
	JLabel[] myCount2 = new JLabel[8];//이송중인 환자수
	JLabel[] myCount3 = new JLabel[8];//수용 가능한 인원
	
	JPanel[] mySubTable = new JPanel[8];
	
	
	
	
	//사용가능 여부
	JButton[] avail = new JButton[8];
	
	//메뉴
	JMenu mainMenu = new JMenu("Main");
	JMenuItem menuExit = new JMenuItem("Exit");
	JMenuItem menuInfo = new JMenuItem("Info");
	JMenuBar mb = new JMenuBar();
	JToolBar toolBar;
	
	//클라이언트
	MyAsyncClient client = new MyAsyncClient();
	
	
	//병원이름 좌표 현재 오는 환자 수
	JLabel subtitle = new JLabel("");
	JLabel incoming = new JLabel();
	
	int i,j;
	
	//현재 오는 환자 수
	int incomingNum[] = new int[8];
	JButton incomingList[] = new JButton[8];
	
	//현재 수용한 숫자
	int currentNum[] = new int[8]; 
	JButton currentList[] = new JButton[8];
	
	// 수용 가능한 숫자
	int availNum[] = new int[8];
	
	//현재 오는 숫자
	int incomingNumSum=0;


	public void setSubtitle() {
		this.subtitle.setText(" " + hospitalInfo.getName());
	}


	public MyJFrame(HospitalInfo hospitalInfo) {
		super();

		this.hospitalInfo = hospitalInfo;

		init();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//setContentPane(new GamePanel()); 
		setSize(600, 400);
		setVisible(false);
		setTitle("종합상황판");
		//setBackground(Color.BLACK);
		setResizable(false);
	}
	
	public void init()
	{
		TitledBorder title1 = new TitledBorder("실시간 병상정보");
		BevelBorder ad2 = new BevelBorder(BevelBorder.RAISED);
		CompoundBorder cm = new CompoundBorder(title1, ad2);
		
		Container ct = this.getContentPane();
		
	
		main1.setLayout(new GridLayout(2,4));
		
		
		for(i=0;i<8;i++)
		{
			JPanel tempPanel[] = new JPanel[3];
			
			//수용가능여부
			avail[i] = new JButton("수용 가능");
			avail[i].addActionListener(new MyActionListener());
			
			
		
			//인원수 표시
			myCountPanel[i]= new JPanel();
			myCountPanel[i].setLayout(new GridLayout(3,1));
			
			myCount[i] =  new JLabel("0");
			myCount[i].setHorizontalAlignment(JLabel.CENTER);

			myCount2[i] = new JLabel("0");
			myCount2[i].setHorizontalAlignment(JLabel.CENTER);

			myCount3[i] = new JLabel("0");
			myCount3[i].setHorizontalAlignment(JLabel.CENTER);

			tempPanel[0] = new JPanel();
			tempPanel[0].setLayout(new BorderLayout(10,10));
			tempPanel[1] = new JPanel();
			tempPanel[1].setLayout(new BorderLayout(10,10));
			tempPanel[2] = new JPanel();
			tempPanel[2].setLayout(new BorderLayout());
			
			incomingList[i] = new JButton("현재수용인원   ");
			incomingList[i].setHorizontalAlignment(JLabel.LEFT);
			incomingList[i].setBackground(Color.lightGray);
			incomingList[i].addActionListener(new MyActionListener());
			
			currentList[i] = new JButton("이송중                ");
			currentList[i].setHorizontalAlignment(JLabel.LEFT);
			currentList[i].setBackground(Color.lightGray);
			currentList[i].addActionListener(new MyActionListener());
			
			//이송중
			tempPanel[0].add("Center",incomingList[i]);
			tempPanel[0].add("East",myCount[i]);
			//
			tempPanel[1].add("Center",currentList[i]);
			tempPanel[1].add("East",myCount2[i]);
			
			tempPanel[2].add("Center",new JLabel("수용가능인원    : "));
			tempPanel[2].add("East",myCount3[i]);
			
			myCountPanel[i].add(tempPanel[0]);
			myCountPanel[i].add(tempPanel[1]);
			myCountPanel[i].add(tempPanel[2]);
			
			//응급환자 상태
			myLabel[i] = new JButton();
			
			myLabel[i].setBorder(new BevelBorder(BevelBorder.RAISED));
			myLabel[i].setBackground(Color.CYAN);
			myLabel[i].addActionListener(new MyActionListener());
			
			
			
			switch(i)
			{
			case 0:
				myLabel[i].setText("[중환자실] 일반");
				break;
			case 1:
				myLabel[i].setText("[중환자실] 내과");
				break;
			case 2:
				myLabel[i].setText("[중환자실] 외과");
				break;
			case 3:
				myLabel[i].setText("[중환자실] 음압격리");
				break;
			case 4:
				myLabel[i].setText("[중환자실] 신경과");
				break;
			case 5:
				myLabel[i].setText("[중환자실] 흉부외과");
				break;
			case 6:
				myLabel[i].setText("[중환자실] 신생아");
				break;
			case 7:
				myLabel[i].setText("[중환자실] 화상");
				break;
			}
			
			myTable[i] = new JPanel();
			myTable[i].setLayout(new BorderLayout());
			myTable[i].add("North",myLabel[i]);
			myTable[i].add("Center",myCountPanel[i]);
			myTable[i].add("South",avail[i]);
			
			myTable[i].setBorder(new EtchedBorder());
			
			
			main1.add(myTable[i]);
			main1.setBorder(cm);
		}
		sub1.setLayout(new GridLayout(1,2));
		
		subtitle.setText(" "+"NULL");
		subtitle.setHorizontalAlignment(JLabel.LEFT);
		sub1.add(subtitle);
		
		incoming.setText("이송중인 환자 : 0");
		incoming.setHorizontalAlignment(JLabel.RIGHT);
		sub1.add(incoming);
		
		sub1.setBorder(new EtchedBorder());
		
		ct.setLayout(new BorderLayout());
		ct.add("North",sub1);
		ct.add("Center", main1);
		
		
		//메뉴바
		
		
		mainMenu.add(menuExit);
		mainMenu.add(menuInfo);
		
		
		mb.add(mainMenu);
		
		menuExit.addActionListener(new MyActionListener());
		menuInfo.addActionListener(new MyActionListener());
		
		setJMenuBar(mb);
	}
	

	//클릭 시
	class MyActionListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub


			if(e.getSource()==menuExit)
			{
				System.out.println("종료");
				client.stopClient();
				System.exit(ABORT);
			}
			
			if(e.getSource()==menuInfo)
			{
				
				
			}
			
			for(int j=0; j<8; j++)
			{
				if(e.getSource() == myLabel[j])
				{
					
					//MySubJFrame assign = new MySubJFrame();
					//assign.inInit();
					boolean output = true;
					String ans_str = (String) JOptionPane.showInputDialog(null, null, null);
					
					//숫자인지 판별
					if(ans_str!=null)
					{
						char tmp;				
						
						for(int k=0; k<ans_str.length();k++)
						{
							tmp=ans_str.charAt(k);
							if(Character.isDigit(tmp)==false)
								output=false;
						}
					}
					//숫자라면 현재 수용가능한 인원 변경후 현재 상태 서버로 보냄
					if(ans_str!=null && output==true && ans_str.length()>0)
					{
						availNum[j] = Integer.parseInt(ans_str);
						myCount3[j].setText(""+availNum[j]);
						client.send("Client.Send."+j+"."+currentNum[j]+"."+incomingNum[j]+"."+availNum[j]+";");
						
						
						if(availNum[j]<=currentNum[j]+incomingNum[j])
						{
							avail[j].setText("수용 불가능");
							defaultColor= avail[j].getBackground();
							avail[j].setBackground(Color.red);
							client.send("Client.Denied."+j+";");
						}
					}
					
					
				}
			}
			
			for(int j=0; j<8; j++)
			{
				JButton bt = avail[j];
				String temp;
				if(e.getSource() == bt)
				{
					temp = bt.getText();
					if(temp.equals("수용 가능"))
					{
						bt.setText("수용 불가능");
						defaultColor= bt.getBackground();
						bt.setBackground(Color.red);
						client.send("Client.Denied."+j+";");
					}
					else
					{
						//수용가능한 인원
						if(availNum[j]>incomingNum[j] && availNum[j]>currentNum[j] && availNum[j]>incomingNum[j] + currentNum[j])
						{
							bt.setText("수용 가능");
							bt.setBackground(defaultColor);
							client.send("Client.Access."+j+";");
						}
					}
				}
			}
			
			
		}
		

		
	}
	
	
	
	
	//클라처리를 여기서 함
	public class MyAsyncClient
	{
		AsynchronousChannelGroup channelGroup;
		AsynchronousSocketChannel socketChannel;

		String tempBufferString=null;
		
		public boolean isChannelOpen(){
			return socketChannel.isOpen();
		}

		public void startClient()
		{
			try {
				channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
						Runtime.getRuntime().availableProcessors(),
						Executors.defaultThreadFactory());
				
				socketChannel = AsynchronousSocketChannel.open(channelGroup);
				
				//서버 연결요청
				socketChannel.connect(SocketInfo.getInstance().getInetSocketAddress(),null,
						new CompletionHandler<Void, Void>() {
							//연결 성공시 콜백되는 메소드
							@Override
							public void completed(Void result, Void attachment) {
								// TODO Auto-generated method stub
								
								//서버에서 데이터를 받겠다
								receive();
							}
							//연결 실패시 콜백되는 메소드
							@Override
							public void failed(Throwable exc, Void attachment) {
								// TODO Auto-generated method stub
								if(socketChannel.isOpen())
								{
									stopClient();
								}
							}
					
						});
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void stopClient()
		{
			if(channelGroup!=null && !channelGroup.isShutdown())
			{
				//클라 종료
				try {
					channelGroup.shutdownNow();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		void receive() {
			ByteBuffer byteBuffer = ByteBuffer.allocate(100);
			
			try {
			socketChannel.read(byteBuffer,byteBuffer,
					new CompletionHandler<Integer, ByteBuffer>() {
						//읽기 성공시
						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							
							int readIndex=0;
                        	int bufferLimit=attachment.position();
                        	int tempIndex=0;
                        	String tempString;
                        	//앞서 버퍼에서 읽은 것이 있다면
                        	
                        	
                            
                            
                        	while(readIndex<bufferLimit)
                        	{
                        		//;가 오면 버퍼 끊기
                        		if(attachment.get(readIndex)==';')
                        		{
                        			
	                                attachment.position(tempIndex);
	                                attachment.limit(readIndex+1);
	                                Charset charset = Charset.forName("UTF-8");
	                                String inWhileData = charset.decode(attachment).toString();
	                                
	                                 
	                                //읽기 앞서 읽은게 있다면
	                                if(tempBufferString!=null)
                        			{
	                                	inWhileData=tempBufferString+inWhileData;
	                                	
                        				
                        				tempBufferString=null;
                        			}
	                                doReceiveMethod(inWhileData);
	                                
	                                tempIndex=readIndex+1;
	                                attachment.limit(bufferLimit);
                        		}

                        		
                        		readIndex++;
                        	}
                        	
                        	//클라 종료시
                        	if(readIndex==0)
                        	{
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
                        	
                        		
                        	
                        	
                        	 ByteBuffer byteBuffer = ByteBuffer.allocate(100);

                             socketChannel.read(byteBuffer,byteBuffer,this);
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							// TODO Auto-generated method stub
							stopClient();
						}

						
					});
			
			
			}catch(NotYetConnectedException e)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("서버 연결 안됨");
				startClient();
			}
		}
		
		void send(String data)
		{
			//String msg = scanner.next() + "\r\n";
			//System.out.println(msg);
			//ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
			
			//메시지 공정
			//int dataLength=data.length();
			//data="["+dataLength+"]"+data;
			
			Charset charset = Charset.forName("UTF-8");
			ByteBuffer byteBuffer = charset.encode(data);
			
			try {
			socketChannel.write(byteBuffer,null,
					new CompletionHandler<Integer, Void>(){
						//쓰기 성공시
						@Override
						public void completed(Integer result, Void attachment) {
							// TODO Auto-generated method stub
							System.out.println("송신완료: "+data);
						}

						@Override
						public void failed(Throwable exc, Void attachment) {
							// TODO Auto-generated method stub
							stopClient();
						}
				
			});
			}catch(WritePendingException e)
			{
				System.out.println("송신완료(Wrpending): "+data);
				send(data);
				
				
			}catch(NotYetConnectedException | ShutdownChannelGroupException e)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//서버 재연결 시도
				System.out.println("서버 연결 안됨");
				startClient();
				send(data);
			}
		}
		
		
		public void  doReceiveMethod(String data)
		{
		
			
			//출력
			System.out.println(""+data);

			String recvData = data.substring(0, data.length()-1);
			String info[] = recvData.split("\\.");

			if (info[1].equals("Hello")) {
				SocketInfo.getInstance().setWelcomeMessage(true);
			}

			if (data.length() >= 7 && data.substring(0,7).equals("Server."))
			{
				
				//응급차가 병원에 오겠다 요청
				if(data.length() >= 11 && data.substring(7,11).equals("Send"))
				{
					int hospitalNum = Integer.parseInt(data.substring(12,13));
					
					//병원에 오는 숫자 
					synchronized(this)
					{
						System.out.println("Send()");
						incomingNum[hospitalNum]++;
						myCount2[hospitalNum].setText(""+incomingNum[hospitalNum]);
						//수용 불가능할 때
						if(currentNum[hospitalNum]+incomingNum[hospitalNum] >= availNum[hospitalNum]) {
							avail[hospitalNum].setText("수용 불가능");
							defaultColor= avail[hospitalNum].getBackground();
							avail[hospitalNum].setBackground(Color.red);
						}
						
						
						incomingNumSum++;
						incoming.setText("이송중인 환자 : "+incomingNumSum);
					}
				}
			
				//응급차가 이쪽으로 오는걸 포기
				else if(data.length() >= 13 && data.substring(7,13).equals("Desend"))
				{
					int hospitalNum = Integer.parseInt(data.substring(14,15));
					
					//병원에 오는 숫자 
					synchronized(this)
					{
						System.out.println("Desend");
						incomingNum[hospitalNum]--;
						myCount2[hospitalNum].setText(""+incomingNum[hospitalNum]);
						
						if(currentNum[hospitalNum]+incomingNum[hospitalNum] < availNum[hospitalNum]) {
							avail[hospitalNum].setText("수용 가능");
							avail[hospitalNum].setBackground(defaultColor);
						}
						incomingNumSum--;
						incoming.setText("이송중인 환자 : "+incomingNumSum);
					}
				}
				
				//응급차가 도착
				else if(data.length() >= 14 && data.substring(7,14).equals("Success"))
				{
					int hospitalNum = Integer.parseInt(data.substring(15,16));
					
					
					
					//병원 도착에 성공했으므로
					synchronized(this)
					{
						currentNum[hospitalNum]++;
						myCount[hospitalNum].setText(""+currentNum[hospitalNum]);
						
						incomingNum[hospitalNum]--;
						myCount2[hospitalNum].setText(""+incomingNum[hospitalNum]);
						//현재 오는 사람 수 한명 줄임
						if(incomingNumSum>0)
							{
								incomingNumSum--;
								incoming.setText("이송중인 환자 : "+incomingNumSum);
							}
						//0보다 작을 땐 0으로 표기
						else
						{
							incoming.setText("이송중인 환자 : 0");
						}
					}
				}
			}
			
		}

		
		
		
	}
	
}
