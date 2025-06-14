package test;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.*;
import java.util.Scanner;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
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

        // Create the window
        window = glfwCreateWindow(screenWidth, screenHeight, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
    };

    //the vertex data (x and y)
    double[] triangle = {
            0.0,	 0.5, //first x and y
            -0.5,	-0.5, //second x and y
            0.5,	-0.5, //third x and y
    };

    //the color data (red, green, and blue)
    double[] color = {
            0.0, 1.0, 0.0, //first vertex color
            1.0, 0.0, 0.0, //second vertex color
            0.0, 0.0, 1.0, //third vertex color
    };

    ByteBuffer vertices;
    ByteBuffer colors;
    ByteBuffer indices;
    private void initBuffers(){



        //convert the vertex data arrays into ByteBuffers using a method I created down below
        vertices = Util.storeArrayInBuffer(triangle);
        colors = Util.storeArrayInBuffer(color);
        indices = Util.storeArrayInBuffer(index);

        //VAO: stores pointers to all of the vbos to keep 'em organized
        //VBO: stores data (vertex coordinates, colors, indices, etc.) and a header that contains information about their format

        //tell the GPU to make a single vertex array and store the returned id into the VBO int
        int vao = glGenVertexArrays();

        //set the current vertex array object
        glBindVertexArray(vao);

        //tell the gpu to make a VBO and store its ID in the 'coordVBO' varabile
        int coordVBO = glGenBuffers();

        //bind the 'coordVBO' VBO for use
        glBindBuffer(GL_ARRAY_BUFFER, coordVBO);


        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        //specifies information about the format of the VBO (number of values per vertex, data type, etc.)
        glVertexAttribPointer(0, 2, GL_DOUBLE, false, 0, 0);

        //enable vertex attribute array 0
        glEnableVertexAttribArray(0);

        //create a second VBO for the colors
        int colorVBO = glGenBuffers();

        //bind the 'colorVBO' VBO for use
        glBindBuffer(GL_ARRAY_BUFFER, colorVBO);

        //uploads VBO data (in this case, colors) to the GPU
        glBufferData(GL_ARRAY_BUFFER, colors, GL_DYNAMIC_DRAW);

        //specifies information about the format of the VBO (number of values per vertex, data type, etc.)
        glVertexAttribPointer(1, 3, GL_DOUBLE, false, 0, 0);

        //enable vertex attribute array 1
        glEnableVertexAttribArray(1);

        //create a third VBO for the indices (tells the GPU which vertices to render and when)
        int indicesVBO = glGenBuffers();

        //bind the 'indicesVBO' for use
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);

        //uploads VBO data (in this case, colors) to the GPU
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        //unbind the last bound VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(vao);

    }

    private void compileShaders(){
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // before creating the window

        // ENABLE IF SHADER WON'T COMPILE:
        //GLUtil.setupDebugMessageCallback();

        //load the vertex shader from the file using a method I wrote down below
        int vertexShader = loadShader(new File("src/main/resources/vert.glsl"), GL_VERTEX_SHADER);

        //load the fragment shader from the file using a method I wrote down below
        int fragmentShader = loadShader(new File("src/main/resources/frag.glsl"), GL_FRAGMENT_SHADER);



        //create a program object and store its ID in the 'program' variable
        int program = glCreateProgram();

        //these method calls link shader program variables to attribute locations so that they can be modified in Java code
        glBindAttribLocation(program, 0, "position");
        glBindAttribLocation(program, 1, "color");

        //attach the vertex and fragment shaders to the program
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        //link the program (whatever that does)
        glLinkProgram(program);

        //validate the program to make sure it wont blow up the program
        glValidateProgram(program);

        glUseProgram(program);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.5f, 0.5f, 0.9f, 0.0f);

        //initBuffers();

        QuadBuffer qbuf = new QuadBuffer();
        qbuf.update(index, triangle, color);
        qbuf.bindAndPush();

        int tid = Texture.loadTexture("image.png");

        compileShaders();

        int error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glDrawElements(GL_TRIANGLES, index.length, GL_UNSIGNED_INT, 0);

            glfwSwapBuffers(window); // swap the color buffers


            triangle[0] += 0.001f;
            qbuf.update(index, triangle, color);
            qbuf.bindAndPush();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }



    public static int loadShader(File file, int type) {
        try {
            Scanner sc = new Scanner(file);
            StringBuilder data = new StringBuilder();

            if(file.exists()) {
                while(sc.hasNextLine()) {
                    data.append(sc.nextLine() + "\n");
                }

                sc.close();
            }
            int id = glCreateShader(type);
            glShaderSource(id, data);
            glCompileShader(id);
            return id;
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

}
