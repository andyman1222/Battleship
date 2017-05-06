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

	/**
	 * Creates a ship in the ship placing menu
	 * 
	 * @param board
	 *            board to place the ship on
	 * @param xSize
	 * @param ySize
	 */

	public Ship(Board board, int xSize, int ySize, int x, int y) {
		this.board = board;
		this.xSize = xSize * board.getSquaresXsize();
		this.ySize = ySize * board.getSquaresYsize();
		damagedSections = new boolean[xSize][ySize];
		// System.out.println("Listener added");
		this.x = x * board.getSquaresXsize();
		this.y = y * board.getSquaresYsize();
		// System.out.println(this);
		repaint();
	}

	public Ship(int xSize, int ySize, int x, int y) {
		this.xSize = xSize;
		this.ySize = ySize;
		// System.out.println("Listener added");
		this.x = x;
		this.y = y;
		// System.out.println(this);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, xSize, ySize);
	}

	public Rectangle getBounds(int x, int y) {
		return new Rectangle(x, y, xSize, ySize);
	}

	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		if (destroyed == true) {
			g.setColor(Color.RED);
			g.fillRect(x + 5, y + 5, xSize - 10, ySize - 10);
		} else {
			if (board.getPlayer() == Main.localPlayer||Main.dev == true) {
				g.setColor(Color.GRAY);
				g.fillRect(x + 5, y + 5, xSize - 10, ySize - 10);
			}
			for (int i = 0; i < damagedSections.length; i++) {
				for (int ii = 0; ii < damagedSections[i].length; ii++) {
					if (damagedSections[i][ii]) {
						g.setColor(Color.RED);
						if(!rotate)
							g.fillOval(x + (i * board.getSquaresXsize()+(board.getSquaresXsize()/8)), y + (ii * board.getSquaresXsize()+(board.getSquaresXsize()/8)),
								(int) (board.getSquaresXsize()-10), (int) (board.getSquaresYsize() -10));
						else
							g.fillOval(x + (ii * board.getSquaresXsize()+(board.getSquaresXsize()/8)), y + (i * board.getSquaresXsize()+(board.getSquaresXsize()/8)),
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
					System.out.println(compare.getBounds().intersection(getBounds(x, y)));
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

	public boolean mouseInBounds(Point e) {
		return toBoardDimensions((int) e.getX(), board.getSquaresXsize()) >= x
				&& toBoardDimensions((int) e.getY(), board.getSquaresYsize()) >= y
				&& toBoardDimensions((int) e.getX(), board.getSquaresXsize()) <= x + xSize-1
				&& toBoardDimensions((int) e.getY(), board.getSquaresYsize()) <= y + ySize-1;

	}

	protected void changeMovable() {
		movable = !movable;
	}

	protected void changeMovable(boolean bool) {
		movable = bool;
	}

	public boolean isMovable() {
		return movable;
	}

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

	protected void stopMoving() {
		dragShip = false;

	}

	protected void drag(Point e) {
		int mX = toBoardDimensions((int) e.getX(), board.getSquaresYsize());
		int mY = toBoardDimensions((int) e.getY(), board.getSquaresYsize());
		if (isInBoardBounds(mX, mY) && !collidingWithAnotherShip(mX, mY)) {
			this.x = mX;
			this.y = mY;
			System.out.println("Change pos to " + x + ", " + y);
		}

	}
	
	protected void position(int x, int y, boolean rotate){
		if (isInBoardBounds(x, y) && !collidingWithAnotherShip(x, y)) {
			this.x = x;
			this.y = y;
			System.out.println("Change pos to " + x + ", " + y);
			if(rotate) rotate();
		}
		
	}
	
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

	protected Ship copy(Board board) {
		return new Ship(board, x, y, xSize, ySize);
	}

	public boolean attack(Point e) {
		int mX = toBoardDimensions((int) e.getX(), board.getSquaresYsize());
		int mY = toBoardDimensions((int) e.getY(), board.getSquaresYsize());
		System.out.println("" + (mX-x)/board.getSquaresXsize() + " " + (mY-y)/board.getSquaresYsize());
		int hitX = (mX-x)/board.getSquaresXsize(), hitY = (mY-y)/board.getSquaresYsize();
		if(!rotate){
			if (damagedSections[hitX][hitY] == false){
				damagedSections[hitX][hitY] = true;
				return true;
			}
			else return false;
		}
		else if(damagedSections[hitY][hitX] == false){
			damagedSections[hitY][hitX] = true;
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
