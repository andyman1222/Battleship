
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Board extends JPanel {
	private JFrame frame;
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private int squaresXsize, squaresYsize, width, height;
	private Player player;
	private Ship movingShip;
	private boolean[][] misses = new boolean[width][height];
	private Container pane;
	private Button startGame = new Button("Start game");
	private Button randomizeShips = new Button("Randomize ships");
	private int floatingShips;
	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height, boolean showBtn) {
		frame = new JFrame("" + player);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Main.closeAllWindows();
			}
		});
		frame.addWindowFocusListener(new WindowAdapter() {

			public void windowGainedFocus(WindowEvent e) {
				System.out.println("Focus gained");
				if (e.getOppositeWindow() == null) {
					Main.changeFocus();
					setFocus();
				}
			}
		});
		frame.setBounds(x, y, width, height + 250);
		frame.add(this);
		frame.addMouseListener(mouseListen);
		frame.addMouseMotionListener(mouseMotion);
		pane = frame.getContentPane();
		startGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				player.changeEdit();
			}
		});
		pane.add(randomizeShips, BorderLayout.NORTH);
		pane.add(startGame, BorderLayout.SOUTH);
		if (!showBtn) {
			//System.out.println("Showing button");
			pane.remove(randomizeShips);
			pane.remove(startGame);
			pane.add(new Button(), BorderLayout.NORTH);
			
		}
		
		randomizeShips.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				randomizeShips();
			}
		});
		startGame.setLocation(width / 2, height + 150);
		
		frame.setVisible(true);
		
		this.width = width;
		this.height = height;
		this.squaresXsize = width / squaresX;
		this.squaresYsize = height / squaresY;
		this.player = player;
		misses = new boolean[width][height];
	}

	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height, Ship[] initShips, boolean showBtn) {
		this(player, x, y, squaresX, squaresY, width, height, showBtn);
		createShips(initShips);
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.GRAY);
		for (int x = 0; x + squaresXsize < width; x += squaresXsize)
			for (int y = 0; y + squaresYsize < height; y += squaresYsize) {
				g.drawRect(x, y, squaresXsize, squaresYsize);
			}
		for (Ship ship : ships){
			ship.paintComponent(g);
		}
		for (int i = 0; i < misses.length; i++) {
			for (int ii = 0; ii < misses[i].length; ii++) {
				if (misses[i][ii]) {
					g.setColor(Color.CYAN);
					g.fillOval(i,ii,squaresXsize, squaresYsize);
				}
			}
		}
	}

	protected void createShip(int x, int y, int xSize, int ySize) {
		Ship newShip = new Ship(this, xSize, ySize, x, y);
		ships.add(newShip);
		frame.add(newShip);
		repaint();
		floatingShips++;
	}
	
	protected void createShip(Ship ship) {
		Ship newShip = ship.copy(this);
		ships.add(newShip);
		frame.add(newShip);
		repaint();
		floatingShips++;
	}

	protected void createShips(Ship[] shipsArr) {
		for (Ship ship : shipsArr) {
			Ship addShip = ship.copy(this);
			ships.add(addShip);
			frame.add(addShip);
			repaint();
			floatingShips += shipsArr.length;
		}
	}

	public void closeJframe() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	public void setFocus() {
		frame.toFront();
	}

	public ArrayList<Ship> getShips() {
		return ships;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public int getSquaresXsize() {
		return squaresXsize;
	}

	public int getSquaresYsize() {
		return squaresYsize;
	}

	private boolean isInBoardBounds(int x, int y) {
		return x>=0&&y>=0&&x<getWidth()&&y<getHeight();
	}
	
	private int toBoardDimensions(int i, int dimension){
		return (int)Math.floor(i/dimension)*dimension;
	}
	
	public Point getLocationRelativeTo() {
	    int x = frame.getX() - MouseInfo.getPointerInfo().getLocation().x;
	    int y = frame.getY() - MouseInfo.getPointerInfo().getLocation().y;
	    return new Point(-x-15, -y-80);
	}
	
	private MouseAdapter mouseListen = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			//int mX = e.getX()-
			boolean hit = false;
			for (Ship ship : ships) {
				if (ship.mouseInBounds(getLocationRelativeTo())) {
					if (ship.isMovable()&&Main.localPlayer == player) {
						System.out.println("Rotating");
						ship.rotate();
						repaint();
					} else if (Main.getCurrentPlayer() == player) {
						// do nothing- current turn player clicking on self
					} else if(Main.gameStart){
						hit = true;
						if(ship.attack(getLocationRelativeTo())){
							System.out.println("Hit!");
							if(ship.checkDestroyed()) floatingShips--;
							if(floatingShips <= 0) player.defeat();
							repaint();
							player.setTurn(false);
							Main.nextTurn();
						}
					}
				}
			}
			if(!hit&&Main.gameStart&&!player.isCurrentTurn())
				if(isInBoardBounds((int)getLocationRelativeTo().getX(),(int)getLocationRelativeTo().getY())){
					int mX = toBoardDimensions((int)getLocationRelativeTo().getX(), squaresXsize), mY = toBoardDimensions((int)getLocationRelativeTo().getY(), squaresYsize);
						if(!misses[mX][mY]){
							misses[mX][mY] = true;
							System.out.println("Miss!");
							repaint();
							player.setTurn(false);
							Main.nextTurn();
					}
				}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			for (Ship ship : ships) {
				if (ship.isMovable()) {
					System.out.println("Stop moving");
					ship.stopMoving();
					movingShip = null;
					repaint();
				}
			}
		}
	};

	private MouseMotionAdapter mouseMotion = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			System.out.println("Dragging detected");
			System.out.println(getLocationRelativeTo());
			if(Main.localPlayer == player)
			for (Ship ship : ships) {
				if (movingShip == null) {
					if (ship.mouseInBounds(getLocationRelativeTo()))
						movingShip = ship;
				} else if (movingShip == ship)
					if (ship.isMovable()) {
						System.out.println("Start dragging ship " + ship);
						ship.drag(getLocationRelativeTo());
						repaint();
					}

			}

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			//System.out.println("" + e.getX() + " , " + e.getY());
		}
	};

	public void removeStart() {
		try{
			pane.remove(startGame);
			pane.remove(randomizeShips);
			repaint();
			System.out.println("Removed btn");
		}
		catch (Exception e){
		}
	}
	
	public void randomizeShips(){
		for(Ship ship : ships){
			while(!ship.positionVal(toBoardDimensions((int)(Math.random()*1000),getSquaresXsize()), toBoardDimensions((int)(Math.random()*1000),getSquaresYsize()), Math.random()<.5?true:false)){}
			repaint();
		}
	}
}
