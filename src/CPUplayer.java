
public class CPUplayer extends Player{

	public CPUplayer(String name, int i, int j) {
		super(name, i, j, false);
		getBoard().randomizeShips();
	}
	
	@Override
	public void addShips(Ship[] ships){
		changeEdit(true);
		super.addShips(ships);
		getBoard().randomizeShips();
		changeEdit();
	}
	
	@Override
	public void addShip(int x, int y, int xSize, int ySize){
		changeEdit(true);
		super.addShip( x,  y,  xSize,  ySize);
		getBoard().randomizeShips();
		changeEdit();
	}
	
	@Override
	public void addShip(Ship ship){
		changeEdit(true);
		super.addShip(ship);
		getBoard().randomizeShips();
		changeEdit();
	}
}
