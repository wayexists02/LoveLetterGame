package Server;
/* 
 * Love Letter Online ver - 0.9.6
 * ���� ������ : 2014�� 6�� 23��
 * ���� �Ϸ��� : 2014�� 9�� 11��
 * ���߱Ⱓ : �� 2.5����
 * ������ : ������[��ȹ, ��Ʈ��ũ ���, ���� �ý��� ����ȭ, ����/Ŭ���̾�Ʈ GUI], ���翵[���� �ý��� �߻�ȭ/����ȭ, Ŭ���̾�Ʈ GUI]
 * ���� ������ : 2014�� 9�� 16��
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
	 * ��� �ʱ�ȭ�� ���⼭ �Ѵ�.
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
					System.out.println("��Ʈ "+3611+"���� ���� �������Դϴ�.\n");
					for(i=0;i<4;i++){
						
						try{
							socket = serverSocket.accept();
							clients = new Clients();
							list.add(clients);
							clients.start();
							System.out.println("Ŭ���̾�Ʈ�� �����߽��ϴ�.");
						}catch(Exception e){
							i--;
							continue;
						}
						
					}
					for(int i=0;i<4;i++)
						list.get(i).setTurn(i);
					System.out.println("��� Ŭ���̾�Ʈ�� �����߽��ϴ�.");
					
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
		return "<html>Ŭ���̾�Ʈ���� <u>�ϸ�ġ ��Ʈ��ũ</u>��<br><u>ȣ��Ʈ IP Address</u>�� �˷��ּ���!</html>";
		
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
		
		//���� ����
		gameLoop : while (true) {
			//�� ����
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
			
			//���� ����
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
				
				
				
				String message = list.get(turn).getGameMessage(); // CARDTYPE (+ #TURN#CARDTYPE (+ #CARDTYPE - ���))
				System.out.println(message);
				String[] msg = message.split("#");
				if (msg.length == 1) {
					//�ó��϶�
					if (msg[0].equals(Cards.HANDMAID)) {
						list.get(0).broadCasting("");
						list.get(turn).putCard(new Cards(msg[0]), false);
						list.get(turn).handmaid();
					}
					//���ۺ����϶�
					else if (msg[0].equals(Cards.COUNTESS)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//�����ϋ�
					else if (msg[0].equals(Cards.PRINCESS)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
						list.get(turn).die();
					}
					
				}
				else {
					int object_turn = new Integer(msg[1]).intValue();
					//���� ��¿�� ���� ���� ����� ����������(��� �óడ �ߵ����̰ų�)
					if (msg[2].equals(Cards.NULL)) {
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//����϶�
					else if (msg[0].equals(Cards.GUARD)) {
						if (!list.get(object_turn).getLife())
							continue;
						if (msg[3].equals(msg[2])) {
							list.get(object_turn).putCard(new Cards(msg[3]), true);
							list.get(object_turn).die();
						}
						
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//�����϶�
					else if (msg[0].equals(Cards.PRIEST)) {
						list.get(turn).send("GAME#PRINT#"+list.get(object_turn).getDeck().get(0).getType()+"#"+object_turn);
						list.get(turn).putCard(new Cards(msg[0]), false);
					}
					//�����϶�
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
					//�����϶�
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
					//���϶�
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
									list.get(i).send("CHAT#�¸������� ť�긦 1�� �߰��մϴ�.");
									list.get(i).addCube();
									turn = i;
								}
							}
						}
						else {
							int idx = winner.get(0).intValue();
							list.get(idx).send("CHAT#�¸������� ť�긦 1�� �߰��մϴ�.");
							list.get(idx).addCube();
							turn = idx;
						}
						
						break roundLoop;
					}
					else {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getLife()) {
								list.get(i).send("CHAT#�¸������� ť�긦 �߰��մϴ�.");
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
						list.get(i).send("CHAT#���� ���Դϴ�.");
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// �ѻ���� ť�� 4���̸� gameLoop Ż��
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
	//���ξ����� ����
	public void suspended() { isSuspended = true; }
	//���ξ����� ���
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
		//�ش� �÷��̾ ������ �ִ� ��(�ִ� 2)
		private ArrayList<Cards> deck = new ArrayList<Cards>();
		//�ش� �÷��̾ �ش� ���忡�� ����ִ��� �׾��ִ���
		private boolean isAlive = true;
		//�ó��� ȿ�� �ߵ�����
		private boolean handmaid = false;
		//�ش� �÷��̾��� ���� �Ǿ� GAME�޼��� ó�� ����
		private boolean turn = false;
		//�ش� �÷��̾ ������ �ִ� ť�� ����
		private int number_cube = 0;
		//���� �ĺ� ��ȣ
		private int turn_number;
		//Game#(ī������)#...���� Game#�� ����(tail)
		private String tail = null;
		private String game_message = "";
		private boolean isReady = false;
		
		public void setTurn(int num){
			turn_number = num;
		}
		
		public void removeAll(){
			deck = new ArrayList<Cards>();
		}
		
		//ori�� ù��° ��Ҹ� �����ϰ� �������� �̾�ٿ� tail�� �����.
		private String parser(String[] ori){
			String str="";
			for(int i=1;i<ori.length;i++)
				str=str+"#"+ori[i];
			
			//������ ����
			str.replaceAll("SYSTEM", "System");
			str.replaceAll("CHAT", "Chat");
			str.replaceAll("GAME", "Game");
			return str.trim().substring(1);
		}
		
		public void run() {
			try {
				ois = new DataInputStream(socket.getInputStream());
				oos = new DataOutputStream(socket.getOutputStream());
				
				send("SYSTEM#ENTRY");//Ŭ���̾�Ʈ�� �����ϸ� SYSTEM#ENTRY�� �۽��Ѵ�.
				if(i==0)
					send("SYSTEM#TURN");
				String message = null;
				while(true){
					message = (String) ois.readUTF();
					System.out.println("Ŭ���̾�Ʈ�� ���� ���� �޼��� : "+message);
					String msg[]=message.split("#");
					tail = parser(msg);
					/******************************************************
					 
					 ���� �޼����� ������ ���� ���⼭ ó���Ѵ�.
					 1. SYSTEM
					 	- 1. EXIT (�������� ���� - �Ѹ� ������ �� ƨ��°Ŵ�.)
					 		SYSTEM#RESET �� ��ε�ĳ�����Ѵ�.(��� �ñų���.)
					 		lsg.jbt_server.setEnabled(true);
					 	- 2. READY
					 		cnt_ready�� ������Ų��.
					 		cnt_ready�� 4��� lsg.jbt_start.setEnabled(true)
					 	- 3. UNREADY
					 		cnt_ready�� ���ҽ�Ų��.
					 		lsg.jbt_start.setEnabled(false)
					 		
					 2. CHAT
					 	- �̰�� �׳� ä������ ó��.
					 	
					 3. GAME
					 	- ���ӿ� ���õ� �޼���.
					  
					 ******************************************************/
					
					if(msg[0].equals("SYSTEM")){ // ���� �޼����� SYSTEM �޼����� ���
						
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
							System.out.println("�� �� ���� SYSTEM �޼���");
						}
						
					}
					
					
					
					else if(msg[0].equals("CHAT")){ // ���� �޼����� CHAT �޼����� ���
						broadCasting("CHAT#"+tail);//tail�� SYSTEM�� CHAT�� �˿��� ���ڿ�
					}
					
					
					else if(msg[0].equals("GAME")){
						if (!isAlive) continue;
						
						//���϶��� �޴´�
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
							if (msg.length == 4) { //���
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
							
							handmaid = false; // �ó�Ư�� ����
							resumed(); // ���� ������(?) ���
						}
					}
					
					
					else {//�Ѵ� �ƴѰ��
						System.out.println("SYSTEM,CHAT�� �ƴ� �޼����� �����Ǿ����ϴ�.(���������޼���)");
						System.out.println("�޼��� ���� : "+message);
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
		//�ش� �÷��̾��� ���ΰ�? �ƴϸ� ����(GAME#)
		public void endTurn() {
			turn = false;
			send("GAME#TURN#FALSE");
		}
		//�ش� �÷��̾��� ���ΰ�? ������ ���(GAME#)
		public void startTurn() {
			if (!isAlive) return;
			for (int i = 0; i < deck.size(); i++)
				send("GAME#" + deck.get(i).getType());
			send("GAME#TURN#TRUE");
			send("CHAT#����� ���Դϴ�.");
			turn = true;
		}
		public int getCube() {
			return number_cube;
		}
		/**
		 * ť�긦 ���Ѵ�
		 * @return ť�긦 ���Ҽ� �ִ����� ����
		 */
		public boolean addCube() {
			number_cube++;
			broadCasting("GAME#CUBE#" + turn_number);
			if (number_cube >= 4) return false;
			return true;
		}
		/**
		 * ���尡 ó�� �����Ҷ�
		 */
		public void restart() {
			isAlive = true;
			handmaid = false;
			turn = false;
			for (int i = 0; i < deck.size(); i++) deck.remove(0);
			send("SYSTEM#RESTART");
		}
		/**
		 * �ش� ���忡�� ����ִ��� ����
		 * @return �ش� ���忡�� ����ִ��� ����
		 */
		public boolean getLife() {
			return isAlive;
		}
		/**
		 * �ش� �÷��̾ ���δ�.
		 */
		public void die() {
			isAlive =  false;
			for (int i = 0; i < deck.size(); i++) putCard(deck.get(0), false);
			broadCasting("GAME#DEAD#" + turn_number);
		}
		/**
		 * ī�带 �̾� �п� �ִ´�
		 * @param c ��ο��� ī�� 
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
		 * �ó� �ߵ����� ����
		 * @return �ó� �ߵ�����
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
