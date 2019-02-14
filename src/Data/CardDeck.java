package Data;

import java.util.ArrayList;

import javax.swing.JButton;

public class CardDeck extends JButton {
	private ArrayList<Cards> deck = null;
	
	
	public CardDeck() {
		deck = new ArrayList<Cards>();
		
		for (int i = 0; i < 5; i++)
			deck.add(new Cards(Cards.GUARD));
		deck.add(new Cards(Cards.PRIEST));
		deck.add(new Cards(Cards.PRIEST));
		deck.add(new Cards(Cards.BARON));
		deck.add(new Cards(Cards.BARON));
		deck.add(new Cards(Cards.HANDMAID));
		deck.add(new Cards(Cards.HANDMAID));
		deck.add(new Cards(Cards.PRINCE));
		deck.add(new Cards(Cards.PRINCE));
		deck.add(new Cards(Cards.KING));
		deck.add(new Cards(Cards.COUNTESS));
		deck.add(new Cards(Cards.PRINCESS));
	}
	//카드 랜덤으로 한장 드로우
	public Cards drawCard() {
		int idx = (int)(Math.random() * deck.size());
		Cards card = deck.get(idx);
		deck.remove(card);
		return card;
	}
	public int getCount() { return deck.size(); }
}
