package game.ships;

import java.util.ArrayList;

public interface IMeshable<V> {
    void genMesh(int x, int y, ArrayList<V> vertices, ArrayList<Integer> indices);
}
