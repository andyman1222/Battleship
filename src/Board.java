
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

enum Directions {
	UP, DOWN, LEFT, RIGHT;
}

public class Board extends JPanel {
	private JFrame frame;
	private ArrayList<Ship> ships = new ArrayList<Ship>();
	private int squaresXsize, squaresYsize, width, height, squaresX, squaresY;
	private Player player;
	private Ship movingShip;
	private boolean[][] misses;
	private boolean[][] hits;
	private Container pane;
	private Button startGame = new Button("Start game");
	private Button randomizeShips = new Button("Randomize ships");
	private int floatingShips;

	/**
	 * create a new, preinitialized board
	 * 
	 * @param player
	 *            player board belongs to
	 * @param x
	 *            x position on screen to display board
	 * @param y
	 *            y position on screen to display board
	 * @param squaresX
	 *            number of squares along X axis
	 * @param squaresY
	 *            number of squares along Y axis
	 * @param width
	 *            width of window in pixels
	 * @param height
	 *            height of window + 160 pixels
	 * @param showBtn
	 *            whether or not to show the start game btn on the bottom of the
	 *            screen. Typically true for human players.
	 */
	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height, boolean showBtn) {
		this.width = width;
		this.height = height;
		this.squaresXsize = (int) ((width) / (squaresX + .5));
		this.squaresYsize = (int) ((height) / (squaresY + .5));
		this.player = player;
		misses = new boolean[squaresX][squaresY];
		hits = new boolean[squaresX][squaresY];
		this.squaresX = squaresX;
		this.squaresY = squaresY;

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
		frame.setBounds(x, y, width, height);
		frame.addMouseListener(mouseListen);
		frame.addMouseMotionListener(mouseMotion);
		pane = frame.getContentPane();
		startGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				player.changeEdit();
			}
		});
		this.setPreferredSize(new Dimension(width, height + 100));
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		pane.add(this);
		randomizeShips.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				randomizeShips();
			}
		});
		frame.pack();
		frame.setVisible(true);
		repaint();
	}

	/**
	 * create a new, preinitialized board
	 * 
	 * @param player
	 *            player board belongs to
	 * @param x
	 *            x position on screen to display board
	 * @param y
	 *            y position on screen to display board
	 * @param squaresX
	 *            number of squares along X axis
	 * @param squaresY
	 *            number of squares along Y axis
	 * @param width
	 *            width of window
	 * @param height
	 *            height of window + 250
	 * @param initShips
	 *            initialize the board with specified ships, copied from the
	 *            array
	 * @param showBtn
	 *            whether or not to show the start game btn on the bottom of the
	 *            screen. Typically true for human players.
	 */
	public Board(Player player, int x, int y, int squaresX, int squaresY, int width, int height, Ship[] initShips,
			boolean showBtn) {
		this(player, x, y, squaresX, squaresY, width, height, showBtn);
		createShips(initShips);
	}

	@Override

	/**
	 * draws stuff on the screen
	 */
	public void paint(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height + 100);
		g.setColor(Color.GRAY);
		for (int x = 0; x + squaresXsize < width; x += squaresXsize)
			for (int y = 0; y + squaresYsize < height; y += squaresYsize) {
				g.drawRect(x, y, squaresXsize, squaresYsize);
			}
		for (Ship ship : ships) {
			ship.paintComponent(g);
		}
		for (int i = 0; i < misses.length; i++) {
			for (int ii = 0; ii < misses[i].length; ii++) {
				if (misses[i][ii]) {
					g.setColor(Color.CYAN);
					g.fillOval(i * squaresXsize + (squaresXsize / 16), ii * squaresYsize + (squaresYsize / 16),
							squaresXsize - (squaresXsize / 8), squaresYsize - (squaresYsize / 8));
				}
			}
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("MONOSPACED", Font.PLAIN, 16));
		g.drawString("Ships left: " + floatingShips, width / 3, height);
		g.drawString("Points: " + player.getPoints(), width * 2 / 3, height);
		g.setFont(new Font("MONOSPACED", Font.PLAIN, 50));
		if (player.checkDefeat())
			g.drawString("Player defeated", width / 2, height / 2);
	}

	/**
	 * create one new ship on board
	 * 
	 * @param x
	 *            x location of initial ship
	 * @param y
	 *            y location of initial ship
	 * @param xSize
	 *            width of ship
	 * @param ySize
	 *            height of ship
	 */
	protected void createShip(int x, int y, int xSize, int ySize) {
		Ship newShip = new Ship(this, xSize, ySize, x, y);
		ships.add(newShip);
		frame.add(newShip);
		repaint();
		floatingShips++;
	}

	/**
	 * create one new ship from a predefined ship
	 * 
	 * @param ship
	 *            ship to copy to board
	 */
	protected void createShip(Ship ship) {
		Ship newShip = ship.copy(this);
		ships.add(newShip);
		frame.add(newShip);
		repaint();
		floatingShips++;
	}

	/**
	 * create new ships on the board from the array
	 * 
	 * @param shipsArr
	 *            array of ships to add
	 */
	protected void createShips(Ship[] shipsArr) {
		for (Ship ship : shipsArr) {
			Ship addShip = ship.copy(this);
			ships.add(addShip);
			frame.add(addShip);
			repaint();
			floatingShips++;
		}
	}

	/**
	 * closes window
	 */
	public void closeJframe() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * changes focus
	 */
	public void setFocus() {
		frame.toFront();
	}

	/**
	 * 
	 * @return all ships on board
	 */
	public ArrayList<Ship> getShips() {
		return ships;
	}

	/**
	 * 
	 * @return player the board belongs to
	 */
	public Player getPlayer() {
		return player;
	}

	@Override
	/**
	 * @return width of board
	 */
	public int getWidth() {
		return width;
	}

	@Override
	/**
	 * @return height of board
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return width of 1 square in pixels
	 */
	public int getSquaresXsize() {
		return squaresXsize;
	}

	/**
	 * 
	 * @return height of 1 square in pixels
	 */
	public int getSquaresYsize() {
		return squaresYsize;
	}

	@Override
	/**
	 * @return number of squares along x dimension
	 */
	public int getX() {
		return squaresX;
	}

	@Override
	/**
	 * @return number of squares along y dimension
	 */
	public int getY() {
		return squaresY;
	}

	private boolean isInBoardBounds(int x, int y) {
		return x >= 0 && y >= 0 && x < squaresX && y < squaresY;
	}

	private boolean isInBoardBoundsAttack(int x, int y) {
		System.out.println("" + x + ", " + y);
		return x >= 0 && y >= 0 && x < squaresX && y < squaresY;
	}

	public int round(int i, int dimension) {
		return (int) Math.round(i / dimension) * dimension;
	}

	/**
	 * return the given coordinate relative to the JFrame converted to the
	 * coorindate relative to teh board
	 * 
	 * @param i
	 *            coordinate to convert
	 * @param dimension
	 *            which dimension to convert to (usually squaresXsize or
	 *            squaresYsize)
	 * @return the converted dimension
	 */
	public int toBoardDimensions(int i, int dimension) {
		return i / dimension;
	}

	public int toFrameDimensions(int i, int dimension) {
		return i * dimension;
	}

	private Point getLocationRelativeToJFrame() {
		int x = frame.getX() - MouseInfo.getPointerInfo().getLocation().x;
		int y = frame.getY() - MouseInfo.getPointerInfo().getLocation().y;
		return new Point(-x - 15, -y - 60);
	}

	private Point getLocationRelativeToBoard() {
		int x = frame.getX() - MouseInfo.getPointerInfo().getLocation().x;
		int y = frame.getY() - MouseInfo.getPointerInfo().getLocation().y;
		return new Point(toBoardDimensions(-x - 15, squaresXsize), toBoardDimensions(-y - 60, squaresYsize));
	}

	private MouseAdapter mouseListen = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {

			if (Main.gameStart && !Main.currentPlayer.isComputer()) {
				switch (checkHit(getLocationRelativeToBoard(), false)) {
				case HIT:
				case HIT_AND_DESTROYED:
				case MISS:
					Main.nextTurn();
				}

			} else if (isInBoardBounds((int) getLocationRelativeToBoard().getX(),
					(int) getLocationRelativeToBoard().getY())) {
				for (Ship ship : ships) {
					if (ship.isInBounds(getLocationRelativeToBoard())) {
						if (ship.isMovable() && Main.localPlayer == player) {
							System.out.println("Rotating");
							ship.rotate();
							repaint();
						}
					}
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

	// Dragging handler
	private MouseMotionAdapter mouseMotion = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
			System.out.println("Dragging detected");
			System.out.println(getLocationRelativeToJFrame() + "\n" + getLocationRelativeToBoard());
			if (Main.localPlayer == player)
				for (Ship ship : ships) {
					if (movingShip == null) {
						if (ship.isInBounds(getLocationRelativeToBoard()))
							movingShip = ship;
					} else if (movingShip == ship)
						if (ship.isMovable()) {
							System.out.println("Start dragging ship " + ship);
							ship.drag(getLocationRelativeToBoard());
							repaint();
						}

				}

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// System.out.println("" + e.getX() + " , " + e.getY());
		}
	};

	private boolean prevHit = false;

	/**
	 *
	 * @return whether or not the previous shot was a hit.
	 */
	public boolean getPrevHit() {
		return prevHit;
	}

	private boolean sankShip = false;

	/**
	 * check if point e hit something
	 * 
	 * @param e
	 *            Point to check on board if it hit something, relative to the
	 *            board
	 * @return true if it was a legal move, false otherwise (like clicking on
	 *         the same thing twice). Use getPrevHit() to determine whether or
	 *         not it hit a ship.
	 */

	public AttackStates checkHit(Point e, boolean computer) {
		prevHit = false;
		sankShip = false;
		boolean miss = true;
		AttackStates prevState;
		if (Main.getCurrentPlayer() == player) {
			System.out.println("Trying to commit treason...");
			return AttackStates.WRONG_TARGET;
		} else if (player.checkDefeat()) {
			System.out.println("wrong target...");
			return AttackStates.WRONG_TARGET;
		} else if (!isInBoardBoundsAttack((int) e.getX(), (int) e.getY())) {
			System.out.println("attack out of bounds...");
			return AttackStates.OUT_OF_BOUNDS;
		} else if (!Main.gameStart) {
			System.out.println("Cannot pre-attack...");
			return AttackStates.ERROR;
		} else if (misses[(int) e.getX()][(int) e.getY()]) {
			System.out.println("Trying to miss again!");
			return AttackStates.ALREADY_MISS;
		} else {
			AttackStates globalState = AttackStates.MISS;
			for (Ship ship : ships) {
				AttackStates state = ship.attack(e);
				switch (state) {
				case HIT_AND_DESTROYED:
					miss = false;
					hits[(int) e.getX()][(int) e.getY()] = true;
					Main.currentPlayer.addPoints(10);
					repaint();
					floatingShips--;
					Main.currentPlayer
							.addPoints((int) (ship.getBounds().getWidth() * ship.getBounds().getHeight() * 10));
					System.out.println("Hit and destroyed! Remanining ships: " + floatingShips);
					if (floatingShips <= 0)
						player.defeat();
					globalState = state;
					break;
				case HIT:
					miss = false;
					System.out.println("Hit!");
					hits[(int) e.getX()][(int) e.getY()] = true;
					Main.currentPlayer.addPoints(10);
					repaint();
					prevHit = true;
					globalState = state;
					break;
				case ALREADY_HIT_AND_DESTROYED:
					miss = false;
					System.out.println("Already destroyed here!");
					globalState = state;
					break;
				case ALREADY_HIT:
					miss = false;
					System.out.println("Already hit here!");
					globalState = state;
					break;
				case MISS:
					break;
				default:
					System.out.println("This shouldn't be printing..." + state);
				}
			}
			if(miss){
				System.out.println("Miss!");
				misses[(int) e.getX()][(int) e.getY()] = true;
				repaint();
			}
			return globalState;
		}
	}

	/**
	 * hides start button
	 */
	public void removeStart() {
		try {
			pane.remove(startGame);
			pane.remove(randomizeShips);
			repaint();
			System.out.println("Removed btn");
		} catch (Exception e) {
		}
	}
	
	/**
	 * shows start button
	 */
	public void addStart() {
		try {
			pane.add(randomizeShips);
			pane.add(startGame);
			repaint();
			frame.pack();
			frame.setVisible(true);
			System.out.println("added btn");
		} catch (Exception e) {
		}
	}

	/**
	 * randomly places the ships
	 */
	public void randomizeShips() {
		for (Ship ship : ships) {
			System.out.println("Randomizing...");
			while (!ship.positionVal((int) (Math.random() * squaresX), (int) (Math.random() * squaresY),
					Math.random() < .5 ? true : false)) {
			}
			repaint();
		}
	}

	public boolean[][] getHits() {
		return hits;
	}

	public boolean[][] getMisses() {
		return misses;
	}
}
