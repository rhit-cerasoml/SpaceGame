package test;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class QuadBuffer {
    ByteBuffer vertices;
    ByteBuffer colors;
    ByteBuffer indices;


    private int vao;
    private int coordVBO;
    private int colorVBO;
    private int indicesVBO;

    public QuadBuffer(){
        vao = glGenVertexArrays();
        coordVBO = glGenBuffers();
        colorVBO = glGenBuffers();
        indicesVBO = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, coordVBO);
        glVertexAttribPointer(0, 2, GL_DOUBLE, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, colorVBO);
        glVertexAttribPointer(1, 3, GL_DOUBLE, false, 0, 0);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    void update(int[] index, double[] triangle, double[] color) {
        vertices = Util.storeArrayInBuffer(triangle);
        colors = Util.storeArrayInBuffer(color);
        indices = Util.storeArrayInBuffer(index);
    }

    void bindAndPush(){
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, coordVBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, colorVBO);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
    }
}
