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
		JLabel jlb_caution = new JLabel(/*"<html>* ������ <u>�ŷ��� �� �ִ� ���</u>���Ը� �����Ͻʽÿ�.<br>* ���� : ������ ���� ������ <u>��Ʈ������</u>�� <br>�Ǿ��־�� �մϴ�. (TCP 3611��Ʈ)</html>"*/);
		jbt_server = new JButton("���� ����");
		jbt_start = new JButton("���� ����");
		jbt_end = new JButton("���� ����");
		jlb_ip = new JLabel("��ø� ��ٷ��ּ���...");
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
