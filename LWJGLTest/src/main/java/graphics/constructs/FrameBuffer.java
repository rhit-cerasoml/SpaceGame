package graphics.constructs;

import graphics.reload.GPUReloadRegistry;
import graphics.reload.GPUUnloadRegistry;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FrameBuffer {
    private int frameBuffer;
    private int bufferTexture;

    private final int width;
    private final int height;
    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        load();
        GPUUnloadRegistry.register(e -> unload());
        GPUReloadRegistry.register(e -> load());
    }

    private void load(){
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        bufferTexture = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, bufferTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, bufferTexture, 0);
    }

    private void unload(){
        glDeleteFramebuffers(frameBuffer);
        glDeleteTextures(bufferTexture);
    }

    public int getTextureHandle(){
        return bufferTexture;
    }

    public void bind(){
        glViewport(0, 0, width, height);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }
}
