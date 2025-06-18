package graphics;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_INT;

public class VertexDescriptor {
    public final int count;
    public final int[] attribute_sizes;
    public final int[] attribute_types;
    public VertexDescriptor(int count, int[] attribute_sizes, int[] attribute_types){
        this.count = count;
        this.attribute_sizes = attribute_sizes;
        this.attribute_types = attribute_types;
    }

    public void put(ByteBuffer buf, int index, VertexData vertexData){
        switch (attribute_types[index]){
            case GL_DOUBLE -> {
                for(int i = 0; i < attribute_sizes[index]; i++)
                    buf.putDouble(vertexData.getDouble(index, i));
            }
            case GL_INT -> {
                for(int i = 0; i < attribute_sizes[index]; i++)
                    buf.putInt(vertexData.getInt(index, i));
            }
        }
    }

    private static int getTypeSize(int type){
        return switch (type) {
            case GL_DOUBLE -> 8;
            case GL_INT -> 4;
            default -> throw new RuntimeException("UNKNOWN ATTRIBUTE TYPE");
        };
    }

    void collect(ArrayList<? extends VertexData> vertices, ByteBuffer[] buffers){
        for(int i = 0; i < buffers.length; i++){
            buffers[i] = BufferUtils.createByteBuffer(vertices.size() * getTypeSize(attribute_types[i]) * attribute_sizes[i]);
        }

        for(VertexData v : vertices){
            for(int i = 0; i < buffers.length; i++) {
                put(buffers[i], i, v);
            }
        }

        for(ByteBuffer b : buffers){
            b.position(0);
        }
    }

}
