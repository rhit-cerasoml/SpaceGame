package graphics;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;

public class Atlas {
    private int handle;
    public Atlas(){
        handle = glGenTextures();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w0 = stack.mallocInt(1);
            IntBuffer h0 = stack.mallocInt(1);
            IntBuffer channels0 = stack.mallocInt(1);
            IntBuffer w1 = stack.mallocInt(1);
            IntBuffer h1 = stack.mallocInt(1);
            IntBuffer channels1 = stack.mallocInt(1);

            ByteBuffer buffer0 = STBImage.stbi_load("src/main/resources/floor0.png", w0, h0, channels0, 4);
            ByteBuffer buffer1 = STBImage.stbi_load("src/main/resources/floor1.png", w1, h1, channels1, 4);
            if (buffer0 == null || buffer1 == null) {
                throw new Exception("Can't load file" + STBImage.stbi_failure_reason());
            }

            check("pass 0");
            //glTexStorage3D(GL_TEXTURE_2D_ARRAY, 2, GL_RGBA, 1024, 1024, 16);

//            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, 32, 32, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer0);

            glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, 32, 32, 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 0, 32, 32, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer0);
            check("pass 1");
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, 1, 32, 32, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer1);
            check("pass 2");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void bind(){
        glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
    }
    private void check(String pass){
        int error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }else{
            System.out.println(pass);
        }
    }
}
