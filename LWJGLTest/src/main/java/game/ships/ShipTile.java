package game.ships;

public class ShipTile {
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
}
