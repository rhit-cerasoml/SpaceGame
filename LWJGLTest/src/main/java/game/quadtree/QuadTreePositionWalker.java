package game.quadtree;

public interface QuadTreePositionWalker<Terminal> {
    void visit(int x, int y, Terminal item);
}
