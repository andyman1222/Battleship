import javax.swing.JOptionPane;

//main will handle the turns (and rules?) of the game
public class Main {
	
	public static final boolean dev = true;
	private static Player[] players = {
			new Player("Player 1",0,0, true),
			new CPUplayer("CPU player",1000,0)
	};
	
	//private int i = (int) (Math.round(Math.random())*players.length);
	private static int i = -2;
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
			start = true;
			for(Player player : players){
				if(!player.isInPlay()){
					start = false;
				}
			}
		}
		gameStart = true;
		for(Player player : players){
			player.getBoard().removeStart();
		}
		nextTurn();
	}
	
	public static Player getCurrentPlayer(){
		return currentPlayer;
	}
	
	public static Player[] getPlayers(){
		return players;
	}
	
	public static void destroy(Player player){
		for(Player p : players){
			if(p.equals(player)){
				p.closeJframe();
				p = null;
			}
			}
		}
	public static void closeAllWindows(){
		System.exit(0);
	}

	public static void changeFocus() {
		System.out.println("Moving windows");
		for(Player player : players)
		player.getBoard().setFocus();
	}
	
	public static void nextTurn(){
		if(players.length<2){
			JOptionPane.showMessageDialog(null, "" + currentPlayer + " has won!");
			System.exit(0);
		}
		i=(i+2)%players.length;
		currentPlayer = players[i];
		System.out.println("Switching turns");
		if(currentPlayer == null) nextTurn();
		currentPlayer.setTurn();
	}
}
