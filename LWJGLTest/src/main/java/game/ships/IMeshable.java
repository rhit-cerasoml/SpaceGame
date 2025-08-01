package game.ships;

import graphics.VertexData;

import java.util.ArrayList;

public interface IMeshable<V extends VertexData > {
    void genMesh(int x, int y, ArrayList<V> vertices, ArrayList<Integer> indices);
}
