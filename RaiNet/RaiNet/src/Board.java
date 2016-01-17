import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Change all player 2 to player -1

/**
 * A class to keep track of the board of the game
 * 
 * @author James Bai Marvin Li
 * @version December 25th, 2014
 */
public class Board extends JPanel implements MouseListener,
		MouseMotionListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Constants for the overall layout.
	public static final int WIDTH = 960;
	public static final int HEIGHT = 960;
	private final int ANIMATION_FRAMES = 6; // May need to change this number.

	// Constants for layout of the board.
	private final int NO_OF_ROWS = 8;
	private final int NO_OF_COLUMNS = 8;
	private final int STACK_AREA_CAPACITY = 7;
	private final int BOARD_X = 30; // May also need to change this number.
	private final int BOARD_Y = 150; // As well as this one.
	private final int STACK_AREA_X = 70; // Change
	private final int PLAYER_1_STACK_AREA_Y = 10; // These as
	private final int PLAYER_2_STACK_AREA_Y = 850; // Well!
	public final static Image BACKGROUND = new ImageIcon(
			"images\\RN Background.jpg").getImage();
	public final static Image READY_BUTTON = new ImageIcon("images\\RN Ready.jpg").getImage();
	// Variables for the Rai Net Game
	private RaiNetMain parentFrame;
	static Tile[][] board;
	static Tile[] player1StackArea;
	static Tile[] player2StackArea;
	static int player1VirusCards;
	static int player1LinkCards;
	static int player2VirusCards;
	static int player2LinkCards;
	private CardPiece selectedCard;
	static int playerTurn;
	boolean won;
	private Point lastPoint;
	private boolean setUp;
	private int mode; // 0 is neither, 1 is move, 2 is peek.
	boolean ai=true;

	private JButton setButton;
	static ArrayList<CardPiece> allCards;

	/**
	 * Constructs the Board, the player's Hands, and puts in the listeners.
	 * 
	 * @param parentFrame
	 *            the main Frame that holds this panel.
	 */
	public Board(RaiNetMain parentFrame) {
		// Set up the size and background colour
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.parentFrame = parentFrame; // We'll experiment with the
										// consequences of removing this later
										// to find out what this does.

		// Add mouse listeners to the Board
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// Sets up the Board.
		board = new Tile[NO_OF_ROWS][NO_OF_COLUMNS];
		// Does the Board
		int yPos = BOARD_Y;
		for (int row = 0; row < NO_OF_ROWS; row++) {
			int xPos = BOARD_X;
			for (int col = 0; col < NO_OF_COLUMNS; col++) {
				board[row][col] = new Tile(xPos, yPos, row, col);
				xPos += Tile.TILE_WIDTH;
			}
			yPos += Tile.TILE_HEIGHT;
		}
		// Then the Stack Areas

		// For Player 1.
		player1StackArea = new Tile[STACK_AREA_CAPACITY];
		yPos = PLAYER_1_STACK_AREA_Y;
		int xPos = STACK_AREA_X;
		for (int col = 0; col < STACK_AREA_CAPACITY; col++) {
			// -2 is used to indicate the Stack Area is 'above' the board.
			player1StackArea[col] = new Tile(xPos, yPos, -2, col);
			xPos += Tile.TILE_WIDTH;
		}
		// For Player 2
		player2StackArea = new Tile[STACK_AREA_CAPACITY];
		yPos = PLAYER_2_STACK_AREA_Y;
		xPos = STACK_AREA_X;
		for (int col = 0; col < STACK_AREA_CAPACITY; col++) {
			// 9 is used to indicate the Stack Area is 'below' the board.
			player2StackArea[col] = new Tile(xPos, yPos, 9, col);
			xPos += Tile.TILE_WIDTH;
		}
		// The Hands.
		// Add code.
		// Do we need Tiles? how will spacing work? Will they we centered?

		// We might need a menu screen.
		allCards = new ArrayList<CardPiece>();
		repaint();
	}

	/**
	 * Checks to see if the player has won by having 4 Link Cards in their Stack
	 * Area or 4 Virus Cards in their opponent's Stack Area.
	 * 
	 * @return 0 if noone has won; 1 if player 1 has won; 2 if player 2 has won.
	 */
	private int checkForWinner() {
		if (player1LinkCards == 4 || player2VirusCards == 4)
		{
			won=true;
			return 1;
		}
			if (player2LinkCards == 4 || player1VirusCards == 4)
			{
				won=true;
			return 2;
			}
		// No player has won yet.
		return 0;
	}

	/**
	 * Delays the given number of milliseconds
	 * 
	 * @param milliSec
	 *            number of milliseconds to delay
	 */
	private void delay(int milliSec) {
		try {
			Thread.sleep(milliSec);
		} catch (Exception e) {
		}
	}

	/**
	 * Handles the mouse moved events to show which Cards may be selected.
	 * 
	 * @param event
	 *            event information for mouse moved
	 */
	public void mouseMoved(MouseEvent event) {
		// Set the cursor to the hand if we are on a card that we can pick up
		Point currentPoint = event.getPoint();

		for (Tile[] nextRow : board)
			for (Tile nextTile : nextRow)
				if (nextTile.hasPiece() && nextTile.contains(currentPoint)
						&& nextTile.canPickUp()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					return;
				}

		// Otherwise we just use the default cursor.
		setCursor(Cursor.getDefaultCursor());
	}

	public void ai() {
		if(ai){
		System.out.print(playerTurn);
		if (playerTurn == 1) {
			//Priority 1: protect other piece
			for (CardPiece next : allCards)
			{
				if (next.owner==-1)
					{int attacking=0;
					for(Tile around:next.currentTile.around()){
						if(around.hasPiece()&&around.getPiece().owner==1&&!around.getPiece().isVirus)
					attacking++;
						if(attacking>1&&player2VirusCards<3){
							around.removePiece().moveTo(next.currentTile);
							playerTurn *= -1;
							repaint();
							return;
						}
					}
					}
			}
			for (CardPiece next : allCards) {
				//Second priority is to escape/capture while in interaction
				for (Tile[] nextRow : board)
					for (Tile nextTile : nextRow)
						if (next.aiCanMoveTo(nextTile) && nextTile.hasPiece()) {
							if (nextTile.getPiece().linkFactor()<=0 && !next.isVirus) {
								for (Tile around : next.currentTile.around()) {
									if (around.isSafe() && !around.hasPiece()) {
										Point pos = next.position;
										CardPiece temp = (CardPiece) next.currentTile
												.removePiece();
										temp.moveTo(around);
										moveAPiece(temp, pos, temp.position);
										playerTurn *= -1;
										repaint();
										return;
									}
								}
							} else if (nextTile.getPiece().linkFactor()>0) {
								Point pos = next.position;
								CardPiece temp = (CardPiece) next.currentTile
										.removePiece();
								temp.moveTo(nextTile);
								moveAPiece(temp, pos, temp.position);
								playerTurn *= -1;
								repaint();
								return;
							}
						}
//third priority is to leave the stack area 
				System.out.println(player1VirusCards + player1LinkCards);
						if (next.owner==1&&next.canLeaveTo(player1StackArea[player2VirusCards + player2LinkCards])) {
					player1StackArea[player2VirusCards + player2LinkCards]
							.addPiece(next.currentTile.removePiece());
					playerTurn *= -1;
					repaint();
					return;
				}
			}
			for (Tile[] nextRow : board) {
				for (Tile nextTile : nextRow) {
					if (nextTile.hasPiece() && nextTile.getPiece().linkFactor()>=0
							&& nextTile.getPiece().owner == 1)
						for (Tile around : nextTile.around())
							if (!around.isSafe()
									&& nextTile.getPiece().aiCanMoveTo(
											around)&&!around.hasPiece()) {
								((CardPiece) nextTile.removePiece())
										.moveTo(around);
								playerTurn *= -1;
								repaint();
								return;
							}
				}
			}
			for (Tile[] nextrow : board)
				for (Tile next : nextrow) {
					if (next.hasPiece()&&next.getPiece().owner==1) {
						if (next.row<5||(next.row < 7&&!next.getPiece().isVirus))
							if (next.getPiece().aiCanMoveTo(
									board[next.row + 1][next.col])
									&& board[next.row + 1][next.col].isSafe()) {
								Point pos = next.getPiece().position;
								CardPiece temp = (CardPiece) next.removePiece();
								temp.moveTo(board[next.row + 1][next.col]);
								moveAPiece(temp, pos, temp.position);
								playerTurn *= -1;
								repaint();
								return;
							}
						if (!next.getPiece().isVirus&&next.col > 4)
							if (next.getPiece().aiCanMoveTo(
									board[next.row][next.col - 1])) {
								((CardPiece) next.removePiece())
										.moveTo(board[next.row][next.col - 1]);
								playerTurn *= -1;
								repaint();
								return;
							}
					}
				}
		}
		}
	}

	/**
	 * Handles the mouse pressed events to select a card.
	 * 
	 * @param event
	 *            event information for mouse pressed
	 */
	public void mousePressed(MouseEvent event) {
if(!won){
		// Add code so that the selected card has a marker or something on it.
		// Similar to what happens in dn when you click a card.
		Point selectedPoint = event.getPoint();

		lastPoint = selectedPoint;

		// // A mode is selected.
		// // if // Insert code here.
		// To pick up a piece.
		if (selectedCard == null) {
			for (Tile[] nextRow : board)
				for (Tile nextTile : nextRow) {
					if (nextTile.contains(selectedPoint) && nextTile.hasPiece()
							&& nextTile.canPickUp()) {
						// If right click is held down.
						if (!setUp && event.getButton() == MouseEvent.BUTTON3) {
							nextTile.getPiece().flip();
						} else {
							selectedCard = nextTile.removePiece();
							lastPoint = selectedPoint;
						}

						repaint();
						return;
					}
				}
		}

	

		// Searches through the CardPiece objects to see if anything was
		// selected.
		for (Tile[] nextRow : board)
			for (Tile nextTile : nextRow)
				if (nextTile.hasPiece() && nextTile.contains(selectedPoint)
						&& nextTile.canPickUp()) {
					if (event.getButton() == MouseEvent.BUTTON3) {
						nextTile.getPiece().flip();
						repaint();
					} else {
						selectedCard = nextTile.removePiece();
						return;
					}
				}
		// Add in code so that the selected card is highlighted like in dn.
		repaint();
}
	}

	/**
	 * Moves a Piece during the animation
	 * 
	 * @param PieceToMove
	 *            the CardPiece that you want to move
	 * @param fromPos
	 *            the original position of the Piece
	 * @param toPos
	 *            the destination position of the piece
	 */
	public void moveAPiece(CardPiece PieceToMove, Point fromPos, Point toPos) {
		int dx = (toPos.x - fromPos.x) / ANIMATION_FRAMES; // We might want to
															// refractor this
															// variable.
		int dy = (toPos.y - fromPos.y) / ANIMATION_FRAMES; // As well as this
															// one.

		for (int times = 1; times <= ANIMATION_FRAMES; times++) // And this one.
		{
			fromPos.x += dx;
			fromPos.y += dy;
			PieceToMove.setPosition(fromPos);

			// Update the drawing area.
			paintImmediately(0, 0, getWidth(), getHeight());
			delay(30); // Maybe change the number in here.

		}
		PieceToMove.setPosition(toPos);
		repaint();
	}

	/**
	 * Makes a new card by resetting the board and the hands.
	 */
	public void newGame() {
		// Resets the Board.
		allCards.clear();
		won=false;
		player1LinkCards=0;
		player2LinkCards=0;
		player1VirusCards=0;
		player2VirusCards=0;
		// Resets the Board.
		for (Tile[] nextRow : board)
			for (Tile nextTile : nextRow)
				if (nextTile.hasPiece())
					nextTile.removePiece();
		for (Tile nextTile :player1StackArea)
				if (nextTile.hasPiece())
					nextTile.removePiece();
		for (Tile nextTile :player2StackArea)
			if (nextTile.hasPiece())
				nextTile.removePiece();
		LinkedList<CardPiece>deck=new LinkedList<CardPiece>();
		deck.add(new CardPiece(new Point(0, 0), false, 1));
		deck.add(new CardPiece(new Point(0, 0), false, 1));
		deck.add(new CardPiece(new Point(0, 0), false, 1));
		deck.add(new CardPiece(new Point(0, 0), false, 1));
		deck.add(new CardPiece(new Point(0, 0), true, 1));
		deck.add(new CardPiece(new Point(0, 0), true, 1));
		deck.add(new CardPiece(new Point(0, 0), true, 1));
		deck.add(new CardPiece(new Point(0, 0), true, 1));

		Collections.shuffle(deck);
		board[0][0].addPiece(deck.remove());
		board[0][1].addPiece(deck.remove());
		board[0][2].addPiece(deck.remove());
		board[1][3].addPiece(deck.remove());
		board[1][4].addPiece(deck.remove());
		board[0][5].addPiece(deck.remove());
		board[0][6].addPiece(deck.remove());
		board[0][7].addPiece(deck.remove());
		board[7][0].addPiece(new CardPiece(new Point(80, 80), false, -1));
		board[7][1].addPiece(new CardPiece(new Point(0, 0), false, -1));
		board[7][2].addPiece(new CardPiece(new Point(0, 0), false, -1));
		board[6][3].addPiece(new CardPiece(new Point(0, 0), false, -1));
		board[6][4].addPiece(new CardPiece(new Point(0, 0), true, -1));
		board[7][5].addPiece(new CardPiece(new Point(0, 0), true, -1));
		board[7][6].addPiece(new CardPiece(new Point(0, 0), true, -1));
		board[7][7].addPiece(new CardPiece(new Point(0, 0), true, -1));

		for (Tile[] nextRow : board)
			for (Tile nextTile : nextRow)
				if (nextTile.hasPiece())
					allCards.add(nextTile.getPiece());

		if(ai)
		{
			for (Tile[] nextRow : board)
				for (Tile nextTile : nextRow)
					if(nextTile.hasPiece()&&nextTile.getPiece().owner==1)
				nextTile.getPiece().flip();
		}
		playerTurn = -1;
		setUp = true;
		setButton = new JButton("Ready");
		setButton.setBounds(270, 430, 160, 80);
		this.add(setButton);
		setButton.addActionListener(this);
		repaint();
	}

	/**
	 * Draws out the pieces, the cards, and the animations.
	 * 
	 * @param g
	 *            the Graphics context to do the drawing
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(BACKGROUND, 0, 0, null);
		// Draws out the pieces.
		for (Tile[] nextRow : board)
			for (Tile nextTile : nextRow)
				nextTile.draw(g);
		if (selectedCard != null)
		{
			for(Tile next:selectedCard.fromTile.around())
				if(selectedCard.canMoveTo(next))
					next.hightlight(g);
			selectedCard.draw(g);
		}
		CardPiece show=new CardPiece(new Point(700,430),false, playerTurn);
		show.flip();
		show.draw(g);
		// Draws out the Stack Areas.
		// Player 1.
		for (Tile nextTile : player1StackArea)
			nextTile.draw(g);
		for (Tile nextTile : player2StackArea)
			nextTile.draw(g);


	}

	@Override
	public void mouseDragged(MouseEvent event) {

		Point currentPoint = event.getPoint();

		if (selectedCard != null) {
			selectedCard.move(lastPoint, currentPoint);
			lastPoint = currentPoint;
			repaint();
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		// Do the set up button here.
		// setUp = false;

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent event) {

		Point currentPoint = event.getPoint();

		if (selectedCard != null) {
			if (setUp) {
				// Searches through the board.
				for (Tile[] nextRow : board)
					for (Tile nextTile : nextRow)
						if (nextTile.hasPiece()
								&& nextTile.contains(currentPoint)
								&& ((CardPiece) selectedCard).canSwap(nextTile)) {
							((CardPiece) selectedCard).swap(nextTile);
							selectedCard = null;
							repaint();
							return;
						}

				if (selectedCard != null) {
					selectedCard.move(lastPoint, currentPoint);
					lastPoint = currentPoint;
					repaint();
				}
			} else {
				// Searches the stack area.
				if (playerTurn == 1) {
					for (Tile nextTile : player1StackArea) {
						if (nextTile.contains(currentPoint)
								&& ((CardPiece) selectedCard)
										.canLeaveTo(nextTile)) {
							((CardPiece) selectedCard).moveTo(nextTile);
							selectedCard = null;
							playerTurn *= -1;
							repaint();
							ai();
							if (!won && checkForWinner() != 0) {
								JOptionPane
										.showMessageDialog(parentFrame,
												"Player " + checkForWinner()
														+ " wins!",
												"Congratulations!",
												JOptionPane.INFORMATION_MESSAGE);
								// To prevent the message from popping up more
								// than once
								won = true;
							}
							return;
						}
					}
				} else {
					for (Tile nextTile : player2StackArea) {
						if (nextTile.contains(currentPoint)
								&& ((CardPiece) selectedCard)
										.canLeaveTo(nextTile)) {
							((CardPiece) selectedCard).moveTo(nextTile);
							selectedCard = null;
							playerTurn *= -1;
							repaint();
							ai();
							if (!won && checkForWinner() != 0) {
								JOptionPane
										.showMessageDialog(parentFrame,
												"Player " + checkForWinner()
														+ " wins!",
												"Congratulations!",
												JOptionPane.INFORMATION_MESSAGE);
								// To prevent the message from popping up more
								// than once
								won = true;
							}
							return;
						}
					}
				}
				// Searches the board.
				for (Tile[] nextRow : board)
					for (Tile nextTile : nextRow) {
						if (nextTile.contains(currentPoint)
								&& ((CardPiece) selectedCard)
										.canMoveTo(nextTile)) {
							((CardPiece) selectedCard).moveTo(nextTile);
							selectedCard = null;
							playerTurn *= -1;
							repaint();
							ai();
							if (!won && checkForWinner() != 0) {
								JOptionPane
										.showMessageDialog(parentFrame,
												"Player " + checkForWinner()
														+ " wins!",
												"Congratulations!",
												JOptionPane.INFORMATION_MESSAGE);
								// To prevent the message from popping up more
								// than once
								won = true;
							}
							return;
						}
					}
			}

			((CardPiece) selectedCard).back();
			selectedCard = null;

		}

		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub

		if (event.getSource() == setButton){
			if(ai){
				this.remove(setButton);	
				repaint();
				setUp=false;
			}
			else{
			if (playerTurn == 1) {
				setUp = false;
				this.remove(setButton);

			}
			playerTurn *= -1;
			for (Tile[] nextRow : board)
				for (Tile nextTile : nextRow) {
					if (!nextTile.tile.isEmpty()) {
						if (nextTile.getPiece().owner != playerTurn)
							nextTile.getPiece().flip();
					}
					repaint();
				}
		}
		}
	}
}