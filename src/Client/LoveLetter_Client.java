package Client;

/* 
 * Love Letter Online ver - 0.9.6
 * ���� ������ : 2014�� 6�� 23��
 * ���� �Ϸ��� : 2014�� 9�� 11��
 * ���߱Ⱓ : �� 2.5����
 * ������ : ������[��ȹ, ��Ʈ��ũ ���, ���� �ý��� ����ȭ, ����/Ŭ���̾�Ʈ GUI], ���翵[���� �ý��� �߻�ȭ/����ȭ, Ŭ���̾�Ʈ GUI]
 * ���� ������ : 2014�� 9�� 16��
 * */

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Data.Cards;


public class LoveLetter_Client implements ActionListener, KeyListener, WindowListener{

	
	LoveLetter_Client_GUI lcg;
	Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	//�ڱ� �ĺ���ȣ
	private int private_num;
	//ť�갹��
	private int[] number_cube = {0, 0, 0, 0};
	//�ڱ����ΰ�?
	private boolean turn = false;
	//�ڱ� ���̶� ī�带 �ϴ� ���� ī�带 �ݺ��ؼ� �� �� ����.
	private boolean waiting = false;
	
	public LoveLetter_Client() throws IOException {
		
		lcg = new LoveLetter_Client_GUI();
		lcg.jbt_entry.addActionListener(this);
		lcg.jtf_chat_chat.addKeyListener(this);
		lcg.lcgg.jbt_ready.addActionListener(this);
		lcg.addWindowListener(this);
		
		
		
		
		///Debugging
		//lcg.lcgg.set_JP_Game();
		
		
		
	}
	
	private String parser(String[] ori){
		String str="";
		for(int i=1;i<ori.length;i++) str+=ori[i];
		return str;
	}
	Thread c;
	public void reset(){
		
		number_cube[0] = 0;
		number_cube[1] = 0;
		number_cube[2] = 0;
		number_cube[3] = 0;
		
		try {
			socket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//
		isready = false;
		
		try{
			lcg.lcgg.card_center_south.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_center_north.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_center_east.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_center_west.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
			//lcg.lcgg.card_center_discard.setCard(new Cards(Cards.NULL));
			lcg.lcgg.card_center_prince.setCard(new Cards(Cards.NULL));
			lcg.lcgg.setDeckCount(16);
		}catch(Exception e){
			lcg.lcgg.set_JP_Ready();
			lcg.lcgg.jbt_ready.setEnabled(false);
			lcg.repaint();
		}
		
		//
		isready = false;
		lcg.jbt_entry.setEnabled(true);
		lcg.jtf_entry.setEditable(true);
		lcg.jtf_entry.setEnabled(true);
		lcg.lcgg.set_JP_Ready();
		lcg.lcgg.jbt_ready.setEnabled(false);
		lcg.repaint();
		
	}
	private void clientStart(final String ip){
		lcg.jbt_entry.setEnabled(false);
		c = new Thread(){
			
			public void run(){
		
				try {
					socket = new Socket(ip,3611);
					System.out.println("����("+socket.getLocalAddress()+")�� �����մϴ�...\n");
					lcg.jta_chat.setText("����("+socket.getLocalAddress()+")�� �����մϴ�...");
					in=new DataInputStream(socket.getInputStream());
					out=new DataOutputStream(socket.getOutputStream());
					while(in!=null){
						
						String message="";
						message=in.readUTF();
						String[] msg = message.split("#");
						String tail = parser(msg);
						
						/***********************************************
							���⼭���� ���� �޼��� ó��
							1. SYSTEM
								- 1. ENTRY
									�����г��� "Ready?" ��ư�� Ȱ��ȭ ��Ų��.
									IP Address �Է� â�� ��ư�� ��Ȱ��ȭ��Ų��.
								- 2. RESET
									������.
								- 3. START
									������ �����Ѵ�.
							2. CHAT
								�׳� ���.
							3. GAME
								���ӿ� ���õ� �޼����� ó���Ѵ�.
						***********************************************/
						if(msg[0].equals("SYSTEM")){
							
							if(tail.equals("ENTRY")){
								System.out.println("����("+socket.getLocalAddress()+")�� ����Ǿ����ϴ�.");
								lcg.jta_chat.setText("����("+socket.getLocalAddress()+")�� ����Ǿ����ϴ�.\n");
								lcg.lcgg.jbt_ready.setEnabled(true);
								
							}
							if(tail.equals("RESET")){
								JOptionPane.showMessageDialog(lcg, "<html>� �÷��̾ �������ϴ�. <br>������ �����մϴ�.</html>");
								reset();
								
							}
							if(msg[1].equals("START")){
								lcg.lcgg.set_JP_Game();
								private_num = new Integer(msg[2]).intValue();
								addActionListenerToCard();
							}
							if (tail.equals("RESTART")) {
								lcg.lcgg.card_center_south.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_center_north.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_center_east.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_center_west.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
								//lcg.lcgg.card_center_discard.setCard(new Cards(Cards.NULL));
								lcg.lcgg.card_center_prince.setCard(new Cards(Cards.NULL));
								lcg.lcgg.setDeckCount(16);
							}
						}
						else if(msg[0].equals("CHAT")){
							System.out.println(tail);
							lcg.jta_chat.append(tail + "\n");
							lcg.jta_chat.setCaretPosition(lcg.jta_chat.getDocument().getLength());
						}
						else if(msg[0].equals("GAME")) {
							if (msg.length == 3 && msg[1].equals("TURN")) {
								if (msg[2].equals("TRUE")) {
									JOptionPane.showMessageDialog(null, "����� ���Դϴ�!");
									turn = true; 
									waiting = false;
								}
								else if (msg[2].equals("FALSE")) {
									turn = false; 
								}
							}
							else if (msg.length == 2 && msg[1].equals("VICTORY")) {
								JOptionPane.showMessageDialog(null, "����� ����߽��ϴ�!");
								reset();
								continue;
							}
							else if (msg.length == 2 && msg[1].equals("LOSE")) {
								JOptionPane.showMessageDialog(null, "����� �й��߽��ϴ�...");
								reset();
								continue;
							}
							else if (msg.length == 3 && msg[1].equals("CUBE")) {
								int objective = new Integer(msg[2]).intValue() - private_num;
								if (objective < 0)
									objective += 4;
								
								switch (objective) {
								case 0 :
									lcg.lcgg.cubes_south.setCubes(++number_cube[0]);
									break;
								case 1 :
									lcg.lcgg.cubes_east.setCubes(++number_cube[1]);
									break;
								case 2 :
									lcg.lcgg.cubes_north.setCubes(++number_cube[2]);
									break;
								case 3 :
									lcg.lcgg.cubes_west.setCubes(++number_cube[3]);
									break;
								}
							}
							else if (msg.length == 3 && msg[1].equals("DEAD")) {
								
								String[] who = new String[] {"���", "East", "North", "West"};
								
								int objective = new Integer(msg[2]).intValue() - private_num;
								if (objective < 0)
									objective += 4;
								
								switch (objective) {
								case 0 :
									lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
									lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
									break;
								case 1 :
									lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
									lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
									break;
								case 2 :
									lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
									lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
									break;
								case 3 :
									lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
									lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
									break;
								}
								lcg.jta_chat.append(who[objective] + "�� �׾����ϴ�.\n");
							}
							else if (msg.length == 3 && msg[1].equals("DECK")) {
								int deckCount = Integer.parseInt(msg[2]);
								lcg.lcgg.setDeckCount(deckCount);
							}
							else if ((msg.length == 5 || msg.length == 6) && msg[1].equals("CHAT")) {
								String[] who = new String[] {"���", "East", "North", "West"};
								
								int from = Integer.parseInt(msg[2]) - private_num;
								int to = Integer.parseInt(msg[3]) - private_num;
								
								if (from < 0) from += 4;
								if (to < 0) to += 4;
								
								if (from == to) {
									lcg.jta_chat.append(who[from] + "�� " + msg[4] + "�� ����߽��ϴ�.\n");
								}
								else {
									lcg.jta_chat.append(who[from] + "�� " + who[to] + "���� " + msg[4] + "�� ����߽��ϴ�.\n");
								}
								
								if (msg[4].equals(Cards.GUARD)) {
									lcg.jta_chat.append(who[from] + "�� " + msg[5] + "�� ������ϴ�.\n");
								}
								else if (msg[4].equals(Cards.HANDMAID)) {
									lcg.jta_chat.append(who[from] + "�� 1�ϵ��� � ī���� ���⵵ ���� �ʽ��ϴ�.\n");
								}
								else if (msg[4].equals(Cards.PRINCE)) {
									lcg.jta_chat.append(who[to] + "�� ī�带 ��ο��մϴ�.\n");
								}
								else if (msg[4].equals(Cards.KING)) {
									lcg.jta_chat.append(who[from] + "�� " + who[to] + "�� ī�带 �ٲߴϴ�.\n");
								}
							}
							else if (msg.length == 4 && msg[1].equals("CHANGE")) {
								int turn1 = Integer.parseInt(msg[2]) - private_num;
								int turn2 = Integer.parseInt(msg[3]) - private_num;
								
								if (turn1 < 0) turn1 += 4;
								if (turn2 < 0) turn2 += 4;
								
								String type1 = Cards.NULL, type2 = Cards.NULL;
								
								switch (turn1) {
								case 0 :
									switch (turn2) {
									case 1 :
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_south1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.flip();
										lcg.lcgg.card_east1.setCard(new Cards(type1));
										break;
									case 2 :
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_south1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.flip();
										lcg.lcgg.card_north1.setCard(new Cards(type1));
										break;
									case 3 :
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_south1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.flip();
										lcg.lcgg.card_west1.setCard(new Cards(type1));
										break;
									}
									break;
								case 1 :
									switch (turn2) {
									case 0 :
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_east1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.setCard(new Cards(type1));
										lcg.lcgg.card_south1.flip();
										break;
									case 2 :
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_east1.setCard(new Cards(type2));
										lcg.lcgg.card_north1.setCard(new Cards(type1));
										break;
									case 3 :
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_east1.setCard(new Cards(type2));
										lcg.lcgg.card_west1.setCard(new Cards(type1));
										break;
									}
									break;
								case 2 :
									switch (turn2) {
									case 0 :
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_north1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.setCard(new Cards(type1));
										lcg.lcgg.card_south1.flip();
										break;
									case 1 :
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_north1.setCard(new Cards(type2));
										lcg.lcgg.card_east1.setCard(new Cards(type1));
										break;
									case 3 :
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_north1.setCard(new Cards(type2));
										lcg.lcgg.card_west1.setCard(new Cards(type1));
										break;
									}
									break;
								case 3 :
									switch (turn2) {
									case 0 :
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_south1.getCard().getType();
											lcg.lcgg.card_south1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_south2.getCard().getType();
											lcg.lcgg.card_south2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_west1.setCard(new Cards(type2));
										lcg.lcgg.card_south1.setCard(new Cards(type1));
										lcg.lcgg.card_south1.flip();
										break;
									case 1 :
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_east1.getCard().getType();
											lcg.lcgg.card_east1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_east2.getCard().getType();
											lcg.lcgg.card_east2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_west1.setCard(new Cards(type2));
										lcg.lcgg.card_east1.setCard(new Cards(type1));
										break;
									case 2 :
										if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
											type1 = lcg.lcgg.card_west1.getCard().getType();
											lcg.lcgg.card_west1.setCard(new Cards(Cards.NULL));
										}
										else {
											type1 = lcg.lcgg.card_west2.getCard().getType();
											lcg.lcgg.card_west2.setCard(new Cards(Cards.NULL));
										}
										if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
											type2 = lcg.lcgg.card_north1.getCard().getType();
											lcg.lcgg.card_north1.setCard(new Cards(Cards.NULL));
										}
										else {
											type2 = lcg.lcgg.card_north2.getCard().getType();
											lcg.lcgg.card_north2.setCard(new Cards(Cards.NULL));
										}
										lcg.lcgg.card_west1.setCard(new Cards(type2));
										lcg.lcgg.card_north1.setCard(new Cards(type1));
										break;
									}
									break;
								}
								//lcg.lcgg.sort();
								
							}
							else if (msg.length == 4 && msg[1].equals("PRINCE")) {
								int object_turn = Integer.parseInt(msg[2]) - private_num;
								if (object_turn < 0) object_turn += 4;
								
								switch (object_turn) {
								case 0 :
									if (!lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_south1.getCard().getType()));
										lcg.lcgg.card_south1.setCard(new Cards(msg[3]));
										lcg.lcgg.card_south1.flip();
									}
									else {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_south2.getCard().getType()));
										lcg.lcgg.card_south2.setCard(new Cards(msg[3]));
										lcg.lcgg.card_south2.flip();
									}
									break;
								case 1 :
									if (!lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL)) {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_east1.getCard().getType()));
										lcg.lcgg.card_east1.setCard(new Cards(msg[3]));
									}
									else {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_east2.getCard().getType()));
										lcg.lcgg.card_east2.setCard(new Cards(msg[3]));
									}
									break;
								case 2 :
									if (!lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL)) {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_north1.getCard().getType()));
										lcg.lcgg.card_north1.setCard(new Cards(msg[3]));
									}
									else {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_north2.getCard().getType()));
										lcg.lcgg.card_north2.setCard(new Cards(msg[3]));
									}
									break;
								case 3 :
									if (!lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL)) {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_west1.getCard().getType()));
										lcg.lcgg.card_west1.setCard(new Cards(msg[3]));
									}
									else {
										lcg.lcgg.card_center_prince.setCard(new Cards(lcg.lcgg.card_west2.getCard().getType()));
										lcg.lcgg.card_west2.setCard(new Cards(msg[3]));
									}
									break;
								}
							}
							else if (msg.length == 6 && msg[1].equals("FLIP")) {
								int objective = new Integer(msg[2]).intValue() - private_num;
								Card card = null;
								Card floor = null;
								if (objective < 0)
									objective += 4;
								
								switch (objective) {
								case 0 :
									if (lcg.lcgg.card_south1.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_south1;
									else if (lcg.lcgg.card_south2.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_south2;
									floor = lcg.lcgg.card_center_south;
									break;
								case 1 :
									if (lcg.lcgg.card_east1.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_east1;
									else if (lcg.lcgg.card_east2.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_east2;
									floor = lcg.lcgg.card_center_east;
									break;
								case 2 :
									if (lcg.lcgg.card_north1.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_north1;
									else if (lcg.lcgg.card_north2.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_north2;
									floor = lcg.lcgg.card_center_north;
									break;
								case 3 :
									if (lcg.lcgg.card_west1.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_west1;
									else if (lcg.lcgg.card_west2.getCard().getType().equals(msg[3].trim()))
										card = lcg.lcgg.card_west2;
									floor = lcg.lcgg.card_center_west;
									break;
								}
								
								
								if (msg[4].equals("TRUE")) {
									card.flip();
									
									if (msg[5].equals("TRUE")) {
										floor.setCard(card.getCard());
										floor.flip();
										card.setCard(new Cards(Cards.NULL));
									}
								}
								else if (msg[4].equals("FALSE")) {
									if (msg[5].equals("TRUE")) {
										floor.setCard(card.getCard());
										floor.flip();
										card.setCard(new Cards(Cards.NULL));
									}
								}
								//lcg.lcgg.sort();
								lcg.lcgg.updateDeck();
							}
							//��ο������� ������ �ʴ´�(�������� �ʴ´�).
							else if (msg.length == 4 && msg[1].equals("DRAW")) {
								int objective = new Integer(msg[2]).intValue() - private_num;
								if (objective < 0)
									objective += 4;
								
								switch (objective) {
								case 1 :
									if (lcg.lcgg.card_east1.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_east1.setCard(new Cards(msg[3]));
									else if (lcg.lcgg.card_east2.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_east2.setCard(new Cards(msg[3]));
									break;
								case 2 :
									if (lcg.lcgg.card_north1.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_north1.setCard(new Cards(msg[3]));
									else if (lcg.lcgg.card_north2.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_north2.setCard(new Cards(msg[3]));
									break;
								case 3 :
									if (lcg.lcgg.card_west1.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_west1.setCard(new Cards(msg[3]));
									else if (lcg.lcgg.card_west2.getCard().getType().equals(Cards.NULL))
										lcg.lcgg.card_west2.setCard(new Cards(msg[3]));
									break;
								}
								lcg.lcgg.sort();
								//lcg.lcgg.updateDeck();
							}
							else if (msg[1].equals("PRINT")) {
								if(msg.length == 4){
									JDialog jd_print = new JDialog();
									JLabel jlb_name = new JLabel("����� ī��");
									jlb_name.setFont(new Font("�޸ո���ü",Font.PLAIN,25));
									jd_print.setAlwaysOnTop(true);
									
									JLabel jlb_print = new JLabel();
									ImageIcon oic = new Cards(msg[2]).getImageIcon();
									Image oi = oic.getImage();
									Image ri = oi.getScaledInstance(300, 400, Image.SCALE_FAST);
									ImageIcon ric = new ImageIcon(ri);
									
									jlb_print.setIcon(ric);
									jd_print.add(jlb_name);
									jd_print.add(jlb_print);
									jd_print.setLayout(null);
									jd_print.setBounds(lcg.getX()+50,lcg.getY()+50,350,520);
									jd_print.setBackground(Color.DARK_GRAY);
									jlb_name.setBounds(20,20,300,50);
									jlb_print.setBounds(20,80,300,400);
									jd_print.setVisible(true);
									jd_print.setResizable(false);
									int objective = new Integer(msg[3]).intValue() - private_num;
									if (objective < 0)
										objective += 4;
									String who="";
									if(objective == 1) who = "EAST";
									if(objective == 2) who = "NORTH";
									if(objective == 3) who = "WEST";
									jd_print.setTitle(who+"���� ī�� : "+msg[2]);
								}
								else if(msg.length == 5){
									if(msg[2].equals("BARON")){
										JDialog jd_print = new JDialog();
										jd_print.setAlwaysOnTop(true);
										JLabel jlb_name = new JLabel("�� ī��(����)�� ����� ī��(������)");
										jlb_name.setFont(new Font("�޸ո���ü",Font.PLAIN,20));
										JLabel jlb_print1 = new JLabel();
										JLabel jlb_print2 = new JLabel();
										jlb_print1.setIcon(new Cards(msg[3]).getImageIcon());
										jlb_print2.setIcon(new Cards(msg[4]).getImageIcon());
										jd_print.add(jlb_name);
										jd_print.add(jlb_print1);
										jd_print.add(jlb_print2);
										jd_print.setLayout(null);
										jd_print.setBounds(lcg.getX()+50,lcg.getY()+50,400,330);
										jlb_name.setBounds(50,15,300,30);
										jlb_print1.setBounds(210,50,150,200);
										jlb_print2.setBounds(30,50,150,200);
										jd_print.setVisible(true);
										jd_print.setResizable(false);
										jd_print.setTitle("���ۿ� ���� �º� ���");
									}
								}
							}
							
							else if (msg.length == 9 && msg[2].equals("INPUT")) {
								if (msg[5].equals("FALSE") && msg[6].equals("FALSE")
										&& msg[7].equals("FALSE") && msg[8].equals("FALSE")) {
									out.writeUTF("GAME#" + msg[1] + "#" + private_num + "#" + Cards.NULL);
									continue;
								}
								
								int objective;
								while (true) {
									objective = JOptionPane.showOptionDialog(null, "����� �����ϼ���", "����", JOptionPane.OK_OPTION,
											JOptionPane.QUESTION_MESSAGE, null, new Object[] {"South", "East", "North", "West"}, null);
									if (objective == 0)
										if (!msg[1].equals(Cards.PRINCE))
											continue;
									if (objective == JOptionPane.CLOSED_OPTION)
										continue;
									
									objective += private_num;
									if (objective > 3)
										objective -= 4;
									
									
									if (msg[5 + objective].equals("TRUE"))
										break;
								}
								
								if (msg[3].equals("TRUE")) {
									int objective_type;
									Image img1 = new Cards(Cards.PRIEST).getImageIcon().getImage();
									ImageIcon iic1 = new ImageIcon(img1.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img2 = new Cards(Cards.BARON).getImageIcon().getImage();
									ImageIcon iic2 = new ImageIcon(img2.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img3 = new Cards(Cards.HANDMAID).getImageIcon().getImage();
									ImageIcon iic3 = new ImageIcon(img3.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img4 = new Cards(Cards.PRINCE).getImageIcon().getImage();
									ImageIcon iic4 = new ImageIcon(img4.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img5 = new Cards(Cards.KING).getImageIcon().getImage();
									ImageIcon iic5 = new ImageIcon(img5.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img6 = new Cards(Cards.COUNTESS).getImageIcon().getImage();
									ImageIcon iic6 = new ImageIcon(img6.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									Image img7 = new Cards(Cards.PRINCESS).getImageIcon().getImage();
									ImageIcon iic7 = new ImageIcon(img7.getScaledInstance(60, 80, Image.SCALE_SMOOTH));
									
									while (true) {
										
										objective_type = JOptionPane.showOptionDialog(null, "�����ϼ���", "Option", JOptionPane.OK_OPTION,
												JOptionPane.QUESTION_MESSAGE, null, new ImageIcon[] {iic1, iic2, iic3, iic4,
												iic5, iic6, iic7}, null);
										if (objective != JOptionPane.CLOSED_OPTION) break;
									}
									
									switch (objective_type + 1) {
									case 1 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.PRIEST);
										break;
									case 2 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.BARON);
										break;
									case 3 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.HANDMAID);
										break;
									case 4 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.PRINCE);
										break;
									case 5 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.KING);
										break;
									case 6 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.COUNTESS);
										break;
									case 7 :
										out.writeUTF("GAME#" + msg[1] + "#" + objective + "#" + Cards.PRINCESS);
										break;
									}
								}
								else {
									out.writeUTF("GAME#" + msg[1] + "#" + objective);
								}
							}
							else if (msg.length == 2){
								if (lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL)) {
									lcg.lcgg.card_south1.setCard(new Cards(msg[1]));
									lcg.lcgg.card_south1.flip();
								}
								else if (lcg.lcgg.card_south2.getCard().getType().equals(Cards.NULL)) {
									lcg.lcgg.card_south2.setCard(new Cards(msg[1]));
									lcg.lcgg.card_south2.flip();
								}
								lcg.lcgg.sort();
								//lcg.lcgg.updateDeck();
							}
						}
						else{
							System.out.println("�����κ��� �� �������� �޼����� �����Ͽ����ϴ�.");
							System.out.println("�޼��� ���� : "+message);
						}
					}
		
				}
				catch (ConnectException ce){
					lcg.jtf_entry.setText("�ش� Host�� Server�� �������� �ʽ��ϴ�.");
				}catch (SocketException se){
					lcg.jtf_entry.setText("������ ����Ǿ����ϴ�. Ŭ���̾�Ʈ�� �ʱ�ȭ �մϴ�.");
					lcg.jtf_entry.setEnabled(true);
					lcg.jbt_entry.setEnabled(true);
					lcg.lcgg.set_JP_Ready();
					lcg.jtf_chat_chat.setText("");
				}
				catch (UnknownHostException e) {
					lcg.jtf_entry.setText("�ùٸ� Host IP �ּҸ� �Է����ּ���.");
				} catch (IOException e) {e.printStackTrace();}
				finally{
					lcg.jbt_entry.setEnabled(true);
				}
				
			}
			
		};
		c.start();
	}

	private boolean isIPAddress(String str){
		final String IPADDRESS_PATTERN = 
				  "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				  "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern p = Pattern.compile(IPADDRESS_PATTERN);
		Matcher m = p.matcher(str);
		return m.matches();
		
	}
	
	
	
	
	public void addActionListenerToCard() {
		lcg.lcgg.card_south1.addActionListener(this);
		lcg.lcgg.card_south2.addActionListener(this);
	}
	
	
	
	
	
	
	private boolean isready=false;
	@Override
	public void actionPerformed(ActionEvent ae) {
		Object o = ae.getSource();
		if(o.equals(lcg.jbt_entry)){
			String ip = lcg.jtf_entry.getText().trim();
			System.out.println(ip);
			if(isIPAddress(ip)){ try{c.stop(); clientStart(ip);}catch(Exception e){clientStart(ip);}}
			else lcg.jtf_entry.setText("�ùٸ� Host IP �ּҸ� �Է����ּ���.");
		}
		if(o.equals(lcg.lcgg.jbt_ready)){
			try {
				if(!isready){ 
					out.writeUTF("SYSTEM#READY");
					lcg.lcgg.jbt_ready.setBackground(Color.GREEN);
					lcg.lcgg.jbt_ready.setText("<html><Font size = 20>UNREADY?</Font><html>");
					isready=true;
				}
				else{
					out.writeUTF("SYSTEM#UNREADY");
					lcg.lcgg.jbt_ready.setBackground(Color.RED);
					lcg.lcgg.jbt_ready.setText("<html><Font size = 20>READY?</Font><html>");
					isready=false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/* ���� ��ư ���� �ÿ� ������ �޼��� �ۼ��� (out.writeUTF(String msg))
		 * 
		 * �޼��� ���� 
		 * 
		 * 1. SYSTEM
		 * 
		 * 2. GAME
		 *
		 * 
		 */
		if (o == lcg.lcgg.card_south1) {
			if(lcg.lcgg.card_south1.getCard().getType().equals(Cards.PRINCE) || lcg.lcgg.card_south1.getCard().getType().equals(Cards.KING))
				if(lcg.lcgg.card_south2.getCard().getType().equals(Cards.COUNTESS))
					return;
				
			if (waiting) return;
			if (!turn || waiting || lcg.lcgg.card_south1.getCard().getType().equals(Cards.NULL))
				return;
			try {
				out.writeUTF("GAME#" + lcg.lcgg.card_south1.getCard().getType());
				waiting = true;
			}
			catch (IOException ex) {}
		}
		else if (o == lcg.lcgg.card_south2) {
			if(lcg.lcgg.card_south2.getCard().getType().equals(Cards.PRINCE) || lcg.lcgg.card_south2.getCard().getType().equals(Cards.KING))
				if(lcg.lcgg.card_south1.getCard().getType().equals(Cards.COUNTESS))
					return;
			if (waiting) return;
			if (!turn || waiting || lcg.lcgg.card_south2.getCard().getType().equals(Cards.NULL))
				return;
			try {
				out.writeUTF("GAME#" + lcg.lcgg.card_south2.getCard().getType());
				waiting = true;
			}
			catch (IOException ex) {}
		}
		else if (o == lcg.lcgg.card_center_south) {
			
		}
		else if (o == lcg.lcgg.card_center_east) {
			
		}
		else if (o == lcg.lcgg.card_center_north) {
			
		}
		else if (o == lcg.lcgg.card_center_west) {
			
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void keyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub
		if(ke.getKeyCode()==KeyEvent.VK_ENTER){
			if(out!=null){
				try {
					String s = lcg.jtf_chat_nick.getText() + " >> "
							   + lcg.jtf_chat_chat.getText()+"\n";
					out.writeUTF("CHAT#"+s);
					lcg.jtf_chat_chat.setText("");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			out.writeUTF("SYSTEM#EXIT");
			System.exit(0);
		} catch (Exception e) {System.exit(0);}
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			
			new LoveLetter_Client();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
