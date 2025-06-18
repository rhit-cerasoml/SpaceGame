package game.ships;

import game.CenterOfMassWalker;
import game.MomentWalker;
import game.Vec2;
import game.quadtree.QuadTree;
import game.quadtree.QuadTreePositionWalker;
import game.quadtree.QuadTreeWalker;
import game.util.physics.BodyConstants;
import game.util.physics.PhysicsObject;

public class Ship extends PhysicsObject {
    QuadTree<ShipTile> grid;

    public Ship(Vec2 pos, double theta) {
        super(pos, new Vec2(0, 0), theta, 0, new BodyConstants(0, 0, new Vec2(0, 0)));
        grid = new QuadTree<>();
        grid.put(100, 0, new ShipTile(500));
        grid.put(-100, 0, new ShipTile(500));
        grid.put(0, -1, new ShipTile(50));
        grid.put(-1, -1, new ShipTile(50));
        getShipConstants();
    }

    Vec2 tv1 = new Vec2(1.0, 0);
    Vec2 tv2 = new Vec2(0, 30000);

    int ticksOfThrust = 1;
    public void tickShip(){
        tick();
        if(ticksOfThrust > 0){
            thrust(tv1, tv2);
            ticksOfThrust--;
        }
        //thrust(new Vec2(0, 0).subCopy(tv1), tv2.multiplyCopy(-1));
    }

    public void getShipConstants(){
        CenterOfMassWalker comWalker = new CenterOfMassWalker();
        grid.walk(comWalker);
        Vec2 com = comWalker.getCenterOfMass();
        double mass = comWalker.getMass();
        MomentWalker momentWalker = new MomentWalker(com);
        grid.walk(momentWalker);
        double moment = momentWalker.getMoment();
        constants = new BodyConstants(mass, moment, com.addCopy(0.5, 0.5));
    }

    public void runWalker(QuadTreePositionWalker<ShipTile> walker){
        grid.walk(walker);
    }
}
