package Client;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Cubes extends JPanel{

	int num = 0;
	JLabel jlb_name;
	JButton[] jbts_cubes;
	public Cubes(String name) {
		setLayout(null);
		
		jlb_name = new JLabel(name);
		jlb_name.setBounds(0,0,100,30);
		
		add(jlb_name);
		setBounds(0,0,100,70);
		setOpaque(false);
		jbts_cubes = new JButton[4];
		
		
		for(int i=0;i<4;i++){
			jbts_cubes[i] = new JButton("бс");
			jbts_cubes[i].setVisible(false);
			jbts_cubes[i].setBounds(i*12,32,15,15);
			add(jbts_cubes[i]);
		}
		
	}
	public void setCubes(int n){
		num=n;
		for(int i=0;i<n;i++){
			/*
			ImageIcon oic = new ImageIcon("./image/cube"+(i+1)+".png");
			Image oi = oic.getImage();
			Image ri = oi.getScaledInstance(15, 15, Image.SCALE_REPLICATE);
			ImageIcon ric = new ImageIcon(ri);
			jbts_cubes[i].setIcon(ric);
			*/
			jbts_cubes[i].setBackground(Color.RED);
			jbts_cubes[i].setVisible(true);
		}
		repaint();
	}

}
