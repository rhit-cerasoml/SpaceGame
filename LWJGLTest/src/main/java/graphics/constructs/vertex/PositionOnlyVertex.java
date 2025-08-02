package graphics.constructs.vertex;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;

public class PositionOnlyVertex extends VertexData {
    public static final VertexDescriptor descriptor = new VertexDescriptor(
        1,
        new int[] { 2 },
        new int[] { GL_DOUBLE }
    );

    private final double x;
    private final double y;
    public PositionOnlyVertex(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public double getDouble(int attribute, int index) {
        return index == 0 ? x : y;
    }

    @Override
    public int getInt(int attribute, int index) {
        throw new RuntimeException("no int field");
    }
}
