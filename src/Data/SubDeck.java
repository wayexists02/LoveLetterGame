package Data;

import java.util.ArrayList;

public class SubDeck {
	private final ArrayList<Cards> deck;
	
	public SubDeck() {
		deck = new ArrayList<Cards>();
	}
	
	public Cards getCard() { return deck.get(deck.size() - 1); }
	public void putCard(Cards c) { deck.add(c); }
}
