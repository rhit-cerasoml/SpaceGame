package game.ships;

import graphics.VertexData;
import graphics.VertexDescriptor;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_INT;

public class ShipTileVertex extends VertexData {

    public static final VertexDescriptor descriptor = new VertexDescriptor(
            3,
            new int[] { 2,         2,         1      },
            new int[] { GL_DOUBLE, GL_DOUBLE, GL_INT }
    );

    private final double x;
    private final double y;
    private final double uvx;
    private final double uvy;
    private final int tid;
    public ShipTileVertex(double x, double y, int tid, int uvx, int uvy){
        this.x = x;
        this.y = y;
        this.uvx = uvx;
        this.uvy = uvy;
        this.tid = tid;
    }

    @Override
    public double getDouble(int attribute, int index) {
        return attribute == 0 ?
                (index == 0 ? x : y) :
                (index == 0 ? uvx : uvy);
    }

    @Override
    public int getInt(int attribute, int index) {
        return tid;
    }
}
