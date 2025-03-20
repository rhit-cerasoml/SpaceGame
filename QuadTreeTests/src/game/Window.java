package game;

import game.quadtree.QuadTree;
import game.ships.Ship;
import game.util.physics.Collisions;
import game.util.Transform;
import processing.core.PApplet;

import java.util.ArrayList;

public class Window extends PApplet {


    public void settings(){
        size(1000, 1000);
        qt2.put(-1,-1, 5);
        qt2.put(-1,0, 6);
        qt2.put(0,0, 7);
        qt2.put(0,-1, 8);
    }

    public void setup(){
        frameRate(30);
    }

    QuadTree<Integer> qt2 = new QuadTree<>();
    QuadTree<Integer> qt = new QuadTree<>();
    //TreeDrawWalker drawer = new TreeDrawWalker(this);
    double t;
    public static Transform mousePos;

    Ship ship = new Ship(new Vec2(15000, 15000), 3.14);
    public void draw(){
        background(0);
        stroke(255, 0, 0);
        scale((float) 0.04);
        ship.tickShip();
        ship.draw(this);
        scale((float) ((float) 1/0.04));
        strokeWeight(1);


        qt.draw(this);

        //Vec2 COM = COMWalker.getCenterOfMass();
        stroke(0, 255, 255);
        //circle((float) (COM.x * 30 + 15), (float) (COM.y * 30 + 15), 30);

        t += 0.015;

        translate(mouseX, mouseY);
        rotate((float)t);
        qt2.draw(this);
        rotate(-(float)t);
        //qt2.walk(drawer);
        translate(-mouseX, -mouseY);

        Transform identity = new Transform();
        mousePos = new Transform((double)mouseX / 30, (double)mouseY / 30, t);
//
//
//
        ArrayList<Collisions.CollisionPoint<Integer>> results = Collisions.collideQuadTrees(qt, identity, qt2, mousePos);
        stroke(255, 0, 0);
        for(Collisions.CollisionPoint<Integer> pt : results){
            line(pt.Apos.multiplyCopy(30).addCopy(15, 15), pt.Bpos.addCopy(0.5,0.5).apply(mousePos).multiplyCopy(30));
        }


//        Vec2 squareSize = new Vec2(30, 30);


//        Transform t1 = new Transform(mouseX - 500, mouseY - 500, t);
//        Transform t2 = new Transform(60, 60, 0);
//
//        Rect r1 = new Rect(new Vec2(0, 0), squareSize, t1);
//        Rect r2a = new Rect(new Vec2(0, 0), squareSize, t2);
//        Rect r2b = new Rect(new Vec2(30, 0), squareSize, t2);
//        Rect r2c = new Rect(new Vec2(0, 30), squareSize, t2);
//        Rect r2d = new Rect(new Vec2(30, 30), squareSize, t2);
//        r1.draw(this);
//        r2a.draw(this);
//        r2b.draw(this);
//        r2c.draw(this);
//        r2d.draw(this);

//        noFill();
//        circle(0, 0, 5);
//
//        stroke(255, 0, 255);
//        Transform compositeB = t1.invert().multiply(t2);
//        Transform compositeA = t2.invert().multiply(t1);
//        Rect b1 = new Rect(new Vec2(0, 0), squareSize);
//        Rect b2a = new Rect(new Vec2(0, 0), squareSize);
//        Rect b2b = new Rect(new Vec2(30, 0), squareSize, compositeB);
//        Rect b2c = new Rect(new Vec2(0, 30), squareSize, compositeB);
//        Rect b2d = new Rect(new Vec2(30, 30), squareSize, compositeB);
//
//        boolean result = Collisions.collideRects(b1, b2a, compositeB, compositeA);
//        if(result) stroke(255, 0, 0);
//        circle(0, 0, 150);
//        b1.draw(this);
//        b2a.draw(this);
//        b2b.draw(this);
//        b2c.draw(this);
//        b2d.draw(this);
    }

    //CenterOfMassWalker COMWalker = new CenterOfMassWalker();

    int count = 0;
    public void mouseClicked(){
        if(mouseButton == LEFT){
            qt.put(mouseX / 30, mouseY / 30, count);
            count++;
        }else{
            qt.remove(mouseX / 30, mouseY / 30);
        }
        SimpleCountWalker<Integer> walker = new SimpleCountWalker<>();
        qt.walk(walker);
        System.out.println(walker.getCount());
        //COMWalker.reset();
        //COMWalker = new CenterOfMassWalker();
        //qt.walk(COMWalker);
        //Vec2 COM = COMWalker.getCenterOfMass();
        //System.out.println(COM.x + ", " + COM.y);
    }

    public void keyPressed(){

    }

    public void line(Vec2 a, Vec2 b){
        line((float) a.x, (float) a.y, (float) b.x, (float) b.y);
    }
}
