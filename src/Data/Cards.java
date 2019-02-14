package Data;

import javax.swing.ImageIcon;

public class Cards implements Cloneable {
	public static String GUARD = "Guard";
	public static String PRIEST = "Priest";
	public static String BARON = "Baron";
	public static String HANDMAID = "Handmaid";
	public static String PRINCE = "Prince";
	public static String KING = "King";
	public static String COUNTESS = "Countess";
	public static String PRINCESS = "Princess";
	public static String NULL = "null";
	
	private String type = null;
	private int power;
	
	public Cards(String type) {
		this.type = type;
		
		if (type.equals(GUARD))
			power = 1;
		else if (type.equals(PRIEST))
			power = 2;
		else if (type.equals(BARON))
			power = 3;
		else if (type.equals(HANDMAID))
			power = 4;
		else if (type.equals(PRINCE))
			power = 5;
		else if (type.equals(KING))
			power = 6;
		else if (type.equals(COUNTESS))
			power = 7;
		else if (type.equals(PRINCESS))
			power = 8;
	}
	
	public String getType() {
		return type;
	}
	public int getPower() {
		return power;
	}
	/**
	 * 카드 파워 비교
	 * @param c 비교할 카드
	 * @return if 대상카드타입 == 해당카드타입 : 0, else if 대상카드타입 > 해당카드타입 : (-)
	 */
	public int compareType(Cards c) {
		return power - c.getPower();
	}
	public ImageIcon getImageIcon() {
		if (type.equals(NULL)){
			return new ImageIcon("Image\\LoveLetter0.png");
		}
		else if (type.equals(GUARD)) {
			return new ImageIcon("Image\\LoveLetter1.png");
		}
		else if (type.equals(PRIEST)) {
			return new ImageIcon("Image\\LoveLetter2.png");
		}
		else if (type.equals(BARON)) {
			return new ImageIcon("Image\\LoveLetter3.png");
		}
		else if (type.equals(HANDMAID)) {
			return new ImageIcon("Image\\LoveLetter4.png");
		}
		else if (type.equals(PRINCE)) {
			return new ImageIcon("Image\\LoveLetter5.png");
		}
		else if (type.equals(KING)) {
			return new ImageIcon("Image\\LoveLetter6.png");
		}
		else if (type.equals(COUNTESS)) {
			return new ImageIcon("Image\\LoveLetter7.png");
		}
		else if (type.equals(PRINCESS)) {
			return new ImageIcon("Image\\LoveLetter8.png");
		}
		else return null;
	}
}
