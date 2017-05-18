import java.awt.Point;
import java.util.EnumMap;

public class CPUplayer extends Player {

	protected Point prevHitLocation = null;
	public Directions direction = Directions.UP;
	public boolean alreadyFlipped = false, prevHit = false;
	private Ship prevShipHit;

	// public- trust me these must be edited by other classes
	private Point hit, backup;
	public int prevHits = 0;
	public int[] alreadyChosendirection = new int[4];

	private EnumMap<Directions, Integer> dirInts = new EnumMap<Directions, Integer>(Directions.class);

	/**
	 * create new CPU player
	 * 
	 * @param name
	 *            name of player
	 * @param x
	 *            x position on screen of JFrame
	 * @param y
	 *            y position on screen of JFrame
	 * @param boardX
	 *            width in terms of squares of board
	 * @param boardY
	 *            height in terms of squares of board
	 * @param width
	 *            width of board
	 * @param height
	 *            height of board
	 */
	public CPUplayer(String name, int x, int y, int boardX, int boardY, int width, int height) {
		super(name, x, y, boardX, boardY, width, height, false);
		dirInts.put(Directions.UP, 0);
		dirInts.put(Directions.DOWN, 1);
		dirInts.put(Directions.LEFT, 2);
		dirInts.put(Directions.RIGHT, 3);
		getBoard().randomizeShips();
	}

	@Override
	public void addShips(Ship[] ships) {
		changeEdit(true);
		super.addShips(ships);
		getBoard().randomizeShips();
		changeEdit();
	}

	@Override
	public void addShip(int x, int y, int xSize, int ySize) {
		changeEdit(true);
		super.addShip(x, y, xSize, ySize);
		getBoard().randomizeShips();
		changeEdit();
	}

	@Override
	public void addShip(Ship ship) {
		changeEdit(true);
		super.addShip(ship);
		getBoard().randomizeShips();
		changeEdit();
	}

	public boolean isComputer() {
		return true;
	}

	private int randomPlayer;
	private Board targetBoard;
	private Ship targetShip;

	/**
	 * Method called when it is the CPU player's turn. Implements some AI
	 */
	private void findTarget() {
		if (targetBoard == null || prevHits == 0) {

			// loops thru each player, checking if it already hit some unsunken
			// boats, then tries to sink them.
			for (Player targetPlayer : Main.getPlayers())
				if (targetPlayer != this) {
					targetBoard = targetPlayer.getBoard();
					for (int i = 0; i < targetBoard.getHits().length; i++)
						for (int x = 0; x < targetBoard.getHits()[i].length; x++) {
							if (targetBoard.getHits()[i][x]) {
								System.out.println("Found something...");
								for (Ship ship : targetBoard.getShips())
									if (ship.isInBounds(new Point(i, x)) && !ship.checkDestroyed()) {
										targetShip = ship;
										System.out.println("Found an unsunken ship!");
										prevHits++;
										hit = new Point(i, x);
										backup = (Point) hit.clone();
										checkForNeighborAttack();
										attack(false);
										return;
									}
							}
						}
				}

			// if the above is false, play guess-and-check
			do
				randomPlayer = (int) (Math.random() * Main.getPlayers().length);
			while (Main.getPlayers()[randomPlayer] == this && !Main.getPlayers()[randomPlayer].checkDefeat());
			targetBoard = Main.getPlayers()[randomPlayer].getBoard();
			boolean Continue = false;
			hit = new Point((int) (Math.random() * targetBoard.getX()), (int) (Math.random() * targetBoard.getY()));
			while (!Continue) {
				AttackStates stat = checkHit();
				switch (stat) {
				case ALREADY_HIT:
				case ALREADY_HIT_AND_DESTROYED:
				case ALREADY_MISS:
				case OUT_OF_BOUNDS:
				case ERROR:
					hit = new Point((int) (Math.random() * targetBoard.getX()),
							(int) (Math.random() * targetBoard.getY()));
					break;
				case HIT:
					backup = (Point) hit.clone();
					System.out.println("Found ship... Now attempting to sink ship...");
					prevHits++;
				case HIT_AND_DESTROYED:
				case MISS:
					Continue = true;
					break;
				case WRONG_TARGET:
					do
						randomPlayer = (int) (Math.random() * Main.getPlayers().length);
					while (Main.getPlayers()[randomPlayer] == this && !Main.getPlayers()[randomPlayer].checkDefeat());
					break;
				default:
					System.out.println("Case error. checkHit() = " + stat);

				}
			}
		}

		// if in its previous turn it hit a ship once, attempt to locate the
		// direction of the ship
		else if (prevHits == 1) {
			attack(true);
		}

		// once direction is established, attack the ship in that direction, or
		// restart if missed and unsunken
		else {
			attack(false);
		}
	}

	private boolean alreadyLegalMove = false;
	
	/**
	 * starts CPU's turn
	 */
	public void setTurn(){
		alreadyLegalMove = false;
		findTarget();
	}
	
	/**
	 * code called to try to hit a target and what to do after a result
	 * 
	 * @param firstAttack
	 *            whether or not this is the first attack on a ship (whether or
	 *            not to find direction
	 */
	private void attack(boolean firstAttack) {
		if(!alreadyLegalMove){
			if (firstAttack) {
				checkForNeighborAttack();
	
			}
			increaseStep();
			switch (checkHit()) {
			case OUT_OF_BOUNDS:
				flip();
				attack(firstAttack);
			case ALREADY_HIT:
				if (!alreadyFlipped) {
					attack(false);
					break;
				}
				findTarget();
				break;
			case ALREADY_HIT_AND_DESTROYED:
				prevHits = 0;
				findTarget();
				break;
			case ALREADY_MISS:
				prevHits = 1;
				hit = (Point) backup.clone();
				if (firstAttack) {
					System.out.println("Finding another legal move...");
					// if(targetBoard.prevHitLocation == null){
					// }
					attack(false);
				} else if (alreadyFlipped) {
					prevHits = 1;
					alreadyFlipped = false;
					System.out.println("Switching directions...");
					switch (direction) {
					case UP:
					case DOWN:
						direction = (int) (Math.random() * 2) == 1 ? Directions.LEFT : Directions.RIGHT;
						break;
					case LEFT:
					case RIGHT:
						direction = (int) (Math.random() * 2) == 1 ? Directions.DOWN : Directions.UP;
						break;
					}
					attack(true);
				} else {
					flip();
					attack(false);
				}
				break;
			// ship hit
			case HIT:
				alreadyLegalMove = true;
				prevHits++;
				backup = (Point) hit.clone();
				alreadyTriedDirections = new Directions[4];
			case HIT_AND_DESTROYED:
				prevHits = 0;
				break;
			// ship miss
			case MISS:
				alreadyLegalMove = true;
				prevHits = 1;
				alreadyTriedDirections = new Directions[4];
				if (alreadyFlipped || firstAttack) {
					hit = (Point) backup.clone();
					getRandomAttack();
				} else {
					flip();
				}
				break;
			case WRONG_TARGET:
				do
					randomPlayer = (int) (Math.random() * Main.getPlayers().length);
				while (Main.getPlayers()[randomPlayer] == this && !Main.getPlayers()[randomPlayer].checkDefeat());
			default:
				getRandomAttack();
				findTarget();
			}
		}
	}

	public AttackStates checkHit() {
		return targetBoard.checkHit(hit, true);
	}

	/**
	 * In Battleship, if a player realizes they reached the end of a ship, they
	 * will try to attack from the other side. This does such for the CPU.
	 */
	public void flip() {
		alreadyFlipped = true;
		System.out.println("Flipping...");
		switch (direction) {
		case UP:
			direction = Directions.DOWN;
			break;
		case DOWN:
			direction = Directions.UP;
			break;
		case LEFT:
			direction = Directions.RIGHT;
			break;
		case RIGHT:
			direction = Directions.LEFT;
			break;
		default:
			prevHits = 0;
		}
		hit = (Point) backup.clone();
	}

	private void getRandomAttack() {
		backup = (Point) hit.clone();
		alreadyFlipped = false;
		direction = setRandomAttack();
	}

	private Directions setRandomAttack() {
		switch ((int) (Math.random() * 4)) {
		case 0:
			return Directions.UP;
		case 1:
			return Directions.DOWN;
		case 2:
			return Directions.LEFT;
		case 3:
			return Directions.RIGHT;
		default:
			return Directions.RIGHT;
		}
	}

	public int getPrevHits() {
		return prevHits;
	}

	public Directions getDirections() {
		return direction;
	}

	private void predictNextMove() {
		getRandomAttack();
		increaseStep();
	}

	private void increaseStep() {
		switch (direction) {
		case RIGHT:
			hit.x++;
			break;
		case LEFT:
			hit.x--;
			break;
		case DOWN:
			hit.y++;
			break;
		case UP:
			hit.y--;
			break;
		default:
			prevHits = 0;
		}
	}

	private Directions[] alreadyTriedDirections = new Directions[4];

	private void checkForNeighborAttack() {
		boolean goThru = false;
		for (Directions dir : alreadyTriedDirections) {
			if (dir == null)
				goThru = true;
		}
		int u = 0;
		if (goThru)
			for (Directions dir : dirInts.keySet()) {
				System.out.println(dir);
				boolean check = true;
				for (Directions dir2 : alreadyTriedDirections) {
					if (dir == dir2) {
						check = false;
					}
				}
				if (check)
					switch (dir) {
					case RIGHT:
						if (hit.x + 1 < targetBoard.getX() && targetBoard.getHits()[hit.x + 1][hit.y]) {
							direction = Directions.RIGHT;
							break;
						}
						break;
					case LEFT:
						if (hit.x - 1 >= 0 && targetBoard.getHits()[hit.x - 1][hit.y]) {
							direction = Directions.LEFT;
							break;
						}
						break;
					case DOWN:
						if (hit.y + 1 < targetBoard.getY() && targetBoard.getHits()[hit.x][hit.y + 1]) {
							direction = Directions.DOWN;
							break;
						}
						break;
					case UP:
						if (hit.y - 1 >= 0 && targetBoard.getHits()[hit.x][hit.y - 1]) {
							direction = Directions.UP;
							break;
						}
						break;
					default:
						getRandomAttack();
					}
				alreadyTriedDirections[u] = dir;
				u++;
			}
		else {
			getRandomAttack();
			System.out.println("Tried all possibilities, generating random attack...");
		}
	}
}
