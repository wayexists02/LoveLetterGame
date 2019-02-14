package Server;
import javax.swing.*;
public class LoveLetter_Server_GUI extends JFrame{

	JButton jbt_server;
	JButton jbt_start;
	JButton jbt_end;
	JLabel jlb_ip;
	public LoveLetter_Server_GUI() {
		
		super("Love Letter Server");
		JPanel jp=new JPanel();
		JLabel jlb_caution = new JLabel(/*"<html>* 정말로 <u>신뢰할 수 있는 사람</u>에게만 공개하십시오.<br>* 주의 : 공유기 사용시 서버는 <u>포트포워딩</u>이 <br>되어있어야 합니다. (TCP 3611포트)</html>"*/);
		jbt_server = new JButton("서버 시작");
		jbt_start = new JButton("게임 시작");
		jbt_end = new JButton("서버 종료");
		jlb_ip = new JLabel("잠시만 기다려주세요...");
		jp.setLayout(null);
		
		jlb_ip.setBounds(50,30,300,50);
		jlb_caution.setBounds(50,70,300,50); 
		jbt_server.setBounds(50,125,90,50);
		jbt_start.setBounds(150,125,90,50);
		jbt_end.setBounds(250,125,90,50);
		
		jp.add(jlb_ip);
		jp.add(jlb_caution);
		jp.add(jbt_server);
		jp.add(jbt_start);
		jp.add(jbt_end);
		
		add(jp);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(20,20,400,250);
		setResizable(false);
		setVisible(true);
	}

}
