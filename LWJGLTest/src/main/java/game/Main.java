package game;

import graphics.*;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {


    public static void main(String[] args){
        //Window.main("game.Window", args);
        Window window = new Window("Space Game");
        Shader s = new Shader("src/main/resources/vert.glsl", "src/main/resources/frag.glsl") {
            @Override
            protected void bindAttributes() {
                glBindAttribLocation(this.handle, 0, "position");
            }
        };
        QuadBuffer<PositionOnlyVertex> qbuffer = new QuadBuffer<PositionOnlyVertex>(PositionOnlyVertex.descriptor);
        ArrayList<PositionOnlyVertex> verts = new ArrayList<>();
        verts.add(new PositionOnlyVertex(-1, -1));
        verts.add(new PositionOnlyVertex(-1, 1));
        verts.add(new PositionOnlyVertex(1, -1));
        verts.add(new PositionOnlyVertex(1, 1));
        ArrayList<Integer> index = new ArrayList<>();
        index.add(0);
        index.add(1);
        index.add(2);
        index.add(1);
        index.add(3);
        index.add(2);
        qbuffer.update(verts, index);
        qbuffer.bindAndPush();

        int scale = 2;

        int gbuf = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
        int gbufTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gbufTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 1920 / scale, 1080 / scale, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbufTex, 0);

        int tid = Texture.loadTexture("image.png");



        while(!window.shouldClose()){glBindTexture(GL_TEXTURE_2D, tid);
            glViewport(0, 0, 1920 / scale, 1080 / scale);
            glBindFramebuffer(GL_FRAMEBUFFER, gbuf);

            glBindTexture(GL_TEXTURE_2D, tid);

            glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            qbuffer.bind();
            glDrawElements(GL_TRIANGLES, qbuffer.getCount(), GL_UNSIGNED_INT, 0);

            window.draw(gbuf);
        }
    }
}
