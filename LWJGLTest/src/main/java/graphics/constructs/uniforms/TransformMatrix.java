package graphics.constructs.uniforms;

import game.util.Transform;

import static org.lwjgl.opengl.GL20.*;

public class TransformMatrix {
    private int layout;

    public TransformMatrix(int layout){
        this.layout = layout;
    }
    public void bindData(Transform t){
        float[] data = new float[9];
        data[0] = (float)t.m0;
        data[1] = (float)t.m1;
        data[2] = (float)t.tx;

        data[3] = (float)t.m2;
        data[4] = (float)t.m3;
        data[5] = (float)t.ty;

        data[6] = 0;
        data[7] = 0;
        data[8] = 1;
        glUniformMatrix3fv(layout, true, data);
    }
}
