package game.util.physics;

import game.Vec2;
import game.util.Transform;

public class PhysicsObject {

    protected Vec2 pos;
    Vec2 velocity;
    protected double theta;
    double angularVelocity;

    protected BodyConstants constants;

    public PhysicsObject(Vec2 pos, Vec2 velocity, double theta, double angularVelocity, BodyConstants constants){
        this.pos = pos;
        this.velocity = velocity;
        this.theta = theta;
        this.angularVelocity = angularVelocity;
        this.constants = constants;
    }

    private Transform pureTransform;
    public void tick(){
        pos.add(velocity);
        theta += angularVelocity;
    }

    public void thrust(Vec2 origin, Vec2 thrustVector){
        double r = origin.distance(constants.centerOfMass);
        this.velocity.add(thrustVector.multiplyCopy(1 / constants.mass).apply(new Transform(0, 0, theta)));
        double torque = r * thrustVector.crossProduct(origin.subCopy(constants.centerOfMass));
        this.angularVelocity += torque / constants.moment;
        System.out.println("------------------");
        System.out.println("\tr: " + r);
        System.out.println("\tCOM: " + constants.centerOfMass);
        System.out.println("\tmoment: " + constants.moment);
        System.out.println("\tmass: " + constants.mass);
        System.out.println("\trelPos: " + origin.subCopy(constants.centerOfMass));
        System.out.println("\tthrust component: " + thrustVector.crossProduct(origin.subCopy(constants.centerOfMass)));
        System.out.println("\ttorque: " + torque);
    }

    public Transform getTransform(){
        return new Transform(pos.x, pos.y, theta);
    }

}
