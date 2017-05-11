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
	private int[] alreadyChosenStep = new int[4];
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
				for(int x = 0; x < alreadyChosenStep.length; x++)
					alreadyChosenStep[x] = 0;
				step = (int) ((Math.random()*4));
				alreadyChosenStep[step] = 1;
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
			case 0:
				nextX ++;
				// backupX -= getBoard().getSquaresXsize();
				break;
			case 1:
				nextX --;
				// backupX += getBoard().getSquaresXsize();
				break;
			case 2:
				nextY ++;
				// backupY -= getBoard().getSquaresYsize();
				break;
			case 3:
				nextY --;
				// backupY += getBoard().getSquaresYsize();
				break;
			default:
				prevHits = 0;
			}
			//illegal move
			if (!targetBoard.checkHit(new Point(nextX, nextY))) {
				prevHits = 1;
				boolean seq = false;
				for(int x : alreadyChosenStep)
					if(x == 0)
						seq = true;
				if(seq){
					System.out.println("Choosing another move...");
					while(alreadyChosenStep[step]!=1)
						step = (int) ((Math.random()*4));
					alreadyChosenStep[step] = 1;
				}
				else prevHits = 0;
				System.out.println("Restarting attack");
				attack();
				//ship hit
			} else if (targetBoard.getPrevHit()) {
				alreadyCheckedIndexes[nextX][nextY] = targetBoard.getPrevHit();
				prevHits++;
				if (targetBoard.sankShip())
					prevHits = 0;
				//ship miss
			} else {
				boolean seq = false;
				for(int x : alreadyChosenStep)
					if(x == 0)
						seq = true;
				if(seq){
					while(alreadyChosenStep[step]!=1)
						step = (int) ((Math.random()*4));
					alreadyChosenStep[step] = 1;
				}
				else prevHits = 0;
			}
		}

		// once direction is established, attack the ship in that direction, or
		// restart if missed and unsunken
		else {
			switch (step) {
			case 0:
				nextX ++;
				break;
			case 1:
				nextX --;
				break;
			case 2:
				nextY ++;
				break;
			case 3:
				nextY --;
				break;
			default:
				prevHits = 0;
			}
			//illegal move
			if (!targetBoard.checkHit(new Point(nextX, nextY))) {
				prevHits = 1;
				switch (step) {
				case 0:
					step = 1;
					break;
				case 1:
					step = 0;
					break;
				case 2:
					step = 3;
					break;
				case 3:
					step = 2;
					break;
				default:
					prevHits = 0;
				}
				attack();
				//ship hit
			} else if (targetBoard.getPrevHit()) {
				alreadyCheckedIndexes[nextX][nextY] = targetBoard.getPrevHit();
				prevHits++;
				if (targetBoard.sankShip())
					prevHits = 0;
				//ship miss
			} else {
				switch (step) {
				case 0:
					step = 1;
					break;
				case 1:
					step = 0;
					break;
				case 2:
					step = 3;
					break;
				case 3:
					step = 2;
					break;
				default:
					prevHits = 0;
				}
				prevHits = 1;
			}
		}
	}
}
