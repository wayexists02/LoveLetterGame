package Server;
/* 
 * Love Letter Online ver - 0.9.6
 * 개발 시작일 : 2014년 6월 23일
 * 개발 완료일 : 2014년 9월 11일
 * 개발기간 : 약 2.5개월
 * 개발자 : 안윤근[기획, 네트워크 통신, 게임 시스템 구현화, 서버/클라이언트 GUI], 이재영[게임 시스템 추상화/구현화, 클라이언트 GUI]
 * 최종 수정일 : 2014년 9월 16일
 * */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Data.CardDeck;
import Data.Cards;
import Data.SubDeck;


public class LoveLetter_Server implements ActionListener{

	private ArrayList<Clients> list;
	private ServerSocket serverSocket;
	private Socket socket;
	public LoveLetter_Server(){
		try {
			showGUI();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	/*
	 * 모든 초기화는 여기서 한다.
	 * */
	private void init(){
		list = new ArrayList<Clients>();
		cnt_ready=0;
	}
	
	int i=0;
	Thread s;
	private void startServer(){
		
		init();
		
		
		s = new Thread(){
			public void run(){
				try {
					serverSocket = new ServerSocket(3611);
					Clients clients = null;
					System.out.println("포트 "+3611+"서버 정상 실행중입니다.\n");
					for(i=0;i<4;i++){
						
						try{
							socket = serverSocket.accept();
							clients = new Clients();
							list.add(clients);
							clients.start();
							System.out.println("클라이언트가 접속했습니다.");
						}catch(Exception e){
							i--;
							continue;
						}
						
					}
					for(int i=0;i<4;i++)
						list.get(i).setTurn(i);
					System.out.println("모든 클라이언트가 접속했습니다.");
					
				} catch (IOException e) {
					list.clear();
					this.interrupt();
				}
			}
		};
		s.start();
		
	}
	
	
	
	LoveLetter_Server_GUI lsg;
	private void showGUI() throws UnknownHostException {
		lsg = new LoveLetter_Server_GUI();
		String ip = getLocalServerIp().trim();
		lsg.jlb_ip.setText(ip);
		lsg.jbt_server.addActionListener(this);
		lsg.jbt_start.addActionListener(this);
		lsg.jbt_end.addActionListener(this);
		
		lsg.jbt_start.setEnabled(false);
	}
	private String getLocalServerIp()
	{
		/*URL findpublicip;
		BufferedReader in;
		String ip="";
		
		try {
			findpublicip = new URL("http://checkip.dyndns.org/");
			in = new BufferedReader(new InputStreamReader(findpublicip.openStream()));
			ip = in.readLine();
		} catch (IOException e) {return "Can't Find your public IP";}
		return ip;*/
		return "<html>클라이언트에게 <u>하마치 네트워크</u>의<br><u>호스트 IP Address</u>를 알려주세요!</html>";
		
	}
	Thread g;
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object o = ae.getSource();
		if(o.equals(lsg.jbt_server)){ startServer(); lsg.jbt_server.setEnabled(false);}
		if(o.equals(lsg.jbt_start)){
			lsg.jbt_start.setEnabled(false);
			g = new Thread(){
				public void run(){
					try{
						startGame();
					}finally{
						init();
						//lsg.jbt_start.setEnabled(true);
					}
				}
			};
			g.start();
		}
		if(o.equals(lsg.jbt_end)){
			try{
				list.get(0).broadCasting("SYSTEM#RESET");
				
			}catch(Exception e){
				System.exit(0);
			}
			System.exit(0);
		}
	}
	
	
	
	private SubDeck[] sub_deck;
	private ArrayList<Cards> prince_deck;
	
	private int cnt_ready;
	private boolean isSuspended = false;
	private void startGame(){	
		for (int i = 0; i < list.size(); i++)
			list.get(i).send("SYSTEM#START#" + list.get(i).getPrivateNumber());
		
		
		int round = 1;
		int turn = 0;
		
		//게임 루프
		gameLoop : while (true) {
			//덱 생성
			CardDeck deck = new CardDeck();
			sub_deck = new SubDeck[list.size()];
			prince_deck = new ArrayList<Cards>();
			
			for (int i = 0; i < list.size(); i++) {
				Cards card = deck.drawCard();
				list.get(0).broadCasting("GAME#DECK#" + deck.getCount());
				list.get(i).handmaid = false;
				list.get(i).addCard(card);
				list.get(i).broadCasting("GAME#DRAW#" + i + "#" + card.getType());
				sub_deck[i] = new SubDeck();
			}
			
			//라운드 루프
			roundLoop : while (true) {

				if (!list.get(turn).isTurn()) {
					Cards card = deck.drawCard();
					list.get(0).broadCasting("GAME#DECK#" + deck.getCount());
					list.get(turn).addCard(card);
					list.get(turn).broadCasting("GAME#DRAW#" + turn + "#" + card.getType());
				}
				
				list.get(turn).startTurn();
				suspended();
				
				while (isSuspended) {
					Thread.yield();
				}
				
				
				
				String message = list.get(turn).getGameMessage(); // CARDTYPE (+ #TURN#CARDTYPE (+ #CARDTYPE - 경비병))
				System.out.println(message);
				String[] msg = message.split("#");
				if (msg.length == 1) {
					//시녀일때
					if (msg[0].equals(Cards.HANDMAID)) {
						list.get(0).broadCasting("");
						list.get(turn).putCard(new Cards(msg[0]), false);
						list.get(turn).handmaid();
					}
					//백작부인일때
					else if (msg[0].equals(Cards.COUNTESS)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//공주일떄
					else if (msg[0].equals(Cards.PRINCESS)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
						list.get(turn).die();
					}
					
				}
				else {
					int object_turn = new Integer(msg[1]).intValue();
					//만약 어쩔수 없이 죽은 사람을 선택했을때(모두 시녀가 발동중이거나)
					if (msg[2].equals(Cards.NULL)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//경비병일때
					else if (msg[0].equals(Cards.GUARD)) {
						if (!list.get(object_turn).getLife())
							continue;
						if (msg[3].equals(msg[2])) {
							list.get(object_turn).putCard(new Cards(msg[3]), true);
							list.get(object_turn).die();
						}
						
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//사제일때
					else if (msg[0].equals(Cards.PRIEST)) {
						list.get(turn).send("GAME#PRINT#"+list.get(object_turn).getDeck().get(0).getType()+"#"+object_turn);
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//남작일때
					else if (msg[0].equals(Cards.BARON)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
						int obj = new Cards(msg[2]).getPower();
						String type = list.get(turn).getDeck().get(0).getType();
						int me = new Cards(type).getPower();
						if(obj < me)
							list.get(object_turn).die();
						if(obj > me)
							list.get(turn).die();
						list.get(turn).send("GAME#PRINT#BARON#"+msg[2]+"#"+type);
						list.get(object_turn).send("GAME#PRINT#BARON#"+type+"#"+msg[2]);
					}
					//왕자일때
					else if (msg[0].equals(Cards.PRINCE)) {
						
						list.get(turn).putCard(new Cards(msg[0]), false);
					
						Cards c = list.get(object_turn).getDeck().get(0);
						prince_deck.add(c);
						list.get(object_turn).getDeck().remove(c);
						
						Cards draw;
						
						if (deck.getCount() == 0) {
							draw = prince_deck.get(0);
							list.get(object_turn).getDeck().add(draw);
							prince_deck.remove(draw);
						}
						else {
							draw = deck.drawCard();
							list.get(0).broadCasting("GAME#DECK#" + deck.getCount());
							list.get(object_turn).getDeck().add(draw);
						}
						
						list.get(0).broadCasting("GAME#PRINCE#" + object_turn + "#" + draw.getType());
					}
					//왕일때
					else if (msg[0].equals(Cards.KING)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
						String type = list.get(turn).getDeck().get(0).getType();
						Cards obj = new Cards(msg[2]);
						Cards me = new Cards(type);
						list.get(turn).removeAll();
						list.get(turn).getDeck().add(obj);
						list.get(object_turn).removeAll();
						list.get(object_turn).getDeck().add(me);
						
						list.get(0).broadCasting("GAME#CHANGE#"+turn+"#"+object_turn);
					}
				}
				
				list.get(turn).broadCasting("GAME#DECK#" + deck.getCount());
				list.get(turn).endTurn();
				
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int numAlive = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getLife())
						numAlive++;
				}
				if (deck.getCount() == 0 || numAlive == 1) {
					if (numAlive >= 2) {
						int temp_power = 0;
						int[] temp = new int[4];
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getLife()) {
								temp[i] = list.get(i).getDeck().get(0).getPower();
								if (temp_power < temp[i])
									temp_power = temp[i];
							}
							else {
								temp[i] = 0;
							}
						}
						ArrayList<Integer> winner = new ArrayList<Integer>();
						for (int i = 0; i < list.size(); i++) {
							if (temp_power == temp[i])
								winner.add(new Integer(i));
						}
						if (winner.size() > 1) {
							temp = new int[4]; temp_power = 0;
							for (int i = 0; i < winner.size(); i++) {
								int idx = winner.get(i).intValue();
								temp[idx] = sub_deck[idx].getCard().getPower();
								if (temp_power < temp[idx])
									temp_power = temp[idx];
							}
							for (int i = 0; i < winner.size(); i++) {
								if (temp_power == temp[i]) {
									list.get(i).send("CHAT#승리했으니 큐브를 1개 추가합니다.");
									list.get(i).addCube();
									turn = i;
								}
							}
						}
						else {
							int idx = winner.get(0).intValue();
							list.get(idx).send("CHAT#승리했으니 큐브를 1개 추가합니다.");
							list.get(idx).addCube();
							turn = idx;
						}
						
						break roundLoop;
					}
					else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getLife()) {
								list.get(i).send("CHAT#승리했으니 큐브를 추가합니다.");
								list.get(i).addCube();
								turn = i;
								break roundLoop;
							}
						}
						break;
					}
				}
				
				do {
					if (++turn >= list.size())
						turn = 0;
				} while (!list.get(turn).getLife());
				
				for (int i = 0; i < list.size(); i++) {
					if (i != turn)
						list.get(i).send("CHAT#다음 턴입니다.");
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// 한사람이 큐브 4개이면 gameLoop 탈출
			for (int i = 0; i < list.size(); i++)
				if (list.get(i).getCube() == 4)
					break gameLoop;
			round++;
			
			for (int i = 0; i < list.size(); i++)
				list.get(i).restart();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		for (int i = 0; i < list.size(); i++) {
			if (i == turn)
				list.get(i).send("GAME#VICTORY");
			else
				list.get(i).send("GAME#LOSE");
		}
		reset();
		
	}
	//메인쓰레드 중지
	public void suspended() { isSuspended = true; }
	//메인쓰레드 재생
	public void resumed() { isSuspended = false; }
	
	public void reset(){
		
		lsg.jbt_start.setEnabled(false);
		lsg.jbt_server.setEnabled(true);
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		i = 0;
		cnt_ready = 0;
		for(int i=0;i<4;i++)
			list.get(i).number_cube=0;
		g.stop();
		s.stop();
		
	}
	
	class Clients extends Thread {
		private DataInputStream ois;
		private DataOutputStream oos;
		//해당 플레이어가 가지고 있는 덱(최대 2)
		private ArrayList<Cards> deck = new ArrayList<Cards>();
		//해당 플레이어가 해당 라운드에서 살아있는지 죽어있는지
		private boolean isAlive = true;
		//시녀의 효과 발동여부
		private boolean handmaid = false;
		//해당 플레이어의 턴이 되야 GAME메세지 처리 진행
		private boolean turn = false;
		//해당 플레이어가 가지고 있는 큐브 갯수
		private int number_cube = 0;
		//개인 식별 번호
		private int turn_number;
		//Game#(카드종류)#...에서 Game#을 뺀것(tail)
		private String tail = null;
		private String game_message = "";
		private boolean isReady = false;
		
		public void setTurn(int num){
			turn_number = num;
		}
		
		public void removeAll(){
			deck = new ArrayList<Cards>();
		}
		
		//ori의 첫번째 요소를 제외하고 나머지를 이어붙여 tail을 만든다.
		private String parser(String[] ori){
			String str="";
			for(int i=1;i<ori.length;i++)
				str=str+"#"+ori[i];
			
			//인젝션 방지
			str.replaceAll("SYSTEM", "System");
			str.replaceAll("CHAT", "Chat");
			str.replaceAll("GAME", "Game");
			return str.trim().substring(1);
		}
		
		public void run() {
			try {
				ois = new DataInputStream(socket.getInputStream());
				oos = new DataOutputStream(socket.getOutputStream());
				
				send("SYSTEM#ENTRY");//클라이언트가 접속하면 SYSTEM#ENTRY를 송신한다.
				if(i==0)
					send("SYSTEM#TURN");
				String message = null;
				while(true){
					message = (String) ois.readUTF();
					System.out.println("클라이언트로 부터 받은 메세지 : "+message);
					String msg[]=message.split("#");
					tail = parser(msg);
					/******************************************************
					 
					 받은 메세지의 종류에 따라서 여기서 처리한다.
					 1. SYSTEM
					 	- 1. EXIT (막장으로 가자 - 한명 나가면 다 튕기는거다.)
					 		SYSTEM#RESET 를 브로드캐스팅한다.(모두 팅궈낸다.)
					 		lsg.jbt_server.setEnabled(true);
					 	- 2. READY
					 		cnt_ready를 증가시킨다.
					 		cnt_ready가 4라면 lsg.jbt_start.setEnabled(true)
					 	- 3. UNREADY
					 		cnt_ready를 감소시킨다.
					 		lsg.jbt_start.setEnabled(false)
					 		
					 2. CHAT
					 	- 이경우 그냥 채팅으로 처리.
					 	
					 3. GAME
					 	- 게임에 관련된 메세지.
					  
					 ******************************************************/
					
					if(msg[0].equals("SYSTEM")){ // 받은 메세지가 SYSTEM 메세지인 경우
						
						if(tail.equals("EXIT")){
							broadCasting("SYSTEM#RESET");
							reset();
							break;
						}
						else if(tail.equals("READY")){
							cnt_ready++;
							isReady = true;
							boolean flag=true;
							if(list.size()==4){
								for(int l = 0 ; l<list.size(); l++)
									flag = flag && list.get(l).isReady;
								if(flag) lsg.jbt_start.setEnabled(true);
							}
						}
						else if(tail.equals("UNREADY")){
							cnt_ready--;
							isReady = false;
							lsg.jbt_start.setEnabled(false);
						}
						
						
						
						else{
							System.out.println("알 수 없는 SYSTEM 메세지");
						}
						
					}
					
					
					
					else if(msg[0].equals("CHAT")){ // 받은 메세지가 CHAT 메세지인 경우
						broadCasting("CHAT#"+tail);//tail은 SYSTEM과 CHAT을 검열한 문자열
					}
					
					
					else if(msg[0].equals("GAME")){
						if (!isAlive) continue;
						
						//턴일때만 받는다
						else if (turn) {
							if (msg.length == 2 && (msg[1].equals(Cards.GUARD) || msg[1].equals(Cards.PRIEST)
									|| msg[1].equals(Cards.BARON) || msg[1].equals(Cards.PRINCE) || msg[1].equals(Cards.KING))) {
								String input_message = "GAME#" + msg[1] + "#INPUT";
								if (msg[1].equals(Cards.GUARD)) {
									input_message += "#TRUE#ALIVE";
									for (int i = 0; i < 4; i++) {
										if (i == turn_number)
											input_message += "#FALSE";
										else if (i < list.size() && list.get(i).getLife() && !list.get(i).handmaidProperty())
											input_message += "#TRUE";
										else
											input_message += "#FALSE";
									}
								}
								
								else {
									input_message += "#FALSE#ALIVE";
									for (int i = 0; i < 4; i++) {
										if (i == turn_number && msg[1].equals(Cards.PRINCE))
											input_message += "#TRUE";
										else if (i == turn_number)
											input_message += "#FALSE";
										else if (i < list.size() && list.get(i).getLife() && !list.get(i).handmaidProperty())
											input_message += "#TRUE";
										else
											input_message += "#FALSE";
									}
								}
								send(input_message);
								continue;
							}
							game_message = "";
							game_message += tail;
							if (msg.length == 4) { //경비병
								int objective = new Integer(msg[2]).intValue();
								if (msg[3].equals(Cards.NULL))
									game_message = game_message + "#" + Cards.NULL;
								else
									game_message = game_message + "#" + list.get(objective).getDeck().get(0).getType();
							}
							if (msg.length == 3) { 
								int objective = new Integer(msg[2]).intValue();
								game_message = game_message + "#" + list.get(objective).getDeck().get(0).getType();
							}
							
							//debug
							System.out.println(game_message);
							
							if (msg.length == 2) {
								broadCasting("GAME#CHAT#" + turn_number + "#" + turn_number + "#" + msg[1]);
							}
							else {
								if (msg.length == 4) {
									broadCasting("GAME#CHAT#" + turn_number + "#" + msg[2] + "#" + msg[1] + "#" + msg[3]);
								}
								else {
									broadCasting("GAME#CHAT#" + turn_number + "#" + msg[2] + "#" + msg[1]);
								}
							}
							
							handmaid = false; // 시녀특성 중지
							resumed(); // 메인 쓰레드(?) 재생
						}
					}
					
					
					else {//둘다 아닌경우
						System.out.println("SYSTEM,CHAT이 아닌 메세지가 감지되었습니다.(비정상적메세지)");
						System.out.println("메세지 원본 : "+message);
					}
				}
				list.remove(this);
			} catch (Exception e) {
				list.clear();
			}
		}
		public void broadCasting(String message) {
			for (int i=0;i<list.size();i++) {
				list.get(i).send(message);
				System.out.println(message);
			}
		}

		public void send(String message) {
			try {
				oos.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void victory() {
			send("GAME#VICTORY");
		}
		public void lose() {
			send("GAME#LOSE");
		}
		//해당 플레이어의 턴인가? 아니면 정지(GAME#)
		public void endTurn() {
			turn = false;
			send("GAME#TURN#FALSE");
		}
		//해당 플레이어의 턴인가? 맞으면 재생(GAME#)
		public void startTurn() {
			if (!isAlive) return;
			for (int i = 0; i < deck.size(); i++)
				send("GAME#" + deck.get(i).getType());
			send("GAME#TURN#TRUE");
			send("CHAT#당신의 턴입니다.");
			turn = true;
		}
		public int getCube() {
			return number_cube;
		}
		/**
		 * 큐브를 더한다
		 * @return 큐브를 더할수 있는지의 여부
		 */
		public boolean addCube() {
			number_cube++;
			broadCasting("GAME#CUBE#" + turn_number);
			if (number_cube >= 4) return false;
			return true;
		}
		/**
		 * 라운드가 처음 시작할때
		 */
		public void restart() {
			isAlive = true;
			handmaid = false;
			turn = false;
			for (int i = 0; i < deck.size(); i++) deck.remove(0);
			send("SYSTEM#RESTART");
		}
		/**
		 * 해당 라운드에서 살아있는지 리턴
		 * @return 해당 라운드에서 살아있는지 여부
		 */
		public boolean getLife() {
			return isAlive;
		}
		/**
		 * 해당 플레이어를 죽인다.
		 */
		public void die() {
			isAlive =  false;
			for (int i = 0; i < deck.size(); i++) putCard(deck.get(0), false);
			broadCasting("GAME#DEAD#" + turn_number);
		}
		/**
		 * 카드를 뽑아 패에 넣는다
		 * @param c 드로우한 카드 
		 */
		public void addCard(Cards c) {
			deck.add(c);
			send("GAME#" + c.getType());
		}
		public void putCard(Cards c, boolean isGuard) {
			for (int i = 0; i < deck.size(); i++) {
				if (deck.get(i).getType().equals(c.getType())) {
					sub_deck[turn_number].putCard(deck.get(i));
					deck.remove(i);
					if (isGuard)
						broadCasting("GAME#FLIP#" + turn_number + "#" + c.getType() + "#TRUE#TRUE");
					else
						broadCasting("GAME#FLIP#" + turn_number + "#" + c.getType() + "#FALSE#TRUE");
					return;
				}
			}
		}
		public ArrayList<Cards> getDeck() {
			return deck;
		}
		/**
		 * 시녀 발동여부 리턴
		 * @return 시녀 발동여부
		 */
		public boolean handmaidProperty() {
			return handmaid;
		}
		public void handmaid() {
			handmaid = true;
		}
		public boolean isTurn() {
			return turn;
		}
		public String getGameMessage() {
			return game_message;
		}
		public int getPrivateNumber() {
			return turn_number;
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new LoveLetter_Server();
	}

}
