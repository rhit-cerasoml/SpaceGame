package graphics;

import graphics.reload.GPUReloadEvent;
import graphics.reload.GPUReloadRegistry;
import graphics.reload.GPUUnloadEvent;
import graphics.reload.GPUUnloadRegistry;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
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
    private int screenWidth;
    private int screenHeight;

    public enum WindowMode {
        FULLSCREEN,
        BORDERLESS_WINDOWED,
        WINDOWED
    }

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

        screenWidth = glfwGetVideoMode(glfwGetPrimaryMonitor()).width();
        screenHeight = glfwGetVideoMode(glfwGetPrimaryMonitor()).height();

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        
        // Create the window
        changeWindowedMode(WindowMode.BORDERLESS_WINDOWED, screenWidth, screenHeight, title);

        initGL();

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

    private void initGL(){
        GL.createCapabilities();
    }

    public void draw(int textureHandle) {

        int error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }

        shader.use();
        
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, widthBuffer, heightBuffer);
        
        int width = widthBuffer.get(0);
        int height = heightBuffer.get(0);

        int viewportHeight;
        int viewportWidth;
        if (width >= height){//screen is wider than tall
            viewportWidth = height *16/9;
            glViewport((width-viewportWidth)/2, 0, viewportWidth, height);
        
        }else{//screen is taller than wide
            viewportHeight = width *9/16;
            glViewport(0, (height-viewportHeight)/2, width, viewportHeight);
        
        }
        
        
        glActiveTexture(GL_TEXTURE0);
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
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(window);
    }

    public static void checkGL(String pass){
        int error = glGetError();
        if(error != 0) {
            System.out.println("Failed on " + pass + " - OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }else{
            System.out.println("check passed - " + pass);
        }
    }

    //Change Window Mode between Fullscreen, Borderless Windowed Fullscreen, and Windowed
    // 0: Fullscreen, 1: Borderless Windowed Fullscreen, 2: Windowed
    public void changeWindowedMode(WindowMode mode, final int screenWidth, final int screenHeight, String title){
        GPUUnloadRegistry.INSTANCE.fireEvent(new GPUUnloadEvent());
        if ( window != NULL ){
            glfwDestroyWindow(window);
        }
        switch (mode) {
            case FULLSCREEN:
                window = glfwCreateWindow(screenWidth, screenHeight, title, glfwGetPrimaryMonitor(), NULL);
                break;
            case BORDERLESS_WINDOWED:
                glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
                glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
                window = glfwCreateWindow(screenWidth, screenHeight, title, NULL, NULL);
                break;
            case WINDOWED:
                glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
                glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
                window = glfwCreateWindow(screenWidth/2, screenHeight/2, title, NULL, NULL);
                break;
        }

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

        GPUReloadRegistry.INSTANCE.fireEvent(new GPUReloadEvent());

        // Make the window visible
        glfwShowWindow(window);
        
    }
}
