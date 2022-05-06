import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;



//import jdk.tools.jlink.internal.Platform;



public class AsyncServer {//extends Application{
	//응급차에서 쓸 락
	ReentrantLock lock = new ReentrantLock();

	
    AsynchronousChannelGroup channelGroup;
    AsynchronousServerSocketChannel serverSocketChannel;
    List<Client> connections = new Vector<Client>();
    List<Client> subconnections1 = new Vector<Client>();
    List<Client> subconnections2 = new Vector<Client>();

    public boolean isServerOpen() {
	    return serverSocketChannel.isOpen();
    }

    public void startServer() throws Exception
    {
        //cpu지원하는 코어의 수만큼 쓰레드 생성
        channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                Executors.defaultThreadFactory());

        serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        serverSocketChannel.bind(ServerAddressInformation.getInstance().getInetSocketAddress());

        System.out.println("서버 시작");


        //실제 연결은 스레드풀에서 알아서 처리할 것임
        serverSocketChannel.accept(null,
                new CompletionHandler<AsynchronousSocketChannel, Void>() {

                    //연결성공
                    @Override
                    public void completed(AsynchronousSocketChannel socketChannel, Void attachment)
                    {
                        // TODO Auto-generated method stub
                        String message;
                        try {
                            message =""+ socketChannel.getRemoteAddress();
                            System.out.println(message);
                        }catch(IOException e){}

                        //통신용 클라이언트 생성
                        Client client = new Client(socketChannel);
                        connections.add(client);
                        //connections.size();

                        System.out.println("연결된 클라이언트 수 "+connections.size());
                        //반복연결을 위해 accept호출
                        serverSocketChannel.accept(null,this);
                    }

                    //연결 실패
                    @Override
                    public void failed(Throwable exc, Void attachment) {
                        // TODO Auto-generated method stub
                        if(serverSocketChannel.isOpen())
                            stopServer();
                    }

                });
    }

   public void stopServer()
    {
        //커넥션의 모든 클라이언트 객체 클리어
        connections.clear();

        if(channelGroup!=null && !channelGroup.isShutdown())
        {
            try {
                //현재 채널에 속한 모든 채널을 닫음
                //클라안에 있는 채널들이 자동적으로 닫힘
                channelGroup.shutdownNow();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //버튼 btnStartStop.setText("start");서버멈춤
        }

        System.out.println("연결된 클라이언트 수 "+connections.size());
    }

   
   
    public class Client {
    	//이전에 버퍼 읽었던 문자열 저장소
    	String tempBufferString=null;
    	
    	//클라이언트 소켓
        AsynchronousSocketChannel socketChannel;
        //클라이언트 타입
        int type=0;
        int clientCheck=0;
        
        //현재 좌표
        int xpos=0;
        int ypos=0;
        
        //이름
        String clientName;
        
        //빈자리
        int[] empty=new int[8];
        
        //이송중
        int[] incoming=new int[8];
        
        //수용한 인원
        int[] accepted=new int[8];

        
        //수용가능여부
        boolean[] possible = new boolean[8];
        //병원선택으로 거부한 경우
       // boolean[] hospChoicePoss = new boolean[8];
        
        
        //응급차량 번호
        int ambulNum=0;
        //응급차가 가고자 하는 병원이름
        String togoClientName;
        
        //응급차가 관심있는 중환자실 번호
        int ambulWantedIndex=-1;
        
        //응급차의 병원 도착여부 확인
        boolean ambulSuccess=false;
       
        
        //응급차가 자기가 선택한 병원 기억
        //<병원 클라이언트, 요구하는 병상번호>맵
        Hashtable<Client,Integer> hospitalMap = new Hashtable<Client,Integer>();
        
        public Client(AsynchronousSocketChannel socketChannel)
        {
            this.socketChannel = socketChannel;
            
            for(int k=0;k<8;k++)
            {
            	possible[k]=true;
            }
            receive();//생성과 동시에 받을 준비
        }

       public void receive()
        {
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            
            socketChannel.read(byteBuffer,byteBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {// read의 결과값은 읽은 수인 integer, 첨부 개체는 Byte버퍼

                        // 읽기 성공시 자동 콜백
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            // TODO Auto-generated method stub

                        	int readIndex=0;
                        	int bufferLimit=attachment.position();
                        	int tempIndex=0;
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

                        //읽기 실패시 자동 콜백
                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            // TODO Auto-generated method stub

                            //해당 클라 연결 끊음
                            try {
                            	
                            	
									
                            	if(Client.this.type == 1)
                             	{
                            		
                            		
                             		System.out.println("[병원 연결 종료]"+Client.this.clientName);
                             		
                             		lock.lock();
                             		
                             		Iterator<Client> iter = subconnections1.iterator();
                             		//동기화를 위해서
                             		
                             		
	                             		while(iter.hasNext())
	                             		{
	                             			Client iterClient = iter.next();
	                             			
	                             			if(iterClient == Client.this)
	                             			{
	                             				iter.remove();
	                             			}
	                             		}
                             		
                             		
                             		
                             		//subconnections1.remove(Client.this);
                             		
                             		
                             		//병원 연결이 끊겼으므로 다시 보냄
                             		
	                             	for(Client tb : subconnections2)
	                             	{
	                             		toAmbulGiveSortArr(tb);
	                             	}
                             		
                             		lock.unlock();
                             	}
                             	
                             	else if(Client.this.type == 2)
                             	{
                             		//
                             		if(Client.this.ambulWantedIndex!=-1 && Client.this.togoClientName!=null)
                             		{
                             			lock.lock();
                             			for(Client tb : subconnections1)
                             			{
                             				//응급차가 사라지기 전에 
                             				if(tb.clientName.equals(Client.this.togoClientName))
                             				{
                             					//차량번호와 원래 갈려고했던 병실 전달
                             					if(Client.this.ambulSuccess==false)
                             						{
	                             						
	                             					tb.send("Server.Failed."+Client.this.ambulNum+"."+Client.this.ambulWantedIndex+";");
	                             						
                             						}
                             					
                             				}
                             			}
                             			
                             			lock.unlock();
                             		}
                             				
                             		
                             		System.out.println("[응급차 연결 종료]"+Client.this.ambulNum);
                             		
                             		lock.lock();
                             		
                             		//subconnections2.remove(Client.this);
                             		
                             		Iterator<Client> iter = subconnections2.iterator();
                             		//동기화를 위해서
                             		while(iter.hasNext())
                             		{
                             			Client iterClient = iter.next();
                             			
                             			if(iterClient == Client.this)
                             			{
                             				iter.remove();
                             			}
                             		}
                             		lock.unlock();
                             		
                             	}
                             	
                             	connections.remove(Client.this);
                             	socketChannel.close();
                                
                            	
                            	
                            	
                            	
                            	
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                    }
            );//바이트 버퍼, 콜백 개체에 첨부할 개체, complethandler

        }

        public void send(String data)
        {
            Charset charset = Charset.forName("UTF-8");
            ByteBuffer byteBuffer = charset.encode(data);
            
            try {
            socketChannel.write(byteBuffer,null,
                    new CompletionHandler<Integer, Void>()
                    {
                        //쓰기 성공시
                        @Override
                        public void completed(Integer result, Void attachment) {
                            // TODO Auto-generated method stub

                        }


                        //쓰기작업 실패 시
                        @Override
                        public void failed(Throwable exc, Void attachment) {
                            // TODO Auto-generated method stub
                            try {
                            	if(Client.this.type==1)
                                {
                                	subconnections1.remove(Client.this);
                                }
                                
                                else if(Client.this.type==2)
                                {
                                	 subconnections2.remove(Client.this);
                                }
                            	
                                connections.remove(Client.this);
                                socketChannel.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        }

                    }

            );
            
            }catch(WritePendingException e)
            {
            	
            	send(data);
            }
        }
        
        
        public void doReceiveMethod(String data)
        {
        	try {

                 System.out.println(data);
                 
                 
                 //데이터 입력이없을시 연결종료
                 //윈도우가 아닌 환경에서 종료조건
                 if(data==null)
                 {
                	 if(Client.this.type == 1)
                  	{
                 		
                 		String eraseName=Client.this.clientName;
                  		System.out.println("[병원 연결 종료]"+eraseName);
                  		
                  		lock.lock();
                  		
                  		Iterator<Client> iter = subconnections1.iterator();
                  		//동기화를 위해서
                  		
                  		
                      		while(iter.hasNext())
                      		{
                      			Client iterClient = iter.next();
                      			
                      			if(iterClient.clientName.equals(eraseName))
                      			{
                      				iter.remove();
                      				break;
                      			}
                      		}
                  		
                  		
                  		//subconnections1.remove(Client.this);
                  		
                  		
                  		//병원 연결이 끊겼으므로 다시 보냄
                  		
                      	for(Client tb : subconnections2)
                      	{
                      		toAmbulGiveSortArr(tb);
                      	}
                  		
                  		lock.unlock();
                  	}
                  	
                  	else if(Client.this.type == 2)
                  	{
                  		//
                  		if(Client.this.ambulWantedIndex!=-1 && Client.this.togoClientName!=null)
                  		{
                  			lock.lock();
                  			for(Client tb : subconnections1)
                  			{
                  				//응급차가 사라지기 전에 
                  				if(tb.clientName.equals(Client.this.togoClientName))
                  				{
                  					//차량번호와 원래 갈려고했던 병실 전달
                  					if(Client.this.ambulSuccess==false)
                  						{
                      						
                      					tb.send("Server.Failed."+Client.this.ambulNum+"."+Client.this.ambulWantedIndex+";");
                      						
                  						}
                  					
                  				}
                  			}
                  			
                  			lock.unlock();
                  		}
                  				
                  		
                  		System.out.println("[응급차 연결 종료]"+Client.this.ambulNum);
                  		
                  		lock.lock();
                  		//subconnections2.remove(Client.this);
                  		
                  		Iterator<Client> iter = subconnections2.iterator();
                  		//동기화를 위해서
                  		while(iter.hasNext())
                  		{
                  			Client iterClient = iter.next();
                  			
                  			if(iterClient == Client.this)
                  			{
                  				iter.remove();
                  			}
                  		}
                  		lock.unlock();
                  		//
                  	}
                  	
                  	connections.remove(Client.this);
                  	socketChannel.close();
                 	
                 }
                 
                 else
                 {
	                //Client.-- 으로 입력될때 
	                if (data.length() >= 7 && data.substring(0,7).equals("Client.")) {
	
	                	//처음 입장시 타입체크
	                    if (data.length() >= 12 && data.substring(7, 12).equals("TYPE:") && clientCheck == 0)
	                    {
	
	                        clientCheck = 1;
	                       // System.out.println(data.substring(12));
	                        type = Integer.parseInt(data.substring(12,13));
	                        System.out.println("클라이언트 타입: " + type);
	
	                        //병원 측 처음 입장 시
	                        if(type == 1)
	                        {
	                        	int h,temph;
	                        	
	                            subconnections1.add(Client.this);
	
	                            for(h=14; h<data.length(); h++)
	                            {
	                            	if(data.charAt(h)=='.')
	                            		break;
	                            
	                            }
	                            
	                            if(h!=14)
	                            {
	                            	clientName=data.substring(14,h);
	                            	
	                            }
	                            
	                            temph=++h;
	                            
	                            for(;h<data.length();h++)
	                            {
	                            	if(data.charAt(h)=='.')
	                            		break;
	                            }
	                            
	                            if(temph!=h)
	                            {
	                            	xpos=Integer.parseInt(data.substring(temph,h));
	                            }
	                            temph=++h;
	                            
	                            for(;h<data.length();h++)
	                            {
	                            	if(data.charAt(h)==';')
	                            		break;
	                            }
	                            
	                            if(temph!=h)
	                            {
	                            	ypos=Integer.parseInt(data.substring(temph,h));
	                            }
	                            
	                            //System.out.println(""+clientName+" " + xpos +" "+ypos);
	                            
	                            //입장 클라이언트에 대해 환영 메시지;
		                        Client.this.send("Server.Hello;");
		                        System.out.println("[INFO] Send Hello Message");
	                            
	                        }
	                        
	                        //응급차 측 처음 입장 시
	                        if(type == 2)
	                        {
	                        	int h,temph;
	                        	
	                            subconnections2.add(Client.this);
	                            
	                            for(h=14; h<data.length(); h++)
	                            {
	                            	if(data.charAt(h)=='.')
	                            		break;
	                            
	                            }
	                            
	                            //응급차의 차량번호
	                            ambulNum=Integer.parseInt(data.substring(14,h));
	                            
	                            temph=++h;
	                            
	                            for(;h<data.length();h++)
	                            {
	                            	if(data.charAt(h)=='.')
	                            		break;
	                            }
	                            
	                            if(temph!=h)
	                            {
	                            	xpos=Integer.parseInt(data.substring(temph,h));
	                            }
	                            temph=++h;
	                            
	                            for(;h<data.length();h++)
	                            {
	                            	if(data.charAt(h)==';')
	                            		break;
	                            }
	                            
	                            if(temph!=h)
	                            {
	                            	ypos=Integer.parseInt(data.substring(temph,h));
	                            }


		                        //입장 클라이언트에 대해 환영 메시지;
		                        Client.this.send("Server.Hello;");
		                        System.out.println("[INFO] Send Hello Message");
	                            
	                        }
	                       
	                    }
	
	                    
	                    //병원의 현재 수용상황을 서버에 올림
	                    else if(data.length() >= 11 && data.substring(7,11).equals("Send"))
	                    {
	                    	//병원측 
	                    	if(Client.this.type==1)
	                    	{
	                    		
	                    		{
	                            	int index = Integer.parseInt(data.substring(12,13));
	                            	int h,temph;
	                            	
	
	                                for(h=14; h<data.length(); h++)
	                                {
	                                	if(data.charAt(h)=='.')
	                                		break;
	                                
	                                }
	                                
	                                if(h!=14)
	                                {
	                                	Client.this.accepted[index]=Integer.parseInt(data.substring(14,h));
	                                	
	                                }
	                                
	                                temph=++h;
	                                
	                                for(;h<data.length();h++)
	                                {
	                                	if(data.charAt(h)=='.')
	                                		break;
	                                }
	                                
	                                if(temph!=h)
	                                {
	                                	Client.this.incoming[index]=Integer.parseInt(data.substring(temph,h));
	                                }
	                                temph=++h;
	                                
	                                for(;h<data.length();h++)
	                                {
	                                	if(data.charAt(h)==';')
	                                		break;
	                                }
	                                
	                                if(temph!=h)
	                                {
	                                	Client.this.empty[index]=Integer.parseInt(data.substring(temph,h));
	                                }
	                                
	                                
	                                lock.lock();
	                                //응급차들에게 정보 전달
	                                for(Client tb : subconnections2)
	                                {
	                                	toAmbulGiveSortArr(tb);
	                                }
	                                lock.unlock();
	                              
	                                
	                            	
	                                //병원측으로 서버 확인문장
	                            	//Client.this.send("avail["+index+"] : "+empty[index]+".");
	                    		}
	                    	} 
	                    	
	                    	
	                    }
	                    
	                    
	                    //응급차 측의 병원정보 요청
	                    else if(data.length() >= 14 && data.substring(7,14).equals("Request"))
	                    {
	                    	//응급차 측
	                    	if(Client.this.type==2)
	                    	{
	
	                        	//request의 0~7사이값 얻기
	                        	//응급차가 요구하는 중환자실 번호 획득
	                        	synchronized(this)
	                    		{
	                        		
	                    			ambulWantedIndex = Integer.parseInt(data.substring(15,16));
	                    			
	                    	
	                    			//System.out.println("원하는 중환자실 번호 : "+ambulWantedIndex);
	                    		}
	                        	
	                        	
	                        	lock.lock();
	                        	//응급차에게 병원 정보 전달 현재 클라가 응급차일때 기준
	                        	toAmbulGiveSortArr(Client.this);
	                        	lock.unlock();
	                        	
	                    	} 	
	                    }
	                    
	                    
	                  //응급차를 병원으로 보냄
	                    else if(data.length() >= 9 && data.substring(7,9).equals("To"))
	                    {
	                    	//응급차 측
	                    	if(Client.this.type==2)
	                    	{
	                    		int h;
	                        	String tempName;
	                        	boolean success=false;
	
	                        	//환자정보가 있을 시 병원이름.환자정보 아니면, 문장의 마지막이 병원이름;
	                            for(h=10; h<data.length(); h++)
	                            {
	                            	if(data.charAt(h)=='.' || data.charAt(h)==';')
	                            		break;
	                            
	                            }
	                            //병원이름
	                    		tempName = data.substring(10,h);
	                            
	                            
	                    		//현재 병원 리스트에서
	                    		for(Client hospital : subconnections1)
	                            {
	                    			//병원 찾기 성공
	                            	if(hospital.clientName.equals(tempName))
	                            	{
	                            		//해당 중환자실에 접근 가능하고 빈병상이 있을 때
	                            		if(hospital.possible[ambulWantedIndex]==true && hospital.empty[ambulWantedIndex]!=0)
	                            		{
	                                		lock.lock();
	                                		{
	
	                                    		success=true;
	                                    		//h는 .또는 ;그 이후로 환자에 대한 정보를 병원에게 보냄
	                                    		hospital.send("Server.Send."+ambulWantedIndex+"."+ambulNum+data.substring(h));
	                                    		//해당 병실에 오고있는 숫자 증가
	                                    		hospital.incoming[ambulWantedIndex]++;
	                                    		
	                                    		//이전에 선택한 병원이 없다면
	                                    		if(Client.this.hospitalMap.isEmpty())
	                                    		{
	                                    			Client.this.hospitalMap.put(hospital, ambulWantedIndex);
	                                    			//System.out.println("병원:" + hospital.clientName+"."+ambulWantedIndex);
	                                    		}
	                                    		//이전 병원이 있었다면
	                                    		else
	                                    		{
	                                    			Iterator<Client> keys = Client.this.hospitalMap.keySet().iterator();
	                                            	
	                                            	while(keys.hasNext())
	                                            	{
	                                            		Client hashClient = keys.next();
	                                            		hashClient.incoming[Client.this.hospitalMap.get(hashClient)]--;
	                                            		//병원이 응급차를 리스트에서 빼도록 명령
	                                            		
	                                            		
	                                            		hashClient.send("Server.Desend."+Client.this.hospitalMap.get(hashClient)+"."+ambulNum+";");
	                                            		Client.this.hospitalMap.remove(hashClient);
	                                            	}
	                                            	
	                                            	Client.this.hospitalMap.put(hospital, ambulWantedIndex);
	                                    			System.out.println("병원:" + hospital.clientName+"."+ambulWantedIndex);
	                                    		}
	                                    		
	                                    		
	                                    		//해당 병원 수용가능한 인원이 줄어들었으므로
	                                    		//해당 병원이 더 이상 수용 불가능 할 떄
	                                    		//병원에서 수용 불가능하다고 알리는 것과 로직은 같음
	                                    		if( hospital.empty[ambulWantedIndex] <= hospital.accepted[ambulWantedIndex] +hospital.incoming[ambulWantedIndex])
	                                    		{
	                                    			hospital.possible[ambulWantedIndex]=false;
	                                    			//응급차들한테 알림
	                                    			for(Client tb : subconnections2)
	                                    			{
	                                    					toAmbulGiveSortArr(tb);
	                                    			}
	                                    			
	                                    		}

	                                		}
	                                		lock.unlock();
	                            		}
	                            		
	                            	}
	                            }
	                    		
	                    		//병원찾기 실패
	                    		if(success==false)
	                    		{
	                    			//응급차에게 병원 연결이 실패했음을 알림
	                    			Client.this.send("Server.ConnectionFail;");
	                    			
	                    			lock.lock();
	                    			for(Client tb : subconnections2)
                        			{
                        					toAmbulGiveSortArr(tb);
                        			}
	                    			lock.unlock();
	                    		}
	                    		
	                    		//병원연결 성공
	                    		else if(success==true)
	                    		{
	                    			Client.this.send("Server.ConnectionSuccess;");
	                    			
	                    			lock.lock();
	                    			for(Client tb : subconnections2)
                        			{
                        					toAmbulGiveSortArr(tb);
                        			}
	                    			lock.unlock();
	                    		}
	                    	} 
	                    	
	                    }
	                    
	                    
	                    //병원이 해당 병실은 사용불가능하다 알림
	                    else if(data.length() >= 13 && data.substring(7,13).equals("Denied"))
	                    {
	                    	if(Client.this.type==1)
	                    	{
	                    		int k =Integer.parseInt(data.substring(14,15));
	                    		
	                    		
	                    		lock.lock();
	                    		possible[k] = false;
	                    		//hospChoicePoss[k]=false;
	                    		
	                    		//응급차들에게 해당 병상 이용 불가능하다고 알림
	                    		for(Client tb : subconnections2)
	                    		{
	                    			toAmbulGiveSortArr(tb);
	                    		}
	                    		lock.unlock();
	                    	}
	                    }
	                    
	                    //병원이 해당 병실 수용가능함을 알림
	                    else if(data.length() >= 13 && data.substring(7,13).equals("Access"))
	                    {
	                    	if(Client.this.type==1)
	                    	{
	                    		int k = Integer.parseInt(data.substring(14,15));
	                    		
	                    		
	                    		
	                    		
	                    		lock.lock();
	                    		possible[k] = true;
	                    		//hospChoicePoss[k]=true;
	                    		
	                    		for(Client tb : subconnections2)
	                    		{
	                    			toAmbulGiveSortArr(tb);
	                    		}
	                    		lock.unlock();
	                    	}
	                    }
	                    
	                    //응급차의 위치 변환
	                    else if(data.length() >= 13 && data.substring(7,13).equals("SetPos"))
	                    {
	                    	if(Client.this.type==2)
	                    	{
	                    		lock.lock();
	                    		int h,temph;
                            	

                                for(h=14; h<data.length(); h++)
                                {
                                	if(data.charAt(h)=='.')
                                		break;
                                
                                }
                                
                                if(h!=14)
                                {
                                	xpos=Integer.parseInt(data.substring(14,h));
                                	
                                }
                                
                                temph=++h;
                                
                                for(;h<data.length();h++)
                                {
                                	if(data.charAt(h)==';')
                                		break;
                                }
                                
                                if(temph!=h)
                                {
                                	ypos=Integer.parseInt(data.substring(temph,h));
                                }

	                    		
	                    		toAmbulGiveSortArr(Client.this);
	                    		lock.unlock();
	                    	}
	                    }
	                    
	                    //응급차 정보 리셋
	                    else if(data.length() >= 12 && data.substring(7,12).equals("Reset"))
	                    {
	                    	if(Client.this.type==2)
	                    	{
	                    		lock.lock();
	                    		
	                    		//응급차가 이전에 병원을 선택했었을 때
	                    		if(!hospitalMap.isEmpty())
	                    		{
	                    			Iterator<Client> keys = Client.this.hospitalMap.keySet().iterator();
	                    			Client hashClient = keys.next();
	                    			hashClient.incoming[Client.this.hospitalMap.get(hashClient)]--;
                            		//병원이 응급차를 리스트에서 빼도록 명령
                            		
                            		
                            		hashClient.send("Server.Desend."+Client.this.hospitalMap.get(hashClient)+"."+ambulNum+";");
                            		Client.this.hospitalMap.remove(hashClient);
	                    		}
	                    		
	                    		for(int k=0; k<5; k++)
	                        	{
	                        		send("Server.Ack.NULL.0.0.0."+k+";");
	                        	}
	                    		
	                    		lock.unlock();
	                    	}
	                    }
	                    
	                    //응급차가 병원에 도착!
	                    else if(data.length() >= 13 && data.substring(7,14).equals("Success"))
	                    {
	                    	//응급차 측
	                    	lock.lock();
	                    	if(Client.this.type==2 && !Client.this.hospitalMap.isEmpty())
	                    	{
	                    		int h;
	
	                        	boolean success=false;
	
	                        	//문장의 마지막이 병원이름;
	                            for(h=15; h<data.length(); h++)
	                            {
	                            	if(data.charAt(h)==';')
	                            		break;
	                            
	                            }
	                            //병원이름
	                    		togoClientName = data.substring(15,h);
	                            
	                    		//응급차가 선택한 병원
	                    		Iterator<Client> keys = Client.this.hospitalMap.keySet().iterator();
	                            Client hospital = keys.next();
	                    		
	                    		
	                    		
	                    			//병원 찾기 성공
	                            	if(hospital.clientName.equals(togoClientName))
	                            	{
	                            		//해당 중환자실에 자리가 있고 빈병상이 있을 때
	                            		if(hospital.empty[ambulWantedIndex] >= hospital.incoming[ambulWantedIndex]+hospital.accepted[ambulWantedIndex] && hospital.empty[ambulWantedIndex]!=0)
	                            		{
	                                		
	                            			synchronized (this)
	                            			{
												
											
	                                    		success=true;
	                                    		//h는 .이고 그 이후로 환자에 대한 정보를 병원에게 보냄
	                                    		hospital.send("Server.Success."+ambulWantedIndex+"."+ambulNum+data.substring(h));
	                                    		//병원 도착했으므로 오고있는중 하나 감소
	                                    		hospital.incoming[ambulWantedIndex]--;
	                                    		//병원 도착한 인원 증가
	                                    		hospital.accepted[ambulWantedIndex]++;
	                                    		
	                                    		
	                            			}	
	                                		
	
	                                		//병원 도착한건 이송중이 도착으로 변경, 이송중 도착 총합은 변화없음
	                            		}
	                            		
	                            	}
	                            
	                    		
	                    		//병원찾기 실패
	                    		if(success==false)
	                    		{
	                    			//응급차에게 병원 연결이 실패했음을 알림
	                    			Client.this.send("Server.ConnectionFail;");
	                    			togoClientName=null;
	                    		}
	                    		
	                    		else if(success==true)
	                    		{
	                    			Client.this.send("Server.Accepted;");
	                    			
	                    			
	                    			
	                    			Client.this.hospitalMap.clear();
	                    			for(Client tb : subconnections2)
		                    		{
		                    			toAmbulGiveSortArr(tb);
		                    		}
									
	                    			
	                    		
	                    			
	                    		}
	                    		
	                    		
	                    		
	                    	} 
	                    	lock.unlock();
	                    }
	                    
	                }

                
                 }
                
                
               

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        
        public double getDistance(int x1, int y1, int x2, int y2)
        {
        	double dist=0;
        	dist = Math.sqrt(Math.pow(x1-x2,2)+ Math.pow(y1-y2, 2));
        	return dist;
        }
        
        
        //응급차에게 5개의 병원 정렬해서 주기
        //amCli는 응급차를 말함
        public void toAmbulGiveSortArr(Client amCli)
        {
        	
        	//파라미터의 클라이언트 인스턴스가 응급차일 때만 줄것
        	if(amCli.type==2 )
        	{
        		Client[] sortClientArr = new Client[subconnections1.size()];
        		Client sortTempClient=null;
        		double[] forSortArr = new double[subconnections1.size()];
        		double tempValue=0.0;
        		
        		int innerCount =0;
        		int k;
        		int j;
        		int tempIndex=0;
        		int count=0;
        	
        		
        		if(amCli.ambulWantedIndex!=-1 && !subconnections1.isEmpty())//아직 아무것도 선택 안했을 경우 제외 병원이 아무도 없을 때도 제외
        		{
        		
        			
	        		//병원측에서
	            	for(Client hospital : subconnections1)
	                {
	            		//응급차에 전달할 5개 병원정보
	            		
	            		System.out.println(hospital.clientName+": "+hospital.accepted[amCli.ambulWantedIndex]+", " + hospital.incoming[amCli.ambulWantedIndex]+", "+hospital.empty[amCli.ambulWantedIndex]+", "+hospital.possible[amCli.ambulWantedIndex]);
	
	            		//현재 응급차 클라에게 모든 병원의 정보 보냄
	            		
	            		//병원의 해당 병실이 접근가능할 때 
	            		if(hospital.possible[amCli.ambulWantedIndex]==true)
	            		{
	            			//수용할 수 있는 자리가 있다면
	                		if((hospital.empty[amCli.ambulWantedIndex] > hospital.accepted[amCli.ambulWantedIndex] + hospital.incoming[amCli.ambulWantedIndex]) && hospital.empty[amCli.ambulWantedIndex]!=0 && innerCount<subconnections1.size())
	                		{
	                			
	                			
	                			//응급차 측에서 정렬을 위해
	                			sortClientArr[innerCount] = hospital;
	                			forSortArr[innerCount]=getDistance(amCli.xpos, amCli.ypos, hospital.xpos, hospital.ypos);
	
	                			
	                			//응급차에게 병실 정보 보냄

	                				
	                			innerCount++;
	                			
	                		}
	               
	            		}
	            		
	            		//해당 병실에 갈 수 없을 떄
	            		else
	            		{
	            			if(!amCli.hospitalMap.isEmpty())
	    	            	{
	
	    	            		Iterator<Client> keys = amCli.hospitalMap.keySet().iterator();
	                     
	                        	Client hashClient = keys.next();
	                        	
	                        	//갈 수 없다고 뜨지만, 내가 선택해서 다른 사람들이 못가게 된 상황
	                        	//자리는 만석인데 응급차가 자리를 차지해서 만석이 된 경우
	                        	if(hashClient==hospital)
	                        	{
	                        		sortClientArr[innerCount] = hospital;
		                			forSortArr[innerCount]=getDistance(amCli.xpos, amCli.ypos, hospital.xpos, hospital.ypos);
	                        		innerCount++;
	                        		
	                        	}
	    	            	}
	            		}
	            	
	                }
	            	
	            	
	            	//정렬
	            	for(k=0;k<innerCount;k++)
	            	{
	            		tempValue=forSortArr[k];
	            		tempIndex=k;
	            		for( j=k+1;j<innerCount;j++)
	            		{
	            			if(forSortArr[j]<tempValue)
	            			{
	            				tempValue=forSortArr[j];
	            				tempIndex=j;
	            			}
	            		}
	            		
	            		if(tempIndex!=k)
	            		{
	            			
	            			forSortArr[tempIndex]=forSortArr[k];
	            			forSortArr[k]=tempValue;
	            			
	            			sortTempClient=sortClientArr[tempIndex];
	            			sortClientArr[tempIndex]=sortClientArr[k];
	            			sortClientArr[k]=sortTempClient;
	            		}
	            		
	            	}
	            	
	            	//응급차가 선택한 병원이 있을 때 그리고 현재 병원 수가 5개보다 많을 때
	            	if(!amCli.hospitalMap.isEmpty() && innerCount>5)
	            	{
	            		boolean isWithinFive = false;
	            		
	            		Iterator<Client> keys = amCli.hospitalMap.keySet().iterator();
                 
                    	Client hashClient = keys.next();
                	
                    	//가장 가까운 5개 내로 응급차가 선택한 병원이 있는지 검사
	            		for(j=0;j<5;j++)
			            {
		            		if(hashClient==sortClientArr[j])
		            			isWithinFive=true;
			            }
		            		
		            		//선택한 병원이 정렬 5개 이내에 없다는 뜻, 즉 앞서 4개는 더 가까이 있으며, 내가 선택한게 젤 멀다는 의미
		            	if(isWithinFive==false)
		            	{
		            		sortClientArr[4]= (Client) hashClient;
		            	}
	            		
	            		
	            		
	            	}
	            	
	            	
	            	
		            	for(j=0;j<innerCount;j++)
		            	{
		            		//응급차에게 전송
		            		amCli.send("Server.Ack."+sortClientArr[j].clientName+"." + sortClientArr[j].accepted[amCli.ambulWantedIndex]+"."+sortClientArr[j].incoming[amCli.ambulWantedIndex] + "."+sortClientArr[j].empty[amCli.ambulWantedIndex]+"."+j+";");
	        				count++;
	        				
	        				if(count==5)
	        				{
	        					break;
	        				}
		            	}
		            	
	            	
		        	
        			
	            	
        		}
        		
        		
        		for(k=count; k<5; k++)
            	{
            		amCli.send("Server.Ack.NULL.0.0.0."+k+";");
            	}
        	}
        	
        	
        	
        }

    }


}
