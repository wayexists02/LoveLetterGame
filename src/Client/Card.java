package Client;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.Border;

import Data.Cards;

public class Card extends JButton implements ActionListener, MouseListener {

	String name;
	int number;
	boolean isDeck;
	
	private Cards card = new Cards(Cards.NULL);
	private ArrayList<Cards> deck = new ArrayList<Cards>();
	private Border border = BorderFactory.createEtchedBorder();
	
	public Card(boolean isDeck) {
		super("");
		this.isDeck = isDeck;


		//setBackground(Color.WHITE);
		//setOpaque(false);
		//setBorderPainted(false);
		setVisible(false);
		setSize(60, 80);
		
		//setBounds(x,y,60,80);
		if (isDeck) addActionListener(this);
		addMouseListener(this);
	}
	
	public void flip(){
		if (card == null) return;
		setBackground(null);
		ImageIcon oic = card.getImageIcon();
		Image oi = oic.getImage();
		Image ri = oi.getScaledInstance(60, 80, Image.SCALE_SMOOTH);
		ImageIcon ric = new ImageIcon(ri);
		setIcon(ric);
		//setText("¾Õ¸é");
		repaint();
	}
	
	public void setCard(Cards c) {
		card = c;
		setIcon(null);
		if (!c.getType().equals(Cards.NULL)){
			setVisible(true);
			ImageIcon oic = new Cards(Cards.NULL).getImageIcon();
			Image oi = oic.getImage();
			Image ri = oi.getScaledInstance(60, 80, Image.SCALE_SMOOTH);
			ImageIcon ric = new ImageIcon(ri);
			setIcon(ric);
			if (isDeck) {
				deck.add(c);
			}
		}
		else {
			if (isDeck)
				deck = new ArrayList<Cards>();
			setVisible(false);
			//setBackground(Color.WHITE);
			//setOpaque(false);
			//setBorderPainted(false);
		}
		repaint();
	}
	public Cards getCard() {
		return card;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int[] height = new int[] {120, 220, 320, 420};
		int[] width = new int[] {340, 85, 170, 255};
		
		JDialog jdg = new JDialog();
		jdg.setLayout(new FlowLayout());
		if (deck.size() <= 4) {
			jdg.setBounds(120, 120, width[deck.size() % 4], height[0]);
		}
		else if (deck.size() <= 8) {
			jdg.setBounds(120, 120, width[0], height[1]);
		}
		else if (deck.size() <= 12) {
			jdg.setBounds(120, 120, width[0], height[2]);
		}
		else {
			jdg.setBounds(120, 120, width[0], height[3]);
		}
		JLabel[] arJlb = new JLabel[deck.size()];
		for (int i = 0; i < arJlb.length; i++) {
			arJlb[i] = new JLabel();
			arJlb[i].setIcon(new ImageIcon(deck.get(i).getImageIcon().getImage().getScaledInstance(60, 80, Image.SCALE_SMOOTH)));
			arJlb[i].setSize(60, 80);
			jdg.add(arJlb[i]);
		}
		jdg.setResizable(false);
		jdg.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
		setBorder(border);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		setBorder(null);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
