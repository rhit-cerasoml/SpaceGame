package game;

import game.quadtree.QuadTreePositionWalker;
import game.ships.ShipTile;

public class CenterOfMassWalker implements QuadTreePositionWalker<ShipTile> {

    double mass;
    double cx;
    double cy;

    @Override
    public void visit(int x, int y, ShipTile item) {
        cx += x * item.mass;
        cy += y * item.mass;
        mass += item.mass;
    }

    public Vec2 getCenterOfMass(){
        return new Vec2(cx / mass, cy / mass);
    }

    public double getMass(){
        return mass;
    }

}
