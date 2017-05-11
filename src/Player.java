import javax.swing.JOptionPane;

public class Player {
	private String name;
	private Board board;
	private boolean isSettingUp = true, currentTurn;
	private int points = 0;
	
	/**
	 * creates new Player with specified name, board size, window size, window position, and whether or not to show the start game btn
	 * @param name name of player
	 * @param x x position of board on window
	 * @param y y position of board on window
	 * @param boardX width of board in terms of number of squares
	 * @param boardY height of board in terms of number of squares
	 * @param width width of window
	 * @param height height of window
	 * @param showBtn whether or not to show start game btn
	 */
	public Player(String name, int x, int y, int boardX, int boardY, int width, int height, boolean showBtn){
		this.name = name;
		board = new Board(this, x, y, boardX, boardY, width, height, showBtn);
	}
	
	/**
	 * @return player name
	 */
	public String toString(){
		return name;
	}
	
	/**
	 * 
	 * @return whether or not the player is setting up
	 */
	public boolean isInPlay(){
		return !isSettingUp;
	}
	
	/**
	 * 
	 * @return whether or not it is this player's turn
	 */
	public boolean isCurrentTurn(){
		return currentTurn;
	}
	
	/**
	 * changes the current player's turn to state
	 * @param state state of player's turn
	 */
	public void setTurn(boolean state){
		currentTurn = state;
	}

	/**
	 * changes player's current turn
	 */
	public void setTurn() {
		currentTurn = !currentTurn;
	}
	
	/**
	 * call when the player loses
	 */
	public void defeat(){
		JOptionPane.showMessageDialog(null, name + " has been defeated by " + Main.getCurrentPlayer() + "!");
		Main.destroy(this);
	}
	
	/**
	 * closes player's Jframe
	 */
	public void closeJframe(){
		board.closeJframe();
	}
	
	/**
	 * 
	 * @return the player's board
	 */
	public Board getBoard(){
		return board;
	}
	
	/**
	 * change whether or not the player's board is editable
	 */
	public void changeEdit(){
		for(Ship ship : board.getShips()){
			ship.changeMovable();
		}
		isSettingUp =! isSettingUp;
		//System.out.println("Changing state");
	}
	
	/**
	 * change whether or not the player's board is editable to state
	 * @param state change whether or not the player's board is editable
	 */
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
	
	/**
	 * add ships to the player's board
	 * @param ships ships to add
	 */
	protected void addShips(Ship[] ships){
		board.createShips(ships);
	}
	
	/**
	 * add the specified ship to the player's board
	 * @param x x position on board
	 * @param y y position on board
	 * @param xSize width of ship on board
	 * @param ySize height of ship on board
	 */
	protected void addShip(int x, int y, int xSize, int ySize) {
		board.createShip(x, y, xSize, ySize);
	}
	
	/**
	 * adds the specified ship to the player's board
	 * @param ship ship to add
	 */
	public void addShip(Ship ship){
		board.createShip(ship);
	}
	
	/**
	 * check whether or not the current player is a cpu player
	 * @return false if it is, true otherwise.
	 * Note: set this manually per class (make a separate CPU class and override this method.)
	 */
	public boolean isComputer(){
		return false;
	}
	
	public void addPoints(int points){
		this.points += points;
	}
	
	public int getPoints(){
		return points;
	}
}
