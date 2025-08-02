package game.util;

import game.quadtree.QuadTreePositionWalker;
import game.ships.IMeshable;
import graphics.constructs.vertex.VertexData;

import java.util.ArrayList;

public class QuadTreeMesher<V extends VertexData, T extends IMeshable<V>> implements QuadTreePositionWalker<T> {
    private final ArrayList<V> vertices = new ArrayList<>();
    private final ArrayList<Integer> indices = new ArrayList<>();

    @Override
    public void visit(int x, int y, T item) {
        item.genMesh(x, y, vertices, indices);
    }

    public ArrayList<V> getVertices(){ return vertices; }
    public ArrayList<Integer> getIndices(){ return indices; }
}
