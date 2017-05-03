import javax.swing.JOptionPane;

public class Player {
	private String name;
	private Board board;
	private boolean isSettingUp = true, currentTurn;
	public Player(String name, int x, int y){
		this.name = name;
		board = new Board(this, x, y, 25, 25, 1000, 1000);
	}
	
	public String toString(){
		return name;
	}
	
	public boolean isInPlay(){
		return !isSettingUp;
	}
	
	public boolean isCurrentTurn(){
		return currentTurn;
	}
	
	public void setTurn(boolean state){
		currentTurn = state;
	}

	public void setTurn() {
		currentTurn = !currentTurn;
	}
	
	public void defeat(){
		JOptionPane.showMessageDialog(null, name + " has been defeated by " + Main.getCurrentPlayer() + "!");
		Main.destroy(this);
	}
	
	public void closeJframe(){
		board.closeJframe();
	}
	
	public Board getBoard(){
		return board;
	}
	
}
