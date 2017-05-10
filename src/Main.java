import javax.swing.JOptionPane;

//main will handle the turns (and rules?) of the game
public class Main {
	
	public static final boolean dev = false;
	private static Player[] players = {
			new Player("Player 1",0,0, 25, 25, 750, 750, true),
			new CPUplayer("CPU player",750,0, 25, 25, 750, 750)
	};
	
	//private int i = (int) (Math.round(Math.random())*players.length);
	private static int i = -1;
	protected static Player currentPlayer, localPlayer = players[0];
	private static Ship[] startingShips = {
			new Ship(0,0,1,5),
			new Ship(1,0,1,4),
			new Ship(2,0,1,4),
			new Ship(3,0,1,3),
			new Ship(4,0,1,3),
			new Ship(5,0,1,3),
			new Ship(6,0,1,3),
			new Ship(7,0,1,2),
			new Ship(8,0,1,2),
			new Ship(9,0,1,2),
			new Ship(10,0,1,2)
	};
	
	protected static boolean gameStart = false;
	public static void main(String[] args){
		
		//add ships to all players
		for(Player player : players)
			player.addShips(startingShips);
		boolean start = false;
		while(!start){
			//System.out.println("Running...");
			start = true;
			for(Player player : players){
				player.getBoard().repaint();
				if(!player.isInPlay()){
					start = false;
				}
			}
		}
		gameStart = true;
		for(Player player : players){
			player.getBoard().removeStart();
		}
		currentPlayer = players[(int)(Math.random()*2)];
		nextTurn();
	}
	
	/**
	 * gets player whose turn it is
	 * @return player whose turn it is
	 */
	public static Player getCurrentPlayer(){
		return currentPlayer;
	}
	
	/**
	 * 
	 * @return list of all players
	 */
	public static Player[] getPlayers(){
		return players;
	}
	
	/**
	 * remove a player when they lose.
	 * @param player player to remove from game
	 */
	public static void destroy(Player player){
		for(Player p : players){
			if(p.equals(player)){
				p.closeJframe();
				p = null;
			}
			}
		}
	
	/**
	 * exits
	 */
	public static void closeAllWindows(){
		System.exit(0);
	}

	/**
	 * moves all windows to the front when one window is clicked
	 */
	public static void changeFocus() {
		System.out.println("Moving windows");
		for(Player player : players)
		player.getBoard().setFocus();
	}
	
	/**
	 * advances game 1 turn or checks if there is 1 player left
	 */
	public static void nextTurn(){
		currentPlayer.setTurn(false);
		if(players.length<2){
			JOptionPane.showMessageDialog(null, "" + currentPlayer + " has won!");
			System.exit(0);
		}
		i=(i+1)%players.length;
		currentPlayer = players[i];
		System.out.println("Switching turns- " + currentPlayer);
		if(currentPlayer == null) nextTurn();
		else{
			currentPlayer.setTurn(true);
			if(currentPlayer instanceof CPUplayer){
				CPUplayer currentCPUPlayer = (CPUplayer) currentPlayer;
				currentCPUPlayer.attack();
				Main.nextTurn();
			}
		}
	}
}
