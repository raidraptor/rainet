import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * Stores the data on all Link Cards and Virus Cards.
 * 
 * @author Marvin Li and James Bai
 * @version January 4th, 2015
 */
public class CardPiece extends Card implements Movable {
	// public final static Image LINK_IMAGE1 = new ImageIcon(
	// "images\\RN Link B1.jpg").getImage();
	// public final static Image Virus_IMAGE1 = new ImageIcon(
	// "images\\RN Virus B1.jpg").getImage();
	// public final static Image LINK_IMAGE2 = new ImageIcon(
	// "images\\RN Link B2.jpg").getImage();
	// public final static Image Virus_IMAGE2 = new ImageIcon(
	// "images\\RN Virus B2.jpg").getImage();
	public final static Image BACK_IMAGE1 = new ImageIcon(
			"images\\RN Back 1.jpg").getImage();
	public final static Image BACK_IMAGE2 = new ImageIcon(
			"images\\RN Back 2.jpg").getImage();

	Tile fromTile;

	boolean isVirus;
	private Image image;
	// boolean lineBoost; // Deal with this later.
	boolean isFaceUp;
	private int virusCounter;
	private int linkCounter; 

	/**
	 * Creates a new cardPiece object.
	 * 
	 * @param position
	 *            the position of the cardPiece object.
	 * @param isVirus
	 *            true if this cardPiece is a virus, false if it is a link.
	 * @param owner
	 *            the player who is in possession of the cardPiece.
	 */
	public CardPiece(Point position, boolean isVirus, int owner) {
		super(owner, position);
		this.isVirus = isVirus;
		isFaceUp = true;
		String fileName = "images//RN ";
		if (isVirus)
			fileName += "Virus B";
		else
			fileName += "Link B";
		if (owner == 1)
			fileName += "1.jpg";
		else
			fileName += "2.jpg";
		image = new ImageIcon(fileName).getImage();
	}

	/**
	 * Checks if a certain move is valid.
	 * 
	 * @param otherTile
	 *            the destination tile.
	 * @return true if the move is valid, false otherwise.
	 */
	public boolean canMoveTo(Tile otherTile) {
		if (otherTile.hasPiece() && otherTile.getPiece().owner == this.owner)
			return false;
		if ((Math.abs(fromTile.row - otherTile.row) == 1 && fromTile.col == otherTile.col)
				|| (Math.abs(fromTile.col - otherTile.col) == 1 && fromTile.row == otherTile.row))
			return true;
		return false;
	}
	public boolean aiCanMoveTo(Tile otherTile) {
	
		if (otherTile.hasPiece() && otherTile.getPiece().owner == this.owner)
			return false;
		if ((Math.abs(currentTile.row - otherTile.row) == 1 && currentTile.col == otherTile.col)
				|| (Math.abs(currentTile.col - otherTile.col) == 1 && currentTile.row == otherTile.row))
			return true;
		return false;
	}
	// Add comments~
	public boolean canLeaveTo(Tile otherTile) {
		if(fromTile!=null)
		{
				if (Board.playerTurn == 1) {
					if (!otherTile.hasPiece() && (fromTile.row == 7)
							&& (fromTile.col == 3 || fromTile.col == 4))
						return true;
				} else {
					if (!otherTile.hasPiece() && (fromTile.row == 0)
							&& (fromTile.col == 3 || fromTile.col == 4))
						return true;
				}
				
				
		}else 
		System.out.print(otherTile.hasPiece());
		if (!otherTile.hasPiece() && (currentTile.row == 7)
						&& (currentTile.col == 3 || currentTile.col == 4))
					return true;
				
				return false;
			}


	// Captured moved to Tile class along with 'leave'

	/**
	 * Returns the piece to the tile it came from.
	 * 
	 * @param fromTile
	 *            The tile the piece came from.
	 */
	public void back() {

		fromTile.addPiece(this);
		;
	}

	/**
	 * Draws the cardPiece object.
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {

		if (isFaceUp) {
			g.drawImage(image, position.x, position.y, null);
		} else {
			if (owner == 1)
				g.drawImage(BACK_IMAGE1, position.x, position.y, null);
			else
				g.drawImage(BACK_IMAGE2, position.x, position.y, null);
		}
		// TODO Auto-generated method stub

	}

	// Comments~
	public void flip() {
		if (isFaceUp)
			isFaceUp = false;
		else
			isFaceUp = true;
		return;
	}

	/**
	 * Adds the cardPiece to the tile
	 * 
	 * @param otherTile
	 *            the destination tile.
	 */
	public void moveTo(Tile otherTile) {
		// Dont forget to animate.
		// Also dont forget to capture pieces.
		// Also, pieces with line boost may not jump.
		if (otherTile.hasPiece() && otherTile.getPiece().owner != this.owner)
			otherTile.captured();
		if(fromTile.isSafe()&&!otherTile.isSafe())
		{System.out.println("virus");
			virusCounter++;
		}
		if(!fromTile.isSafe()&&otherTile.isSafe())
		{
			System.out.println("link");
			linkCounter++;
		}
		otherTile.addPiece(this);
		
	}
public double linkFactor()
{System.out.println( (double)(linkCounter*(4-Board.player2LinkCards)-virusCounter*(4-Board.player2VirusCards))/4
	);
	return (double)(linkCounter*(4-Board.player2LinkCards)-virusCounter*(4-Board.player2VirusCards))/4;
	
}
	// intro
	public boolean canSwap(Tile toTile) {
		return (toTile.hasPiece() && toTile.getPiece().owner == this.owner);
	}

	public void swap(Tile toTile) {

		fromTile.addPiece(toTile.removePiece());
		toTile.addPiece(this);

	}
	public void move(Point initialPos, Point finalPos) {
		position.x += finalPos.x - initialPos.x;
		position.y += finalPos.y - initialPos.y;
	}

	/**
	 * Sets the card's position to the designated point.
	 * 
	 * @param newPos
	 *            the new position of the card.
	 */
	public void setPosition(Point newPos) {
		this.position = newPos;
	}

}