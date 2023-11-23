package pt.iscte.poo.sokobanstarter;


import pt.iscte.poo.utils.Point2D;

public class Palete extends GameElement{

	private boolean canMove;

	public Palete(Point2D position , boolean canMove){
        super(position);
		this.canMove = canMove;
	}

	@Override
	public String getName() {
		return "Palete";
	}

	@Override
	public int getLayer() {
		return 2;
	}

	public boolean canMove() {
		return canMove;
	}

	public boolean setCanMove(boolean canMove) {
		return this.canMove = canMove;
	}
	
}