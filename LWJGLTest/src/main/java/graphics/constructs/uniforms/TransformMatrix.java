package graphics.constructs.uniforms;

import game.util.physics.Transform;

import static org.lwjgl.opengl.GL20.*;

public class TransformMatrix {
    private int layout;

    public TransformMatrix(int layout){
        this.layout = layout;
    }
    public void bindData(Transform t){
        float[] data = new float[9];
        data[0] = (float) Math.cos(t.getT());
        data[1] = (float) Math.sin(t.getT());
        data[2] = t.getX();
        data[3] = (float) -Math.sin(t.getT());
        data[4] = data[0];
        data[5] = t.getY();
        data[6] = 0;
        data[7] = 0;
        data[8] = 1;
        glUniformMatrix3fv(layout, false, data);
    }
}
