import javax.swing.JOptionPane;

//main will handle the turns (and rules?) of the game
public class Main {

	//see all ships no matter what
	public static boolean dev = false;
	
	//true if you will be playing locally, to prevent other players from cheating
	public static boolean passNPlay = false;
	private static Player[] players = { new Player("Player 1", 0, 0, 15, 15, 750, 750, true),
			new CPUplayer("CPU player", 750, 0, 15, 15, 750, 750),
			new CPUplayer("CPU player 2", 1500, 0, 15, 15, 750, 750)};

	// private int i = (int) (Math.round(Math.random())*players.length);
	private static int i = -1;
	protected static Player currentPlayer, localPlayer = null;
	private static Ship[] startingShips = { new Ship(0, 0, 1, 5), new Ship(1, 0, 1, 4), new Ship(2, 0, 1, 4),
			new Ship(3, 0, 1, 3), new Ship(4, 0, 1, 3), new Ship(5, 0, 1, 3), new Ship(6, 0, 1, 3),
			new Ship(7, 0, 1, 2), new Ship(8, 0, 1, 2), new Ship(9, 0, 1, 2), new Ship(10, 0, 1, 2), new Ship(10, 0, 2, 5) };

	protected static boolean gameStart = false;

	public static void main(String[] args) {

		// add ships to all players
		for (Player player : players) {
			player.addShips(startingShips);
			player.getBoard().randomizeShips();
		}
		boolean start = false;
		while (!start) {
			// System.out.println("Running...");
			start = true;
			for (Player player : players) {
				player.getBoard().repaint();
				if (!player.isInPlay()) {
					start = false;
					if(!player.isComputer()){
						localPlayer = player;
						JOptionPane.showMessageDialog(null, "Click here to set up, " + player + ". Click \"start game\" to end your setup.");
						localPlayer = player;
						player.getBoard().addStart();
						player.getBoard().repaint();
						while(!player.isInPlay()){System.out.print("");}
						localPlayer = null;
						player.getBoard().repaint();
						player.getBoard().removeStart();
						JOptionPane.showMessageDialog(null, "Click here to end your setup, " + player);
					}
				}
				player.getBoard().repaint();
			}
		}
		
		gameStart = true;
		i = (int) (Math.random() * players.length);
		currentPlayer = players[i];
		for (Player player : players)
			player.getBoard().repaint();
		currentPlayer.setTurn(true);
		if (currentPlayer instanceof CPUplayer) {
			CPUplayer currentCPUPlayer = (CPUplayer) currentPlayer;
			currentCPUPlayer.setTurn();
			nextTurn();
		}
		else{
			JOptionPane.showMessageDialog(null, "Click here to start playing, " + currentPlayer);
			localPlayer = currentPlayer;
			for (Player player : players)
				player.getBoard().repaint();
		}
	}

	/**
	 * gets player whose turn it is
	 * 
	 * @return player whose turn it is
	 */
	public static Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * 
	 * @return list of all players
	 */
	public static Player[] getPlayers() {
		return players;
	}

	/**
	 * exits
	 */
	public static void closeAllWindows() {
		System.exit(0);
	}

	/**
	 * moves all windows to the front when one window is clicked
	 */
	public static void changeFocus() {
		System.out.println("Moving windows");
		for (Player player : players)
			player.getBoard().setFocus();
	}

	/**
	 * advances game 1 turn or checks if there is 1 player left
	 */
	public static void nextTurn() {
		if(passNPlay&&!currentPlayer.isComputer()){
			JOptionPane.showMessageDialog(null, "Click here to end your turn, " + currentPlayer);
			localPlayer = null;
		}
		for (Player player : players)
			player.getBoard().repaint();
		currentPlayer.setTurn(false);
		if (players.length < 2) {
			JOptionPane.showMessageDialog(null, "" + currentPlayer + " has won!");
			// while(true){
			dev = true;
			gameStart = false;
			for (Player player : players)
				player.getBoard().repaint();
			// }
		} else {
			i = (i + 1) % players.length;
			currentPlayer = players[i];
			System.out.println("Switching turns- " + currentPlayer);
			if (currentPlayer.checkDefeat())
				nextTurn();
			else {
				currentPlayer.setTurn(true);
				if (currentPlayer instanceof CPUplayer) {
					CPUplayer currentCPUPlayer = (CPUplayer) currentPlayer;
					currentCPUPlayer.setTurn();
					nextTurn();
				}
				else if(passNPlay){
					JOptionPane.showMessageDialog(null, "Click here to start your turn, " + currentPlayer);
					for (Player player : players)
						player.getBoard().repaint();
				}
				else{
					localPlayer = currentPlayer;
				}
			}
		}
	}
}
