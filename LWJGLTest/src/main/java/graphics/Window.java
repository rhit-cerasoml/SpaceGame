package graphics;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    public long window;
    private Shader shader;
    private int vao;
    private int vbo;
    private ByteBuffer screenQuad;

    public Window(String title){
        init(title);
    }

    public void deconstruct(){
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init(String title) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        final int screenWidth = glfwGetVideoMode(glfwGetPrimaryMonitor()).width();
        final int screenHeight = glfwGetVideoMode(glfwGetPrimaryMonitor()).height();

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(screenWidth, screenHeight, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        GL.createCapabilities();

        // ENABLE IF SHADER WON'T COMPILE:
//        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // before creating the window
//        GLUtil.setupDebugMessageCallback();

        shader = new Shader("src/main/resources/final_pass_vert.glsl", "src/main/resources/final_pass_frag.glsl") {
            @Override
            protected void bindAttributes() {
                glBindAttribLocation(this.handle, 0, "position");
            }
        };
        shader.use();

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 2, GL_INT, false, 0, 0);
        glEnableVertexAttribArray(0);

        screenQuad = BufferUtils.createByteBuffer(12 * 4);
        screenQuad.putInt(-1);
        screenQuad.putInt(-1);
        screenQuad.putInt(-1);
        screenQuad.putInt(1);
        screenQuad.putInt(1);
        screenQuad.putInt(-1);
        screenQuad.putInt(1);
        screenQuad.putInt(-1);
        screenQuad.putInt(-1);
        screenQuad.putInt(1);
        screenQuad.putInt(1);
        screenQuad.putInt(1);
        screenQuad.position(0);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, screenQuad, GL_STATIC_DRAW);
    }

    public void draw(int textureHandle) {

//        QuadBuffer qbuf = new QuadBuffer();
//        qbuf.update(index, triangle, color);
//        qbuf.bindAndPush();

//        int tid = Texture.loadTexture("image.png");
//
//        shader = new Shader("src/main/resources/vert.glsl", "src/main/resources/frag.glsl") {
//            @Override
//            protected void bindAttributes() {
//                glBindAttribLocation(this.handle, 0, "position");
//                glBindAttribLocation(this.handle, 1, "color");
//            }
//        };
//
//        shader.use();

        int error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }

//        int gbuf = glGenFramebuffers();
//        glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
//        int gbufTex = glGenTextures();
//        glBindTexture(GL_TEXTURE_2D, gbufTex);
//       glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 1920 / scale, 1080 / scale, 0, GL_RGBA, GL_FLOAT, NULL);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbufTex, 0);



        //while ( !glfwWindowShouldClose(window) ) {

            shader.use();
            glViewport(0, 0, 1920, 1080);
            glBindTexture(GL_TEXTURE_2D, textureHandle);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glBindVertexArray(vao);
            glClearColor(0.5f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glDrawArrays(GL_TRIANGLES, 0, 6);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        //}
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(window);
    }

}
