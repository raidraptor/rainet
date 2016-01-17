import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Keeps track of data associated with tiles.
 * 
 * @author Marvin Li and James Bai
 * @version January 4th, 2015
 */
public class Tile {
	final static int TILE_WIDTH = 80; // Subjected to
	final static int TILE_HEIGHT = 80; // change~
	private Image image;
	Point position;
	int row;
	int col;
	ArrayList<CardPiece> tile = new ArrayList<CardPiece>(1);

	/**
	 * Creates a tile at the designated x and y.
	 * 
	 * @param x
	 *            the x coordinate of the top left corner of the tile
	 * @param y
	 *            the y coordinate of the top left corner of the tile
	 */
	// Need to implement image
	public Tile(int x, int y, int row, int col) {
		position = new Point(x, y);
		this.row = row;
		this.col = col;
	}

	/**
	 * Adds a piece to the tile.
	 * 
	 * @param newCard
	 *            the card that will be added.
	 */
	public void addPiece(CardPiece newCard) {
		tile.add(newCard);
		newCard.setPosition(new Point(position));
		newCard.currentTile=this;
		System.out.println(Board.playerTurn);

	}

	/**
	 * Removes a piece from the tile.
	 * 
	 * @return the card that is removed.
	 */
	public CardPiece removePiece() {
		if(this.hasPiece())
		((CardPiece) tile.get(0)).fromTile = this;
		return (tile.remove(0));
	}

	public CardPiece getPiece() {
		return ((CardPiece) (this.tile.get(0)));
	}

	public boolean canPickUp() {
		if (this.getPiece().owner == Board.playerTurn) // Make sure not to pick up a
													// fire wall (or from stack
													// area)
			return true;
		return false;
	}

	/**
	 * Removes the cardPiece from the tile and adds 1 to the owner's link or
	 * virus cards.
	 * 
	 * @return the captured piece.
	 */
	public CardPiece captured() {
		// Modify variables first.
		if (this.tile.get(0).owner == 1) {
			if (((CardPiece) (this.tile.get(0))).isVirus) {
				Board.player1VirusCards++;
			} else {
				Board.player1LinkCards++;
			}

			// Adds the removed piece to the appropriate stack area.
			for (Tile nextTile : Board.player2StackArea) {
				if (!nextTile.hasPiece()) {
					this.getPiece().flip();
					nextTile.addPiece(this.getPiece());
Board.allCards.remove(this);
					System.out.println(Board.player1VirusCards + " : "
							+ Board.player1LinkCards + " - " + Board.player2VirusCards + " : "
							+ Board.player2LinkCards);

					// Removes the piece.
					return (CardPiece) removePiece();
				}
			}

		} else {
			if (((CardPiece) (this.tile.get(0))).isVirus) {
				Board.player2VirusCards++;
			} else {
				Board.player2LinkCards++;
			}

			// Adds the removed piece to the appropriate stack area.
			for (Tile nextTile : Board.player1StackArea) {
				if (!nextTile.hasPiece()) {
					this.getPiece().flip();
					nextTile.addPiece(this.getPiece());

					System.out.println(Board.player1VirusCards + " : "
							+ Board.player1LinkCards + " - " + Board.player2VirusCards + " : "
							+ Board.player2LinkCards);
					Board.allCards.remove(getPiece());
					// Removes the piece.
					return (CardPiece) removePiece();
				}
			}
		}
		return null;
	}

	/**
	 * Determines if the selected point is within the tile.
	 * 
	 * @param selectedPoint
	 *            the selected point
	 * @return true if the selected point is within the tile. false otherwise.
	 */
	public boolean contains(Point selectedPoint) {
		return (new Rectangle(position.x, position.y, TILE_WIDTH, TILE_HEIGHT))
				.contains(selectedPoint);
	}

	/**
	 * Draws the card.
	 * 
	 * @param g
	 */
	public void draw(Graphics g) {
		// An image would be greatly appreciated.
		g.setColor(Color.BLUE);
		g.drawRect(position.x, position.y, TILE_WIDTH, TILE_HEIGHT);

		for (Card next : tile)
			next.draw(g);
	}
	public void hightlight(Graphics g) {
		// An image would be greatly appreciated.
		g.setColor(new Color(0,0,0,50));
		g.fillRect(position.x, position.y, TILE_WIDTH, TILE_HEIGHT);

		for (Card next : tile)
			next.draw(g);
	}
	/**
	 * Returns whether or not is there a piece on the tile.
	 * 
	 * @return true if there is a piece on the tile. false otherwise.
	 */
	public boolean hasPiece() {
		return (!tile.isEmpty());
	}

	/**
	 * Removes the cardPiece from the tile and adds 1 to the owner's link or
	 * virus cards.
	 * 
	 * @return the card that is removed.
	 */
	public CardPiece leave() {
		// Modify variables first.
		if (this.tile.get(0).owner == 1) {
			if (((CardPiece) (this.tile.get(0))).isVirus)
				Board.player1VirusCards++;
			Board.player1LinkCards++;
		} else {
			if (((CardPiece) (this.tile.get(0))).isVirus)
				Board.player2VirusCards++;
			Board.player2LinkCards++;
		}

		// Removes the piece.
		return (CardPiece) removePiece();
	}

	/**
	 * Swaps the contents of the two tiles. Line Boost does not move.
	 * 
	 * @param tile1
	 *            The first tile.
	 * @param tile2
	 *            The second tile.
	 */
	public void swap(Tile otherTile) {
		// if (((CardPiece)(this.tile.get(0))).lineBoost)
		// {
		// ((CardPiece)(this.tile.get(0))).lineBoost = false;
		// ((CardPiece)(otherTile.tile.get(0))).lineBoost = true;
		// }
		// if (((CardPiece)(otherTile.tile.get(0))).lineBoost)
		// {
		// ((CardPiece)(this.tile.get(0))).lineBoost = true;
		// ((CardPiece)(otherTile.tile.get(0))).lineBoost = false;
		// }

		CardPiece tempCard = this.tile.remove(0);
		this.tile.add(otherTile.tile.remove(0));
		otherTile.tile.add(tempCard);
	}
	public ArrayList<Tile>around()
	{
		ArrayList <Tile>list=new ArrayList<Tile>(4);
		if(row<7)
		list.add(Board.board[row+1][col]);
		if(row>0)
		list.add(Board.board[row-1][col]);
		if(col>0)
		list.add(Board.board[row][col-1]);
		if(col<7)
		list.add(Board.board[row][col+1]);
		return list;		
	}
	public boolean isSafe()
	{
			for(Tile next:this.around())
				if(next.hasPiece()&&next.getPiece().owner!=Board.playerTurn)
					return false;
			return true;
	}
	
}
