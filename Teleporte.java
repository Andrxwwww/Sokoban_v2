package pt.iscte.poo.sokobanstarter;
import pt.iscte.poo.utils.Point2D;

public class Teleporte extends GameElement {

    public Teleporte(Point2D position) {
        super(position);
    }

    @Override
    public String getName() {
        return "Teleporte";
    }

    @Override
    public int getLayer() {
        return 1;
    }

}