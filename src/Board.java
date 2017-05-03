
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JPanel{
	private JFrame frame;
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private int squaresXsize, squaresYsize, width, height;
	private Player player;
	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height){
		frame = new JFrame("" + player);
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				Main.closeAllWindows();
			}
		});
		frame.setBounds(x, y, width, height+250);
		frame.add(this);
		frame.setVisible(true);
		this.width = width;
		this.height = height;
		this.squaresXsize = width/squaresX;
		this.squaresYsize = height/squaresY;
		this.player = player;
	}
	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height, Ship[] initShips){
		this(player, x, y, squaresX, squaresY, width, height);
		createShips(initShips);
	}
	
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		for(int x = 0; x+squaresXsize < width; x+=squaresXsize)
			for(int y = 0; y+squaresYsize < height; y+=squaresYsize){
				g.drawRect(x, y, squaresXsize, squaresYsize);
			}
		for(Ship ship : ships)
			ship.paintComponent(g);
	}
	
	public void createShip(int xSize, int ySize){
		Ship newShip = new Ship(this, xSize, ySize);
		ships.add(newShip);
		frame.add(newShip);
	}
	
	public void createShip(int xSize, int ySize, int x, int y){
		Ship newShip = new Ship(this, xSize, ySize, x, y);
		ships.add(newShip);
		frame.add(newShip);
		newShip.repaint();
	}
	
	public void createShips(Ship[] shipsArr){
		for(Ship ship : shipsArr){
			ships.add(ship);
			frame.add(ship);
		}
	}
	
	public void closeJframe(){
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	public ArrayList<Ship> getShips(){
		return ships;
	}
	
	public Player getPlayer(){
		return player;
	}
}
