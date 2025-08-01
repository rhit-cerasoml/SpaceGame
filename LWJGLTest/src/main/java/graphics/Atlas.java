package graphics;

import graphics.reload.GPUReloadRegistry;
import graphics.reload.GPUUnloadRegistry;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;

public class Atlas {
    private int handle;
    private final ArrayList<String> paths;
    private HashMap<String, Integer> contentsMap;

    public Atlas(ArrayList<String> paths){
        this.paths = paths;
        load();
        GPUUnloadRegistry.register((e) -> unload());
        GPUReloadRegistry.register((e) -> load());
    }
    public void bind(int unit, int loc){
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
        glUniform1i(loc, unit);
    }

    private void load() {
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, 32, 32, paths.size(), 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        int index = 0;
        for (String path : paths){
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                ByteBuffer buffer = STBImage.stbi_load("src/main/resources/" + path, w, h, channels, 4);
                if (buffer == null) {
                    throw new Exception("Can't load file " + path + ": " + STBImage.stbi_failure_reason());
                }

                glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, index, 32, 32, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                index++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unload(){
        glBindTexture(GL_TEXTURE_2D_ARRAY, handle);
        glDeleteTextures(handle);
    }
}
