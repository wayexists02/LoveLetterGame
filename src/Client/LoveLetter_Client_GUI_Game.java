package Client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.*;

import Data.Cards;

public class LoveLetter_Client_GUI_Game extends JPanel{

	@Override
	public void paintComponent(Graphics g){
		Image imgae = new ImageIcon("image/GameBackground.jpg").getImage();
		g.drawImage(imgae,0,0,this);
		g.drawRect(-1, 81, 455, 285);
		g.drawRect(81,81,285,285);
	}
	
	public LoveLetter_Client_GUI_Game() {
		setBounds(335,70,450,450);
		set_JP_Ready();
		jbt_ready.setEnabled(false);
	}
	
	JButton jbt_ready = new JButton("READY?");
	public void set_JP_Ready(){
		this.removeAll();
		jbt_ready.setBackground(Color.RED);
		jbt_ready.setEnabled(false);
		jbt_ready.setVisible(true);
		jbt_ready.setText("<html><Font size = 20>READY?</Font><html>");
		jbt_ready.setBounds(-5,-5,460,460);
		setLayout(null);
		add(jbt_ready);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	Card card_north1;
	Card card_north2;
	Card card_west1;
	Card card_west2;
	Card card_center_north;
	Card card_center_west;
	Card card_center_east;
	Card card_center_south;
	JPanel jp_center_deck;
	//Card card_center_discard;//처음 버려진 패
	Card card_center_prince;//왕자에 의해 버려진 패
	Card card_east1;
	Card card_east2;
	Card card_south1;
	Card card_south2;
	
	//JLabel jlb_center_discard;
	JLabel jlb_center_prince;
	
	Cubes cubes_north;
	Cubes cubes_west;
	Cubes cubes_east;
	Cubes cubes_south;
	
	HorizontalPanel jp_south;
	HorizontalPanel jp_north;
	VerticalPanel jp_east;
	VerticalPanel jp_west;
	
	public void set_JP_Game(){
		jbt_ready.setVisible(false);
		removeAll();
		setLayout(null);
		
		cubes_south = new Cubes("<html><b>You</b><html>");
		cubes_north = new Cubes("North");
		cubes_east = new Cubes("East");
		cubes_west = new Cubes("West");
		
		card_south1 = new Card(false);
		card_south2 = new Card(false);
		card_east1 = new Card(false);
		card_east2 = new Card(false);
		card_north1 = new Card(false);
		card_north2 = new Card(false);
		card_west1 = new Card(false);
		card_west2 = new Card(false);
		
		card_center_south = new Card(true); card_center_south.setLocation(115, 200);
		card_center_east = new Card(true); card_center_east.setLocation(220, 100);
		card_center_north = new Card(true); card_center_north.setLocation(115, 5);
		card_center_west = new Card(true); card_center_west.setLocation(30, 100);
		
		jp_center_deck = new JPanel();
		jp_center_deck.setBackground(Color.WHITE);
		jp_center_deck.setBounds(105,87,71,91);
		jp_center_deck.setOpaque(false);
		
		
		jlb_center_prince = new JLabel("왕자에 의해 버려진 패");
		jlb_center_prince.setBounds(155,167,130,50);
		card_center_prince = new Card(true);
		card_center_prince.setLocation(220, 200);
		
		jp_south = new HorizontalPanel(0, 370, card_south1, card_south2, cubes_south);
		jp_north = new HorizontalPanel(0, 0, card_north1, card_north2, cubes_north);
		jp_east = new VerticalPanel(370, 80, card_east1, card_east2, cubes_east);
		jp_west = new VerticalPanel(0, 80, card_west1, card_west2, cubes_west);
		JPanel jp_center = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				/*
				ImageIcon oic = new Cards(Cards.NULL).getImageIcon();
				Image oi = oic.getImage();
				Image ri = oi.getScaledInstance(60, 80, Image.SCALE_SMOOTH);
				
				g.clearRect(0, 0, getWidth(), getHeight());
				setOpaque(false);
				for (int i = 0; i < count; i++) {
					g.drawImage(ri, 16 - i, 16 - i, this);
				}
				*/
				updateDeck();
			}
		};
		
		jp_center.setBounds(80, 80, 290, 290);
		jp_center.setBackground(Color.WHITE);
		jp_center.setOpaque(false);
		jp_center.setLayout(null);
		
		jp_center.add(jp_center_deck);
		jp_center.add(card_center_south);
		jp_center.add(card_center_north);
		jp_center.add(card_center_west);
		jp_center.add(card_center_east);
		jp_center.add(jlb_center_prince);
		jp_center.add(card_center_prince);
		
		add(jp_center);
		add(jp_south);
		add(jp_east);
		add(jp_west);
		add(jp_north);
		
		/*
		jbt_ready.setVisible(false);
		this.removeAll();
		setLayout(null);
		//JPanel jp_north = new JPanel();
		//JPanel jp_west = new JPanel();
		JPanel jp_center = new JPanel();
		//JPanel jp_east = new JPanel();
		//JPanel jp_south = new JPanel();

		
		//jp_north.setOpaque(false);
		//jp_west.setOpaque(false);
		jp_center.setOpaque(false);
		//jp_east.setOpaque(false);
		//jp_south.setOpaque(false);
		
		jp_center_deck = new JPanel();
		jp_center_deck.setOpaque(false);
		jp_center_deck.setLayout(null);
		
		//jp_north.setLayout(null);
		//jp_west.setLayout(null);
		jp_center.setLayout(null);
		//jp_east.setLayout(null);
		//jp_south.setLayout(null);
		


		//jp_north.setBounds(0,0,450,80);
		//jp_west.setBounds(0,80,80,290);
		jp_center.setBounds(80,80,290,290);
		//jp_east.setBounds(370,80,80,290);
		//jp_south.setBounds(0,370,450,80);
		
		
		
		
		
		
		//north
		card_north1 = new Card(160,0,false);
		card_north2 = new Card(230,0,false);
		//card_north2.setVisible(false);
		cubes_north = new Cubes("North");
		
		
		//jp_north.setBackground(Color.BLUE);
		
		
		//west
		card_west1 = new Card(10,60,false);
		card_west2 = new Card(10,150,false);
		//card_west2.setVisible(false);
		cubes_west = new Cubes("West");
		
		
		//jp_west.setBackground(Color.red);
		
		//center
		//jlb_center_discard = new JLabel("처음 버려진 패");
		jlb_center_prince = new JLabel("왕자에 의해 버려진 패");
		//jlb_center_discard.setBounds(5,82,90,20);
		jlb_center_prince.setBounds(155,167,130,50);
		
		card_center_north = new Card(115,5,true);
		card_center_west = new Card(30,100,true);
		card_center_east = new Card(220,100,true);
		card_center_south = new Card(115,200,true);
		
		
		//card_center_discard = new Card(30,5,true);
		card_center_prince = new Card(220,200,true);
		
		//jp_center.add(jlb_center_discard);
		jp_center.add(jlb_center_prince);
		jp_center.add(card_center_north);
		jp_center.add(card_center_west);
		jp_center.add(card_center_east);
		jp_center.add(card_center_south);
		jp_center.add(jp_center_deck);
		//jp_center.add(card_center_discard);
		jp_center.add(card_center_prince);
		
		jp_center_deck.setBackground(Color.WHITE);
		jp_center_deck.setBounds(105,87,71,91);
		jp_center.setBackground(Color.WHITE);
		
		//east
		card_east1 = new Card(10,60,false);
		card_east2 = new Card(10,150,false);
		//card_east2.setVisible(false);
		cubes_east = new Cubes("East");
		
		
		//jp_east.setBackground(Color.cyan);
		
		//south
		card_south1 = new Card(160,0,false);
		card_south2 = new Card(230,0,false);
		//card_south2.setVisible(false);
		cubes_south = new Cubes("You");
		
		
		//jp_south.setBackground(Color.green);
		

		VerticalPanel jp_west = new VerticalPanel(0, 80, 80, 290, card_west1, card_west2, cubes_west);
		VerticalPanel jp_east = new VerticalPanel(370, 80, 80, 290, card_east1, card_east2, cubes_east);
		HorizontalPanel jp_south = new HorizontalPanel(0, 370, 450, 80, card_south1, card_south2, cubes_south);
		HorizontalPanel jp_north = new HorizontalPanel(0, 0, 450, 80, card_north1, card_north2, cubes_north);
		
		jp_north.add(card_north1);
		jp_north.add(card_north2);
		jp_north.add(cubes_north);
		
		jp_west.add(card_west1);
		jp_west.add(card_west2);
		jp_west.add(cubes_west);
		
		jp_east.add(card_east1);
		jp_east.add(card_east2);
		jp_east.add(cubes_east);
		
		jp_south.add(card_south1);
		jp_south.add(card_south2);
		jp_south.add(cubes_south);
		
		add(jp_north);
		add(jp_west);
		add(jp_center);
		add(jp_east);
		add(jp_south);
		*/
	}
	
	public void sort() {
		jp_north.sort();
		jp_south.sort();
		jp_east.sort();
		jp_west.sort();
		jp_center_deck.repaint();
		//updateDeck();
	}
	private int count;
	
	public void setDeckCount(int count) {
		this.count = count;
		jp_center_deck.repaint();
	}
	public void updateDeck() {
		jp_center_deck.removeAll();
		
		ImageIcon oic = new Cards(Cards.NULL).getImageIcon();
		Image oi = oic.getImage();
		Image ri = oi.getScaledInstance(60, 80, Image.SCALE_SMOOTH);
		ImageIcon ric = new ImageIcon(ri);
		
		for (int i = 0; i < count; i++) {
			JLabel jlb = new JLabel();
			jlb.setIcon(ric);
			jlb.setBounds(i + (15 - count), i + (15 - count), 60, 80);
			jp_center_deck.add(jlb);
		}
		jp_center_deck.setToolTipText(String.valueOf(count));
		jp_center_deck.repaint();
	}
	class VerticalPanel extends JPanel {
		
		private Card card1, card2;
		private Cubes cubes;
		
		public VerticalPanel(int x, int y, Card c1, Card c2, Cubes c) {
			setBounds(x, y, 80, 290);
			setBackground(Color.WHITE);
			setLayout(null);
			setOpaque(false);
			card1 = c1; card2 = c2;
			cubes = c;
			add(cubes);
		}
		
		public void sort() {
			removeAll();
			if (!card1.getCard().getType().equals(Cards.NULL) && !card2.getCard().getType().equals(Cards.NULL)) {
				card1.setLocation(10, 60);
				card2.setLocation(10, 150);
				add(card1); add(card2);
			}
			else if (card1.getCard().getType().equals(Cards.NULL) && !card2.getCard().getType().equals(Cards.NULL)) {
				card2.setLocation(10, 105);
				add(card2);
			}
			else if (!card1.getCard().getType().equals(Cards.NULL) && card2.getCard().getType().equals(Cards.NULL)) {
				card1.setLocation(10, 105);
				add(card1);
			}
			add(cubes);
			repaint();
		}
	}
	class HorizontalPanel extends JPanel {
		
		private Card card1, card2;
		private Cubes cubes;
		
		public HorizontalPanel(int x, int y, Card c1, Card c2, Cubes c) {
			setBounds(x+10, y, 370, 80);
			setBackground(Color.WHITE);
			setLayout(null);
			setOpaque(false);
			card1 = c1; card2 = c2;
			cubes = c;
		}
		
		public void sort() {
			removeAll();
			if (!card1.getCard().getType().equals(Cards.NULL) && !card2.getCard().getType().equals(Cards.NULL)) {
				card1.setLocation(140, 0);
				card2.setLocation(220, 0);
				add(card1); add(card2);
			}
			else if (card1.getCard().getType().equals(Cards.NULL) && !card2.getCard().getType().equals(Cards.NULL)) {
				card2.setLocation(180, 0);
				add(card2);
			}
			else if (!card1.getCard().getType().equals(Cards.NULL) && card2.getCard().getType().equals(Cards.NULL)) {
				card1.setLocation(180, 0);
				add(card1);
			}
			add(cubes);
			repaint();
		}
	}
}

