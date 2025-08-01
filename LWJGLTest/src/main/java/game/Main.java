package game;

import game.ships.Ship;
import game.ships.ShipTile;
import game.ships.ShipTileVertex;
import game.util.QuadTreeMesher;
import graphics.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import graphics.reload.GPUReloadEvent;
import graphics.reload.GPUReloadRegistry;
import graphics.reload.GPUUnloadEvent;
import graphics.reload.GPUUnloadRegistry;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {


    public static void main(String[] args){
        //Window.main("game.Window", args);
        Window window = new Window("Space Game");

        // ENABLE IF SHADER WON'T COMPILE:
//        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // before creating the window
//        GLUtil.setupDebugMessageCallback();

        Shader s = new Shader("src/main/resources/ship_tile_vert.glsl", "src/main/resources/ship_tile_frag.glsl") {
            @Override
            protected void bindAttributes() {
                glBindAttribLocation(this.handle, 0, "position");
                glBindAttribLocation(this.handle, 1, "tid");
                glBindAttribLocation(this.handle, 2, "uv");
            }
        };
//        QuadBuffer<PositionOnlyVertex> qbuffer = new QuadBuffer<PositionOnlyVertex>(PositionOnlyVertex.descriptor);
//        ArrayList<PositionOnlyVertex> verts = new ArrayList<>();
//        verts.add(new PositionOnlyVertex(-1, -1));
//        verts.add(new PositionOnlyVertex(-1, 1));
//        verts.add(new PositionOnlyVertex(1, -1));
//        verts.add(new PositionOnlyVertex(1, 1));
//        ArrayList<Integer> index = new ArrayList<>();
//        index.add(0);
//        index.add(1);
//        index.add(2);
//        index.add(1);
//        index.add(3);
//        index.add(2);
//        qbuffer.update(verts, index);
//        qbuffer.bindAndPush();

        Ship ship = new Ship(new Vec2(0, 0), 0);
        QuadTreeMesher<ShipTileVertex, ShipTile> mesher = new QuadTreeMesher<>();
        QuadBuffer<ShipTileVertex> qbuffer = new QuadBuffer<ShipTileVertex>(ShipTileVertex.descriptor);
        ship.runWalker(mesher);
        qbuffer.update(mesher.getVertices(), mesher.getIndices());
        qbuffer.bindAndPush();

        int scale = 1;

        int gbuf = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
        int gbufTex = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gbufTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 1920 / scale, 1080 / scale, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbufTex, 0);

//        int tid = Texture.loadTexture("../../../../Art/floor0.png");
        //int tid = Texture.loadTexture("src/resources/floor0.png");

        ArrayList<String> atlasPaths = new ArrayList<String>();
        atlasPaths.add("floor0.png");
        atlasPaths.add("floor1.png");
        Atlas atlas = new Atlas(atlasPaths);

        int i = 9;
        while(!window.shouldClose() && i < 200000){
            i++;

            s.use();
            atlas.bind(0, s.getUniformLocation("atlas"));
            glViewport(0, 0, 1920 / scale, 1080 / scale);
            glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
            glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            qbuffer.bind();
            glDrawElements(GL_TRIANGLES, qbuffer.getCount(), GL_UNSIGNED_INT, 0);

            window.draw(gbufTex);

            if(i == 100){
                Window.checkGL("A");
                glDeleteFramebuffers(gbuf);
                Window.checkGL("B");
                glDeleteTextures(gbufTex);
                Window.checkGL("C");
                window.changeWindowedMode(Window.WindowMode.WINDOWED, glfwGetVideoMode(glfwGetPrimaryMonitor()).width(), glfwGetVideoMode(glfwGetPrimaryMonitor()).height(), "test");
                Window.checkGL("D");
                gbuf = glGenFramebuffers();
                glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
                gbufTex = glGenTextures();
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, gbufTex);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 1920 / scale, 1080 / scale, 0, GL_RGBA, GL_FLOAT, NULL);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbufTex, 0);

                Window.checkGL("AA");
                qbuffer.update(mesher.getVertices(), mesher.getIndices());
                qbuffer.bindAndPush();
            }
        }
    }
}
