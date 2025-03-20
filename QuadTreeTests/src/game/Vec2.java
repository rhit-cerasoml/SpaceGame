package game;

import game.util.Transform;
import processing.core.PApplet;

public class Vec2 {
    public double x, y;
    public Vec2(Vec2 v){
        this.x = v.x;
        this.y = v.y;
    }
    public Vec2(double x, double y){
        this.x = x;
        this.y = y;
    }
    public Vec2 apply(Transform transform){
        double rx = x * transform.m0 + y * transform.m1 + transform.tx;
        double ry = x * transform.m2 + y * transform.m3 + transform.ty;
        return new Vec2(rx, ry);
    }
    public String toString(){
        return "(" + x + ", " + y + ")";
    }
    public Vec2 addCopy(double dx, double dy){
        return new Vec2(x + dx, y + dy);
    }
    public Vec2 addCopy(Vec2 o){
        return new Vec2(x + o.x, y + o.y);
    }
    public Vec2 multiplyCopy(double f){
        return new Vec2(x * f, y * f);
    }
    public Vec2 subCopy(Vec2 o){
        return new Vec2(x - o.x, y - o.y);
    }
    public void add(Vec2 o){
        this.x += o.x;
        this.y += o.y;
    }
    public double distance(Vec2 o){
        double xc = o.x - x;
        double yc = o.y - y;
        return Math.sqrt(xc * xc + yc * yc);
    }
    public double dotProduct(Vec2 o){
        return x * o.x + y * o.y;
    }
    public Vec2 projectionOnto(Vec2 o){
        if(o.x == 0 && o.y == 0) return new Vec2(0, 0);
        return o.multiplyCopy(this.dotProduct(o) / o.dotProduct(o));
    }
    public Vec2 rejectionOf(Vec2 o){
        System.out.println("\tproj:" + projectionOnto(o));
        return this.subCopy(projectionOnto(o));
    }
    public double magnitude(){
        return Math.sqrt(x * x + y * y);
    }
    public double crossProduct(Vec2 o){
        return x * o.y + y * o.x;
    }
    public void draw(Window c){
        c.line(new Vec2(0, 0), this);
    }
}
