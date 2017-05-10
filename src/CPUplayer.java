import java.awt.Point;

public class CPUplayer extends Player {

	private int prevHits = 0;
	private boolean[][] alreadyCheckedIndexes;

	/**
	 * create new CPU player
	 * @param name name of player
	 * @param x x position on screen of JFrame
	 * @param y y position on screen of JFrame
	 * @param boardX width in terms of squares of board
	 * @param boardY height in terms of squares of board
	 * @param width width of board
	 * @param height height of board
	 */
	public CPUplayer(String name, int x, int y, int boardX, int boardY, int width, int height) {
		super(name, x, y, boardX, boardY, width, height, false);
		getBoard().randomizeShips();
		alreadyCheckedIndexes = new boolean[getBoard().getX()][getBoard().getY()];
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

	private int hitX, hitY, backupX, backupY, nextX, nextY, step = 1, randomPlayer, startingPoint = 1;
	private Board targetBoard;

	/**
	 * Method called when it is the CPU player's turn. Implements some AI
	 */
	public void attack() {
		if (prevHits == 0) {

			// loops thru each player, checking if it already hit some unsunken
			// boats, then tries to sink them.
			for (Player targetPlayer : Main.getPlayers())
				if (targetPlayer != this) {
					//System.out.println("TargetPlayer != this");
					targetBoard = targetPlayer.getBoard();
					for (int i = 0; i < alreadyCheckedIndexes.length; i++)
						for (int x = 0; x < alreadyCheckedIndexes[i].length; x++) {
							if (alreadyCheckedIndexes[i][x]) {
								System.out.println("Found something...");
								for (Ship ship : targetBoard.getShips())
									if (ship.isInBounds(new Point(i, x)) && !ship.checkDestroyed()) {
										System.out.println("Found a hit and unsunken ship!");
										hitX = i;
										hitY = x;
										prevHits++;
										step = 1;
										backupX = hitX;
										backupY = hitY;
										nextX = hitX;
										nextY = hitY;
										attack();
										return;
									}
							}
							else System.out.println(alreadyCheckedIndexes.length);
						}
				}

			// if the above is false, play guess-and-check
			do
				randomPlayer = (int) (Math.random() * Main.getPlayers().length);
			while (Main.getPlayers()[randomPlayer] == this);
			targetBoard = Main.getPlayers()[randomPlayer].getBoard();
			hitX = (int) (Math.random() * targetBoard.getX());
			hitY = (int) (Math.random() * targetBoard.getY());
			while (!targetBoard.checkHit(new Point(hitX, hitY))) {
				hitX = (int) (Math.random() * targetBoard.getX());
				hitY = (int) (Math.random() * targetBoard.getY());
			}
			alreadyCheckedIndexes[hitX][hitY] = getBoard().getPrevHit();
			if (targetBoard.getPrevHit()) {
				System.out.println("Attempting to find ship...");
				prevHits++;
				step = 1;
				backupX = hitX;
				backupY = hitY;
				nextX = hitX;
				nextY = hitY;
			}
		}

		// if in its previous turn it hit a ship once, attempt to locate the
		// direction of the ship
		else if (prevHits == 1) {
			nextX = backupX;
			nextY = backupY;
			switch (step) {
			case 1:
				nextX ++;
				// backupX -= getBoard().getSquaresXsize();
				break;
			case 2:
				nextX --;
				// backupX += getBoard().getSquaresXsize();
				break;
			case 3:
				nextY ++;
				// backupY -= getBoard().getSquaresYsize();
				break;
			case 4:
				nextY --;
				// backupY += getBoard().getSquaresYsize();
				break;
			default:
				prevHits = 0;
			}
			if (!targetBoard.checkHit(new Point(nextX, nextY))) {
				prevHits = 1;
				step++;
				step %= 4 + 1;
				if (step == startingPoint) {
					prevHits = 0;
				}
				System.out.println("restarting sequence...");
				attack();
			} else if (targetBoard.getPrevHit()) {
				alreadyCheckedIndexes[nextX][nextY] = targetBoard.getPrevHit();
				prevHits++;
				if (targetBoard.sankShip())
					prevHits = 0;
			} else {
				step++;
				step %= 4 + 1;
				if (step == startingPoint) {
					prevHits = 0;
				}
			}
		}

		// once direction is established, attack the ship in that direction, or
		// restart if missed and unsunken
		else {
			switch (step) {
			case 1:
				nextX ++;
				break;
			case 2:
				nextX --;
				break;
			case 3:
				nextY ++;
				break;
			case 4:
				nextY --;
				break;
			default:
				prevHits = 0;
			}
			if (!targetBoard.checkHit(new Point(nextX, nextY))) {
				prevHits = 1;
				System.out.println("restarting sequence...");
				attack();
			} else if (targetBoard.getPrevHit()) {
				alreadyCheckedIndexes[nextX][nextY] = targetBoard.getPrevHit();
				prevHits++;
				if (targetBoard.sankShip())
					prevHits = 0;
			} else {
				switch (step) {
				case 1:
					step = 2;
					break;
				case 2:
					step = 1;
					break;
				case 3:
					step = 4;
					break;
				case 4:
					step = 3;
					break;
				default:
					prevHits = 0;
				}
				prevHits = 1;
			}
		}
	}
}
