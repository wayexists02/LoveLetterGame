package Client;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class LoveLetter_Client_GUI extends JFrame{

	public LoveLetter_Client_GUI() {
		
		super("Love Letter Client");
		
		JPanel jp_main = new JPanel();
		jp_main.setLayout(null);
		//jp_main.add(get_JP_Player());
		jp_main.add(get_JP_Entry());
		jp_main.add(get_JP_Chat());
		jp_main.add(get_JP_Game());
		jp_main.add(get_JP_Credit());
		
		add(jp_main);
		setResizable(false);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(300,100,800,600);
		setVisible(true);
		
	}
	
	
	/*
	 * 
	 * 이하는 게임완성 후에 처리.
	//플레이어가 안들어온 자리에는 어두운 상태이다.(이미지)
	//플레이어가 들어오면 그 자리에 불이 켜진다.(이미지)
	//플레이어가 4명일 때 레디하지 않으면 빨간불이 켜진다.(이미지)
	//레디하면 초록불이 켜진다.(이미지)
	 * 
	 */
	JButton[] jbts_player;
	/*private JPanel get_JP_Player(){
		
		JPanel jp_player = new JPanel();
		
		jbts_player = new JButton[4];
		
		jp_player.setBackground(Color.DARK_GRAY);
		jp_player.setBounds(10,10,220,60);
		
		return jp_player;
	}*/
	
	
	
	JTextField jtf_entry;
	JButton jbt_entry;
	private JPanel get_JP_Entry(){
		
		JPanel jp_entry = new JPanel();
		jtf_entry = new JTextField("Host IP를 입력해주세요.");
		
		// 포커스 얻으면 문자열 제거
		jtf_entry.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				jtf_entry.setText("127.0.0.1");
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
		jbt_entry = new JButton("Entry!");
		
		jp_entry.setLayout(null);
		jp_entry.setBounds(335,10,450,50);
		
		jp_entry.add(jtf_entry);
		jp_entry.add(jbt_entry);
		
		jtf_entry.setBounds(0,0,320,50);
		jbt_entry.setBounds(330,0,120,50);
		
		return jp_entry;
		
	}
	
	JTextArea jta_chat;
	JTextField jtf_chat_nick;
	JTextField jtf_chat_chat;
	JScrollPane jsp_chat;
	
	private JPanel get_JP_Credit(){
		JPanel jp_credit = new JPanel();
		JLabel jlb_credit = new JLabel("<html><h2><font face = \"휴먼매직체\">Love Letter Online.&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp Copyright 2014. 안윤근, 이재영 all rights reserved.</font></h2></html>");
		jp_credit.add(jlb_credit);
		jp_credit.setBounds(10,520,780,50);
		return jp_credit;
	}
	
	private JPanel get_JP_Chat(){
		
		JPanel jp_chat = new JPanel();
		
		jtf_chat_nick = new JTextField("닉네임");
		//포커스 얻으면 문자열 제거
		jtf_chat_nick.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {
				if (jtf_chat_nick.getText().equals("닉네임"))
					jtf_chat_nick.setText("");
			}
			@Override
			public void focusLost(FocusEvent arg0) {
				if (jtf_chat_nick.getText().equals(""))
					jtf_chat_nick.setText("닉네임");
			}
			
		});
		jtf_chat_chat = new JTextField();
		
		jp_chat.setLayout(null);
		jp_chat.setBounds(10,10,300,512);
		//jp_chat.setBackground(Color.blue);
		
		jtf_chat_nick.setBounds(0,462,70,50);
		jtf_chat_chat.setBounds(70,462,230,50);
		
		jta_chat = new JTextArea();
		jta_chat.setLineWrap(true);
		jta_chat.setEditable(false);
		jsp_chat = new JScrollPane(jta_chat);
		
		jsp_chat.setBounds(0,0,300,464);
		
		jp_chat.add(jsp_chat);
		jp_chat.add(jtf_chat_nick);
		jp_chat.add(jtf_chat_chat);
		
		return jp_chat;
	}
	
	LoveLetter_Client_GUI_Game lcgg;
	private JPanel get_JP_Game(){
		lcgg = new LoveLetter_Client_GUI_Game();
		return lcgg;
	}
	

}
