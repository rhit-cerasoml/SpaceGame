package game.ships;

import game.CenterOfMassWalker;
import game.MomentWalker;
import game.Vec2;
import game.Window;
import game.quadtree.QuadTree;
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

    public void draw(Window c){
        c.translate((float) pos.x, (float) pos.y);
        c.rotate((float) theta);
        c.strokeWeight(85);
        grid.draw(c);
        c.stroke(255, 255, 255);
        c.circle((float) constants.centerOfMass.x * 30, (float) constants.centerOfMass.y * 30, 15);
        c.circle((float) tv1.x * 30, (float) tv1.y * 30, 5);
        c.line(tv1.multiplyCopy(30), tv1.multiplyCopy(30).addCopy(tv2.multiplyCopy(30)));
        c.rotate((float) -theta);
        c.translate((float) -pos.x, (float) -pos.y);
    }

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
}
