package pt.iscte.poo.sokobanstarter;

import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Empilhadora extends GameElement{
	private String imageName;
	private int Battery;

	private final int FULL_BATTERY = 100;
	private final int FIRST_LEVEL = 0;
	
	public Empilhadora(Point2D position){
        super(position);
		this.imageName = "Empilhadora_D";
		this.Battery = FULL_BATTERY;
	}

	public int getBattery() {
		return Battery;
	}

	@Override
	public String getName() {
		return imageName;
	}

	@Override
	public int getLayer() {
		return 3;
	}

	public int addBattery(int sumBattery) {
		this.Battery += sumBattery;
		if (Battery > FULL_BATTERY) {
			Battery = FULL_BATTERY;
		}
		return Battery;
	}

	//S1
	// Move e muda a imagem segundo a direcao dada, se estiver dentro dos limites
	public void move(int key) {
		Direction direction = Direction.directionFor(key);
		switch (direction) {
			case UP:
				imageName = "Empilhadora_U";
			break;
			case DOWN:
				imageName = "Empilhadora_D";
			break;
			case LEFT:
				imageName = "Empilhadora_L";
			break;
			case RIGHT:
				imageName = "Empilhadora_R";
			break;
	
			default:
				imageName = "Empilhadora_U";
			break;
		}
	}
	
	public void driveTo(Direction direction) {
		Point2D newPosition = getPosition().plus(direction.asVector());
		if (newPosition.getX()>=0 && newPosition.getX()<10 && newPosition.getY()>=0 && newPosition.getY()<10 ){
			setPosition(newPosition);
			Battery--;
			if( Battery == 0 ) {
				GameEngine.getInstance().infoBox("You ran out of battery :(", "Click SPACE for restart");
				GameEngine.getInstance().restartGame(FIRST_LEVEL);
			}
		}
	}
	
}