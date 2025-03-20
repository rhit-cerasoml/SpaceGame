package game.util;

import game.Vec2;
import processing.core.PApplet;

public class Rect {
    public Vec2 p1, p2, p3, p4;
    Vec2 zero = new Vec2(0, 0);
    public Rect(Vec2 pos, Vec2 size){
        double x1 = pos.x;
        double y1 = pos.y;
        double x2 = pos.x + size.x;
        double y2 = pos.y + size.y;
        p1 = new Vec2(x1, y1);
        p2 = new Vec2(x1, y2);
        p3 = new Vec2(x2, y2);
        p4 = new Vec2(x2, y1);
    }

    public Rect(double x, double y, double s){
        p1 = new Vec2(x, y);
        p2 = new Vec2(x, y + s);
        p3 = new Vec2(x + s, y + s);
        p4 = new Vec2(x + s, y);
    }

    public Rect(Vec2 pos, Vec2 size, Transform transform){
        this(pos, size);
        applyTransform(transform);
    }

    public Rect(Rect in){
        this.p1 = new Vec2(in.p1);
        this.p2 = new Vec2(in.p2);
        this.p3 = new Vec2(in.p3);
        this.p4 = new Vec2(in.p4);
    }

    public void applyTransform(Transform transform){
        p1 = p1.apply(transform);
        p2 = p2.apply(transform);
        p3 = p3.apply(transform);
        p4 = p4.apply(transform);
        zero = zero.apply(transform);
    }

    public Rect copyTransformed(Transform transform){
        Rect r = new Rect(this);
        r.applyTransform(transform);
        return r;
    }

    public void draw(PApplet c){
        c.line((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y);
        c.line((float) p2.x, (float) p2.y, (float) p3.x, (float) p3.y);
        c.line((float) p3.x, (float) p3.y, (float) p4.x, (float) p4.y);
        c.line((float) p4.x, (float) p4.y, (float) p1.x, (float) p1.y);
        c.circle((float) zero.x, (float) zero.y, 15);
    }

    public void draw(PApplet c, float s){
        c.line((float) p1.x * s, (float) p1.y * s, (float) p2.x * s, (float) p2.y * s);
        c.line((float) p2.x * s, (float) p2.y * s, (float) p3.x * s, (float) p3.y * s);
        c.line((float) p3.x * s, (float) p3.y * s, (float) p4.x * s, (float) p4.y * s);
        c.line((float) p4.x * s, (float) p4.y * s, (float) p1.x * s, (float) p1.y * s);
        c.circle((float) zero.x * s, (float) zero.y * s, 15);
    }

    public boolean allXLessThan(double v){
        return p1.x < v && p2.x < v && p3.x < v && p4.x < v;
    }

    public boolean allXGreaterThan(double v){
        return p1.x > v && p2.x > v && p3.x > v && p4.x > v;
    }

    public boolean allYLessThan(double v){
        return p1.y < v && p2.y < v && p3.y < v && p4.y < v;
    }

    public boolean allYGreaterThan(double v){
        return p1.y > v && p2.y > v && p3.y > v && p4.y > v;
    }
    public String toString(){
        return "[" + p1 + ", " + p2 + ", " + p3 + ", " + p4 + "]";
    }
}
