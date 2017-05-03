import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class Ship extends JComponent implements MouseListener, Border{
	private int xSize, ySize, x, y;
	private Board board;
	private boolean mouseInBound, movable;
	private boolean dragShip = false;
	
	/**
	 * Creates a ship in the ship placing menu
	 * @param board board to place the ship on
	 * @param xSize
	 * @param ySize
	 */
	public Ship(Board board, int xSize, int ySize) {
		this.board = board;
		this.xSize = xSize * board.getWidth();
		this.ySize = ySize * board.getHeight();
		// board.addMouseListener(this);
		addMouseListener(this);
		this.setBorder(this);
	}
	
	public Ship(Board board, int xSize, int ySize, int x, int y){
		this(board, xSize, ySize);
		this.x = x;
		this.y = y;
		System.out.println(this);
		repaint();
	}

	public Rectangle getBounds(){
		return new Rectangle(x, y, xSize, ySize);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		System.out.println("Runs a ship");
		g.fillRect(x-5, y-5, xSize-5, ySize-5);
	}

	private boolean mouseInBounds() {
		return mouseInBound;
	}

	private boolean collidingWithAnotherShip() {
		for(Ship compare : board.getShips())
			if(getBounds().intersects(compare.getBounds()))
				return true;
		return false;
	}

	private boolean isInBoardBounds() {
		return x>=0&&y>=0&&x+xSize<=board.getWidth()&&y+ySize<=board.getHeight();
	}
	
	private void drag(MouseEvent e){
		int origX = x, origY = y;
		while(dragShip){
			while(isInBoardBounds()){
				x = e.getX();
				y = e.getY();
				repaint();
			}
		}
		if(collidingWithAnotherShip()){
			x = origX;
			y = origY;
		}
	}
	
	public void changeMovable(){
		movable = !movable;
	}
	
	public void changeMovable(boolean bool){
		movable = bool;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (mouseInBounds()) {
			if(movable == true){
				int temp = xSize;
				xSize = ySize;
				ySize = temp;
			}
			else if(Main.getCurrentPlayer() == board.getPlayer()){
				//do nothing
			}
			else{
				//attack
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseInBound = true;

	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseInBound = false;

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (mouseInBounds()) {
			if(movable == true){
				dragShip = true;
				drag(e);
			}
			else if(Main.getCurrentPlayer() == board.getPlayer()){
				//do nothing
			}
			else{
				//attack
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mouseInBounds()) {
			if(movable = true){
				dragShip = false;
			}
			else if(Main.getCurrentPlayer() == board.getPlayer()){
				//do nothing
			}
			else{
				//attack
			}
		}

	}

	@Override
	public Insets getBorderInsets(Component arg0) {
		return new Insets(0, 0, 0, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component arg0, Graphics g, int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
		
	}
}
