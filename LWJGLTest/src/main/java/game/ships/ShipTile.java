package game.ships;

import java.util.ArrayList;

public class ShipTile implements IMeshable<ShipTileVertex> {
    public static int idGen;
    private int id;
    public double mass;
    public ShipTile(double mass){
        this.mass = mass;
        id = idGen;
        idGen++;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }

    @Override
    public void genMesh(int x, int y, ArrayList<ShipTileVertex> vertices, ArrayList<Integer> indices) {
        int zero_index = vertices.size();
        vertices.add(new ShipTileVertex(x, y, 0, 0, 0));
        vertices.add(new ShipTileVertex(x + 1, y, 0, 1, 0));
        vertices.add(new ShipTileVertex(x, y + 1, 0, 0 , 1));
        vertices.add(new ShipTileVertex(x + 1, y + 1, 0, 1, 1));
        indices.add(zero_index);
        indices.add(zero_index + 1);
        indices.add(zero_index + 2);
        indices.add(zero_index + 1);
        indices.add(zero_index + 3);
        indices.add(zero_index + 2);
    }
}
