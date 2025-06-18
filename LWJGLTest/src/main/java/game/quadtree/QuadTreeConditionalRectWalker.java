package game.quadtree;

import game.util.Rect;

public interface QuadTreeConditionalRectWalker<Terminal> {
    boolean visit(Rect r, boolean isTerminal, Terminal terminal);
}
