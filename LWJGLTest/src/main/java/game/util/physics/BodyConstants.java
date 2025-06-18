package game.util.physics;

import game.Vec2;

public class BodyConstants {
    public double mass;
    public double moment;
    public Vec2 centerOfMass;
    public BodyConstants(double mass, double moment, Vec2 CoM){
        this.mass = mass;
        this.moment = moment;
        this.centerOfMass = CoM;
    }
}
