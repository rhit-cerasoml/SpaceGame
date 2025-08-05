package game;

import game.ships.Ship;
import game.ships.ShipTile;
import game.ships.ShipTileVertex;
import game.util.QuadTreeMesher;
import game.util.physics.Transform;
import graphics.constructs.*;
import graphics.constructs.unfiroms.TransformMatrix;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1i;

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

        FrameBuffer gbuffer = new FrameBuffer(1920, 1080);

//        int tid = Texture.loadTexture("../../../../Art/floor0.png");
        //int tid = Texture.loadTexture("src/resources/floor0.png");

        ArrayList<String> atlasPaths = new ArrayList<String>();
        atlasPaths.add("floor0.png");
        atlasPaths.add("floor1.png");
        Atlas atlas = new Atlas(atlasPaths);

        int i = 0;
        while(!window.shouldClose() && i < 20 * 100){
            i++;

            s.use();
            atlas.bind(0, s.getUniformLocation("atlas"));

            gbuffer.bind();

            glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glUniform1i(0, i);
            TransformMatrix mat = new TransformMatrix(1);
            mat.bindData(new Transform(0, 0, (float)Math.toRadians(i)));

            qbuffer.bind();
            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glDrawElements(GL_TRIANGLES, qbuffer.getCount(), GL_UNSIGNED_INT, 0);

            //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            window.draw(gbuffer.getTextureHandle());


            if(i % 200 == 0){;

                window.changeWindowedMode(Window.WindowMode.WINDOWED, glfwGetVideoMode(glfwGetPrimaryMonitor()).width(), glfwGetVideoMode(glfwGetPrimaryMonitor()).height(), "test");

                qbuffer.update(mesher.getVertices(), mesher.getIndices());
                qbuffer.bindAndPush();
            }else if(i % 200 == 100){

                window.changeWindowedMode(Window.WindowMode.BORDERLESS_WINDOWED, glfwGetVideoMode(glfwGetPrimaryMonitor()).width(), glfwGetVideoMode(glfwGetPrimaryMonitor()).height(), "test");

                qbuffer.update(mesher.getVertices(), mesher.getIndices());
                qbuffer.bindAndPush();
            }
        }
    }
}
