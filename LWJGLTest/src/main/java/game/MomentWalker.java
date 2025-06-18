package game;

import game.quadtree.QuadTreePositionWalker;
import game.ships.ShipTile;

public class MomentWalker implements QuadTreePositionWalker<ShipTile> {
    double moment;
    Vec2 CoM;
    public MomentWalker(Vec2 CoM){
        this.CoM = CoM;
    }

    @Override
    public void visit(int x, int y, ShipTile item) {
        double r = CoM.distance(new Vec2(x, y));
        moment += r * r * item.mass;
    }

    public double getMoment(){
        return moment;
    }
}
