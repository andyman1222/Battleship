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
	private int xScl, yScl;
	
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
		xScl = board.getSquaresXsize();
		yScl = board.getSquaresYsize();
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
			g.fillRect(x*xScl + 5, y*yScl + 5, xSize*xScl - 10, ySize*yScl - 10);
		} else {
			if (board.getPlayer() == Main.localPlayer||Main.dev == true) {
				g.setColor(Color.GRAY);
				g.fillRect(x*xScl + 5, y*yScl + 5, xSize*xScl - 10, ySize*yScl - 10);
			}
			for (int i = 0; i < damagedSections.length; i++) {
				for (int ii = 0; ii < damagedSections[i].length; ii++) {
					if (damagedSections[i][ii]) {
						g.setColor(Color.RED);
						if(!rotate)
							g.fillOval(x*xScl + (i *xScl+(xScl/8)), y*yScl + (ii *yScl+(yScl/8)),
								(int) (xScl-10), (int) (yScl -10));
						else
							g.fillOval(x*xScl + (ii * xScl+(xScl/8)), y*yScl + (i * xScl+(xScl/8)),
									(int) (xScl -10), (int) (yScl -10));
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
		System.out.println("" + x  + ">=" + 0 + "&&" + y + ">=" + 0 + "&&" + (x + xSize) + "<" + board.getX() + "&&" + (y + ySize) + "<" + board.getY());
		return x >= 0 && y >= 0 && x + xSize <= board.getX() && y + ySize <= board.getY();
	}

	private int toJFrameDimensions(int i, int dimension) {
		return i * dimension;
	}
	
	private int toBoardDimensions(int i, int dimension) {
		return i / dimension;
	}

	/**
	 * checks if point e is within the ship's boundries, relative to board dimensions (not JFrame)
	 * @param e point to check
	 * @return true if e is in bounds
	 */
	public boolean isInBounds(Point e) {
		return (int) e.getX() >= x
				&& e.getY() >= y
				&& e.getX() <= x + xSize-1
				&& e.getY() <= y + ySize-1;

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
	 * @param e point to move the ship to, relative to the board
	 */
	protected void drag(Point e) {
		if (isInBoardBounds((int)e.getX(), (int)e.getY()) && !collidingWithAnotherShip((int)e.getX(), (int)e.getY())) {
			this.x = (int)e.getX();
			this.y = (int)e.getY();
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
		//System.out.println("" + x + " " + y);
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
	 * @param e point to attack the ship at, relative to the board
	 * @return 
	 */
	public AttackStates attack(Point e) {
		if(isInBounds(e)){
			int mX = (int) e.getX();
			int mY = (int) e.getY();
			System.out.println("" + mX + " " + mY);
			int hitX = mX-x, hitY = mY-y;
			if(!rotate){
				if (damagedSections[hitX][hitY] == false){
					damagedSections[hitX][hitY] = true;
					//System.out.println("Registered hit");
					if(checkDestroyed())return AttackStates.HIT_AND_DESTROYED;
					else return AttackStates.HIT;
				}
			}
			else if(damagedSections[hitY][hitX] == false){
				damagedSections[hitY][hitX] = true;
				//System.out.println("Registered hit");
				if(checkDestroyed())return AttackStates.HIT_AND_DESTROYED;
				else return AttackStates.HIT;
			}
			if(checkDestroyed()) return AttackStates.ALREADY_HIT_AND_DESTROYED;
			else return AttackStates.ALREADY_HIT;
		}
		else return AttackStates.MISS;
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
