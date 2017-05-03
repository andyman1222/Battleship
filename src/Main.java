
//main will handle the turns (and rules?) of the game
public class Main {
	private static Player[] players = {
			new Player("Player 1",0,0),
			new CPUplayer("CPU player",1000,0)
	};
	private static Player currentPlayer;
	public static void main(String[] args){
		int i = 0;
		players[0].getBoard().createShip(0,0,1,5);
		while(true){}
		/*
		while(players.length != 1){
			currentPlayer = players[i];
			currentPlayer.setTurn();
			while(currentPlayer.isCurrentTurn()){}
			currentPlayer.setTurn();
			i = (i+1)%players.length;
		}
		*/
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
}
