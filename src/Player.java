import javax.swing.JOptionPane;

public class Player {
	private String name;
	private Board board;
	private boolean isSettingUp = true, currentTurn;
	public Player(String name, int x, int y, boolean showBtn){
		this.name = name;
		board = new Board(this, x, y, 25, 25, 1000, 1000, showBtn);
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
	
	public void changeEdit(){
		for(Ship ship : board.getShips()){
			ship.changeMovable();
		}
		isSettingUp =! isSettingUp;
		//System.out.println("Changing state");
	}
	
	public void changeEdit(boolean state){
		for(Ship ship : board.getShips()){
			ship.changeMovable(state);
		}
		isSettingUp = state;
		//System.out.println("Changing state");
	}

	private void hideStart() {
		board.removeStart();
		
	}
	
	protected void addShips(Ship[] ships){
		board.createShips(ships);
	}
	
	protected void addShip(int x, int y, int xSize, int ySize) {
		board.createShip(x, y, xSize, ySize);
	}
	
	public void addShip(Ship ship){
		board.createShip(ship);
	}
}
