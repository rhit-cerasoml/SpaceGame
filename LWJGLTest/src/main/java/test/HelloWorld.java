package test;

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

public class HelloWorld {

    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
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

        // ENABLE IF SHADER WON'T COMPILE:
//        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // before creating the window
//        GLUtil.setupDebugMessageCallback();

        // Create the window
        window = glfwCreateWindow(screenWidth, screenHeight, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            } else {
                shader.reload();
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
    }

    private int[] index = {
            //the order to render the vertices
            0,
            1,
            2,
            0,
            3,
            2
    };

    //the vertex data (x and y)
    double[] triangle = {
            -1.0,	 1.0, //first x and y
            -1.0,	-1.0, //second x and y
            1.0,	-1.0, //third x and y
            1.0,     1.0
    };

    //the color data (red, green, and blue)
    double[] color = {
            0.0, 0.0, 0.0, //first vertex color
            0.0, 1.0, 0.0, //second vertex color
            1.0, 1.0, 1.0, //third vertex color
            1.0, 0.0, 1.0, //third vertex color
    };

    Shader shader;
    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        QuadBuffer qbuf = new QuadBuffer();
        qbuf.update(index, triangle, color);
        qbuf.bindAndPush();

        int tid = Texture.loadTexture("image.png");

        shader = new Shader("src/main/resources/vert.glsl", "src/main/resources/frag.glsl") {
            @Override
            protected void bindAttributes() {
                glBindAttribLocation(this.handle, 0, "position");
                glBindAttribLocation(this.handle, 1, "color");
            }
        };

        shader.use();

        //compileShaders();

        int error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }

        int gbuf = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
        int gbufTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gbufTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, 1920, 1080, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gbufTex, 0);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glBindTexture(GL_TEXTURE_2D, tid);

            glBindFramebuffer(GL_FRAMEBUFFER, gbuf);
            glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glDrawElements(GL_TRIANGLES, index.length, GL_UNSIGNED_INT, 0);
            //glDrawBuffer(GL_COLOR_ATTACHMENT0);

            //triangle[0] += 1f;
            qbuf.update(index, triangle, color);
            qbuf.bindAndPush();

            glBindTexture(GL_TEXTURE_2D, gbufTex);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glClearColor(0.5f, 0.5f, 0.9f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            glDrawElements(GL_TRIANGLES, index.length, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

}
