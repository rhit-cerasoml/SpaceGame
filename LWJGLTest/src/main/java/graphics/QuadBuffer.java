package graphics;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class QuadBuffer<V extends VertexData> {
    ByteBuffer[] attributes;
    ByteBuffer indices;

    private final int vao;
    private final int[] attributeVBOs;
    private final int indexVBO;

    private final VertexDescriptor vd;

    private int local_count = 0;
    private int count = 0;

    public QuadBuffer(VertexDescriptor vd){
        this.vd = vd;
        vao = glGenVertexArrays();

        attributes = new ByteBuffer[vd.count];
        attributeVBOs = new int[vd.count];
        for(int i = 0; i < vd.count; i++){
            attributeVBOs[i] = glGenBuffers();
        }

        indexVBO = glGenBuffers();

        glBindVertexArray(vao);

        for(int i = 0; i < vd.count; i++){
            glBindBuffer(GL_ARRAY_BUFFER, attributeVBOs[i]);
            glVertexAttribPointer(i, vd.attribute_sizes[i], vd.attribute_types[i], false, 0, 0);
            glEnableVertexAttribArray(i);
        }

        glBindVertexArray(0);
    }

    public void update(ArrayList<V> vertices, ArrayList<Integer> indexList) {
        vd.collect(vertices, attributes);

        indices = BufferUtils.createByteBuffer(indexList.size() * 4);
        for(int i : indexList) {
            indices.putInt(i);
        }
        indices.position(0);

        local_count = indexList.size();
    }

    public void bindAndPush(){
        count = local_count;
        glBindVertexArray(vao);

        for(int i = 0; i < vd.count; i++){
            glBindBuffer(GL_ARRAY_BUFFER, attributeVBOs[i]);
            glBufferData(GL_ARRAY_BUFFER, attributes[i], GL_DYNAMIC_DRAW);
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
    }

    public void bind(){
        glBindVertexArray(vao);
    }

    public int getCount(){
        return count;
    }
}
