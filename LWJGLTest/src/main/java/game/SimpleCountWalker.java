package game;

import game.quadtree.QuadTreeWalker;

public class SimpleCountWalker<T> implements QuadTreeWalker<T> {
    private int count = 0;
    @Override
    public void visit(T t) {
        count++;
    }
    public int getCount(){
        return count;
    }
}
