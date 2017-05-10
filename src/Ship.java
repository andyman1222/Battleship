import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class Ship extends JComponent {
	private int xSize, ySize, x, y;
	private Board board;
	private boolean movable = true, rotate = false;
	private boolean dragShip = false;
	private boolean destroyed = false;
	private boolean[][] damagedSections;
	private int xSizeScl = xSize * board.getSquaresXsize(), ySizeScl = ySize * board.getSquaresYsize(), xScl = x * board.getSquaresXsize(), yScl = y * board.getSquaresYsize();
	
	/**
	 * create a new ship with specified info
	 * @param board board the ship is on
	 * @param xSize width of ship
	 * @param ySize height of ship
	 * @param x x pos of ship
	 * @param y y pos of ship
	 */
	public Ship(Board board, int xSize, int ySize, int x, int y) {
		this.board = board;
		this.xSize = xSize;
		this.ySize = ySize;
		damagedSections = new boolean[xSize][ySize];
		// System.out.println("Listener added");
		this.x = x;
		this.y = y;
		xSizeScl = xSize * board.getSquaresXsize();
		ySizeScl = ySize * board.getSquaresYsize();
		xScl = x * board.getSquaresXsize();
		yScl = y * board.getSquaresYsize();
		// System.out.println(this);
		repaint();
	}

	/**
	 * creates a new ship not on a board. Use in an instance array to copy to via Board's copy method
	 * @param xSize width of ship
	 * @param ySize height of ship
	 * @param x x position of ship
	 * @param y y position of ship
	 */
	public Ship(int xSize, int ySize, int x, int y) {
		this.xSize = xSize;
		this.ySize = ySize;
		// System.out.println("Listener added");
		this.x = x;
		this.y = y;
		// System.out.println(this);
	}

	@Override
	/**
	 * @return a Rectangle representing the tiles the ship is located on (note: Rectangle is larger than what is drawn because it includes the whole tile)
	 */
	public Rectangle getBounds() {
		return new Rectangle(x, y, xSize, ySize);
	}

	/**
	 * 
	 * @param x x position to test
	 * @param y y position to test
	 * @return a Rectangle representing the tiles the ship will be located on if it was located at (x, y)
	 */
	public Rectangle getBounds(int x, int y) {
		return new Rectangle(x, y, xSize, ySize);
	}

	/**
	 * Draws the ship and any damaged sections
	 */
	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		if (destroyed == true) {
			g.setColor(Color.RED);
			g.fillRect(xScl + 5, yScl + 5, xSizeScl - 10, ySizeScl - 10);
		} else {
			if (board.getPlayer() == Main.localPlayer||Main.dev == true) {
				g.setColor(Color.GRAY);
				g.fillRect(xScl + 5, yScl + 5, xSizeScl - 10, ySizeScl - 10);
			}
			for (int i = 0; i < damagedSections.length; i++) {
				for (int ii = 0; ii < damagedSections[i].length; ii++) {
					if (damagedSections[i][ii]) {
						g.setColor(Color.RED);
						if(!rotate)
							g.fillOval(xScl + (i * board.getSquaresXsize()+(board.getSquaresXsize()/8)), yScl + (ii * board.getSquaresXsize()+(board.getSquaresXsize()/8)),
								(int) (board.getSquaresXsize()-10), (int) (board.getSquaresYsize() -10));
						else
							g.fillOval(xScl + (ii * board.getSquaresXsize()+(board.getSquaresXsize()/8)), yScl + (i * board.getSquaresXsize()+(board.getSquaresXsize()/8)),
									(int) (board.getSquaresXsize() -10), (int) (board.getSquaresYsize() -10));
					}
				}
			}
		}
	}

	private boolean collidingWithAnotherShip() {
		for (Ship compare : board.getShips())
			if (compare != this)
				if (compare.getBounds().intersects(getBounds()))
					return true;
		return false;
	}

	private boolean collidingWithAnotherShip(int x, int y) {
		for (Ship compare : board.getShips())
			if (compare != this)
				if (compare.getBounds().intersects(getBounds(x, y))) {
					//System.out.println(compare.getBounds().intersection(getBounds(x, y)));
					return true;
				}
		return false;
	}

	private boolean isInBoardBounds(int x, int y) {
		return x >= 0 && y >= 0 && x + xSize < board.getWidth() && y + ySize < board.getHeight();
	}

	private int toBoardDimensions(int i, int dimension) {
		return (int) Math.floor(i / dimension) * dimension;
	}

	public boolean isInBounds(Point e) {
		return toBoardDimensions((int) e.getX(), board.getSquaresXsize()) >= x
				&& toBoardDimensions((int) e.getY(), board.getSquaresYsize()) >= y
				&& toBoardDimensions((int) e.getX(), board.getSquaresXsize()) <= x + xSize-1
				&& toBoardDimensions((int) e.getY(), board.getSquaresYsize()) <= y + ySize-1;

	}

	/**
	 * change the movable state of the ship
	 */
	protected void changeMovable() {
		movable = !movable;
	}

	/**
	 * change the movable state of the ship
	 * @param bool state to change to
	 */
	protected void changeMovable(boolean bool) {
		movable = bool;
	}

	/**
	 * check if the ship is movable
	 * @return true if it is
	 */
	public boolean isMovable() {
		return movable;
	}

	/**
	 * rotates the ship 90°
	 */
	protected void rotate() {
		int temp = xSize;
		xSize = ySize;
		ySize = temp;
		rotate = !rotate;
		if (collidingWithAnotherShip() || !isInBoardBounds(x, y)) {
			temp = xSize;
			xSize = ySize;
			ySize = temp;
			rotate = !rotate;
		}

	}
	
	/**
	 * rotates the ship 90° and returns whether or not it was sucessful.
	 * @return true if the ship rotated, false if something interfered (collision)
	 */
	protected boolean rotateVal() {
		int temp = xSize;
		xSize = ySize;
		ySize = temp;
		rotate = !rotate;
		if (collidingWithAnotherShip() || !isInBoardBounds(x, y)) {
			temp = xSize;
			xSize = ySize;
			ySize = temp;
			rotate = !rotate;
			return false;
		}
		else return true;

	}

	/**
	 * causes ship to stop being dragged by mouse
	 */
	protected void stopMoving() {
		dragShip = false;

	}

	/**
	 * moves the ship to e if possible
	 * @param e point to move the ship to
	 */
	protected void drag(Point e) {
		int mX = toBoardDimensions((int) e.getX(), board.getSquaresYsize());
		int mY = toBoardDimensions((int) e.getY(), board.getSquaresYsize());
		if (isInBoardBounds(mX, mY) && !collidingWithAnotherShip(mX, mY)) {
			this.x = mX;
			this.y = mY;
			System.out.println("Change pos to " + x + ", " + y);
		}

	}
	
	/**
	 * move the ship to (x, y) with rotation rotate
	 * @param x x pos to move the ship to
	 * @param y y pos to move the ship to
	 * @param rotate rotation of the ship
	 */
	protected void position(int x, int y, boolean rotate){
		if (isInBoardBounds(x, y) && !collidingWithAnotherShip(x, y)) {
			this.x = x;
			this.y = y;
			System.out.println("Change pos to " + x + ", " + y);
			if(rotate) rotate();
		}
		
	}
	
	/**
	 * move the ship to (x, y) with rotation rotate
	 * @param x x pos to move the ship to
	 * @param y y pos to move the ship to
	 * @param rotate rotation of the ship
	 * @return true if it moved successfully, false otherwise (collision)
	 */
	protected boolean positionVal(int x, int y, boolean rotate){
		if (isInBoardBounds(x, y) && !collidingWithAnotherShip(x, y)) {
			this.x = x;
			this.y = y;
			System.out.println("Change pos to " + x + ", " + y);
			if(rotate) return rotateVal();
			else return true;
		}
		else return false;
	}

	/**
	 * copies this ship to a board. Use with the constructor that does not have a Board param
	 * @param board board the copied ship belongs to
	 * @return new ship that belongs to a board
	 */
	protected Ship copy(Board board) {
		return new Ship(board, x, y, xSize, ySize);
	}

	/**
	 * attacks the ship
	 * @param e point to attack the ship at
	 * @return true if it hit, false otherwise (attempting to hit a region already hit)
	 */
	public boolean attack(Point e) {
		int mX = toBoardDimensions((int) e.getX(), board.getSquaresYsize());
		int mY = toBoardDimensions((int) e.getY(), board.getSquaresYsize());
		System.out.println("" + (mX-x)/board.getSquaresXsize() + " " + (mY-y)/board.getSquaresYsize());
		int hitX = (mX-x)/board.getSquaresXsize(), hitY = (mY-y)/board.getSquaresYsize();
		if(!rotate){
			if (damagedSections[hitX][hitY] == false){
				damagedSections[hitX][hitY] = true;
				//System.out.println("Registered hit");
				return true;
			}
			else return false;
		}
		else if(damagedSections[hitY][hitX] == false){
			damagedSections[hitY][hitX] = true;
			//System.out.println("Registered hit");
			return true;
		}
		else return false;
	}
	
	public boolean checkDestroyed(){
		if(destroyed == true)
			return true;
		for(boolean[] i : damagedSections)
			for(boolean sect : i)
				if(sect == false)
					return false;
		destroyed = true;
		System.out.println("Ship destroyed!");
		return true;
	}
}
