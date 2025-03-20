package game;

import game.quadtree.QuadTreeConditionalRectWalker;
import game.util.Rect;
import processing.core.PApplet;

public class TreeDrawWalker implements QuadTreeConditionalRectWalker<Integer> {
    PApplet c;
    public TreeDrawWalker(PApplet c){
        this.c = c;
    }

    @Override
    public boolean visit(Rect r, boolean isTerminal, Integer integer) {
        if(isTerminal){
            c.stroke(255, 0, 0);
        }else{
            c.stroke(255);
        }
        r.draw(c, 30);
        return true;
    }
}
