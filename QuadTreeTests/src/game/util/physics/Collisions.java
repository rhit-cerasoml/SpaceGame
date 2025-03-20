package game.util.physics;

import game.Vec2;
import game.Window;
import game.quadtree.QuadTree;
import game.util.Rect;
import game.util.Transform;
import processing.core.PApplet;

import java.util.ArrayList;

public class Collisions {

    // transform1 - t1.invert() * t2
    // transform2 - t2.invert() * t1
    public static boolean collideRects(Vec2 p1, Vec2 s1, Vec2 p2, Vec2 s2, Transform transform1, Transform transform2){
        Rect r1 = new Rect(p1, s1);
        Rect r1p = new Rect(p1, s1, transform2);
        Rect r2 = new Rect(p2, s2);
        Rect r2p = new Rect(p2, s2, transform1);

        if(r1p.allXLessThan(r2.p1.x) || r1p.allXGreaterThan(r2.p2.x)){
            return false;
        }
        if(r1p.allYLessThan(r2.p1.y) || r1p.allYGreaterThan(r2.p2.y)){
            return false;
        }
        if(r2p.allXLessThan(r1.p1.x) || r2p.allXGreaterThan(r1.p2.x)){
            return false;
        }
        if(r2p.allYLessThan(r1.p1.y) || r2p.allYGreaterThan(r1.p2.y)){
            return false;
        }
        return true;
    }

    // transform1 - t1.invert() * t2
    // transform2 - t2.invert() * t1
    public static boolean collideRects(Rect r1, Rect r2, Transform transform1, Transform transform2){
        Rect r1p = r1.copyTransformed(transform2);
        Rect r2p = r2.copyTransformed(transform1);
        if(r1p.allXLessThan(r2.p1.x) || r1p.allXGreaterThan(r2.p3.x)){
            return false;
        }
        if(r1p.allYLessThan(r2.p1.y) || r1p.allYGreaterThan(r2.p3.y)){
            return false;
        }
        if(r2p.allXLessThan(r1.p1.x) || r2p.allXGreaterThan(r1.p3.x)){
            return false;
        }
        if(r2p.allYLessThan(r1.p1.y) || r2p.allYGreaterThan(r1.p3.y)){
            return false;
        }
        return true;
    }

    public static class CollisionPoint<Terminal> {
        public Vec2 Apos;
        public Terminal A;
        public Vec2 Bpos;
        public Terminal B;
        public CollisionPoint(Terminal A, double ax, double ay, Terminal B, double bx, double by){
            this.A = A;
            this.Apos = new Vec2(ax, ay);
            this.B = B;
            this.Bpos = new Vec2(bx, by);
        }
    }

    public static <T> ArrayList<CollisionPoint<T>> collideQuadTrees(QuadTree<T> q1, Transform t1, QuadTree<T> q2, Transform t2){
        ArrayList<CollisionPoint<T>> results = new ArrayList<>();
        if(q1.isEmpty() || q2.isEmpty()) return results;
        Transform t1r = t1.invert().multiply(t2);
        Transform t2r = t2.invert().multiply(t1);
        collideQuads(q1.getIterator(), q2.getIterator(), t1r, t2r, results);
        return results;
    }

    public static <T> void collideQuads(QuadTree<T>.QuadTreeIterator q1, QuadTree<T>.QuadTreeIterator q2, Transform t1r, Transform t2r, ArrayList<CollisionPoint<T>> results){
        if(collideRects(q1.getBounds(), q2.getBounds(), t1r, t2r)){
            boolean left_terminal = q1.isTerminal();
            boolean right_terminal = q2.isTerminal();
            if(left_terminal && right_terminal){
                results.add(new CollisionPoint<>(q1.getTerminal(), q1.x, q1.y, q2.getTerminal(), q2.x, q2.y));
            }else if(left_terminal){
                QuadTree<T>.QuadTreeIterator subIter;
                q2.reset();
                while((subIter = q2.getNext()) != null){
                    collideQuads(q1, subIter, t1r, t2r, results);
                }
            }else if(right_terminal){
                QuadTree<T>.QuadTreeIterator subIter;
                q1.reset();
                while((subIter = q1.getNext()) != null){
                    collideQuads(subIter, q2, t1r, t2r, results);
                }
            }else{
                if(q2.size > q1.size){
                    QuadTree<T>.QuadTreeIterator subIter;
                    q2.reset();
                    while((subIter = q2.getNext()) != null){
                        collideQuads(q1, subIter, t1r, t2r, results);
                    }
                }else{
                    QuadTree<T>.QuadTreeIterator subIter;
                    q1.reset();
                    while((subIter = q1.getNext()) != null){
                        collideQuads(subIter, q2, t1r, t2r, results);
                    }
                }
            }
        }
    }
}
