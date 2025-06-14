package test;

//import the OpenGL classes, Buffer utilities, and GLFW classes
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;

//import all of the necessary OpenGL functions statically so they can be easily accessed
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

//these are some extra imports for handling the shader files and Buffers
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Scanner;


public class Test2 {
    static long window;

    public static void main(String[] args) {

        //start GLFW
        glfwInit();

        //get the screen resolution of the user's monitor
        final int screenWidth = glfwGetVideoMode(glfwGetPrimaryMonitor()).width();
        final int screenHeight = glfwGetVideoMode(glfwGetPrimaryMonitor()).height();

        //set the window to use opengl version 4.5
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);

        //use the core opengl profile, instead of the compatibility one, to make sure you're only using current features
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        //make this program work with newer versions of opengl
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        //turn on window resizing
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        //removes top close bar
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        
        //maximizes window
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        //create a GLFW window and store its id in the window variable
        window = glfwCreateWindow(screenWidth, screenHeight, "GLFW OpenGL Window", 0, 0);


        //enables opengl
        glfwMakeContextCurrent(window);

        //create GLCapabilities instance because it's required (stupid, I know) and use it to print out if OpenGL 4.5 is supported
        System.out.println("OpenGL 4.5 Supported: " + GL.createCapabilities().OpenGL45);

        //make the opengl screen 1600 pixels wide and 900 pixels tall.
        glViewport(0, 0, screenWidth, screenHeight);

        //show the window
        glfwShowWindow(window);





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

        //the order to render the vertices
        int[] index = {
                0,
                1,
                2,
        };

        //convert the vertex data arrays into ByteBuffers using a method I created down below
        ByteBuffer vertices = storeArrayInBuffer(triangle);
        ByteBuffer colors = storeArrayInBuffer(color);
        ByteBuffer indices = storeArrayInBuffer(index);

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
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);

        //specifies information about the format of the VBO (number of values per vertex, data type, etc.)
        glVertexAttribPointer(1, 3, GL_DOUBLE, false, 0, 0);

        //enable vertex attribute array 1
        glEnableVertexAttribArray(1);

        //create a third VBO for the indices (tells the GPU which vertices to render and when)
        int indicesVBO = glGenBuffers();

        //bind the 'indicesVBO' for use
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVBO);

        //uploads VBO data (in this case, colors) to the GPU
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //unbind the last bound VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        //unbind the currently bound VAO
        glBindVertexArray(0);



        int tid = Texture.loadTexture("image.png");

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tid);



        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // before creating the window
        GLUtil.setupDebugMessageCallback();

        //load the vertex shader from the file using a method I wrote down below
        int vertexShader = loadShader(new File("src/main/resources/vert.glsl"), GL_VERTEX_SHADER);

        System.out.println("-----------------");

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

        //check for compilation errors
        System.out.println("Vertex Shader Compiled: " 		+ glGetShaderi(vertexShader, 	GL_COMPILE_STATUS));
        System.out.println("Fragment Shader Compiled: " 	+ glGetShaderi(fragmentShader, 	GL_COMPILE_STATUS));
        System.out.println("Program Linked: " 				+ glGetProgrami(program, 		GL_LINK_STATUS));
        System.out.println("Program Validated: " 			+ glGetProgrami(program, 		GL_VALIDATE_STATUS));

        //check for general OpenGL errors
        int error = glGetError();
        while(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(1);
        }

        //sets the background clear color to white
        glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //get the 'colorMod and 'positionMod' variables so I can change them while drawing to create the animation
        float i = 0.0f;

        //set the current program
        glUseProgram(program);

        error = glGetError();
        if(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(-1);
        }

        while(!glfwWindowShouldClose(window)) {

            //clear the window
            glClear(GL_COLOR_BUFFER_BIT);

            glUseProgram(program);

            //set the current vao to the one we made earlier with all of the data
            glBindVertexArray(vao);

            //draw the current bound VAO/VBO using an index buffer
            glDrawElements(GL_TRIANGLES, index.length, GL_UNSIGNED_INT, 0);

            //unbind the vao if there's another one that will be used, just to get rid of any conflicts
            //glBindVertexArray(0);
            glActiveTexture(GL_TEXTURE0);

            //swap the frame to show the rendered image
            glfwSwapBuffers(window);

            //poll for window events (resize, close, button presses, etc.)
            glfwPollEvents();

            error = glGetError();
            if(error != 0) {
                System.out.println("OpenGL Error: " + error);
                error = glGetError();
            }
        }

        System.out.println("Window closed");

        //loop();

        //disable the vertex attribute arrays
	    glDisableVertexAttribArray(0);
	    glDisableVertexAttribArray(1);

        //delete the vbos and vao
        glDeleteBuffers(coordVBO);
        glDeleteBuffers(colorVBO);
        glDeleteVertexArrays(vao);

        //detach the shaders from the program object
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);

        //delete the shaders now that they are deatched
        glDeleteShader(vertexShader);

        //stop using the shader program
        glUseProgram(0);

        //delete the program now that the shaders are detached and the program isn't being used
        glDeleteProgram(program);

        //check for general OpenGL errors again to make sure the shutdown process worked
        error = glGetError();
        while(error != 0) {
            System.out.println("OpenGL Error: " + error);
            error = glGetError();
            System.exit(1);
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

    public static ByteBuffer storeArrayInBuffer(double[] array) {

        //8 bytes (64-bits) in a double, multiplied by the number of values in the array
        ByteBuffer buffer = BufferUtils.createByteBuffer(array.length * 8);

        for(double i : array) {
            buffer.putDouble(i);
        }

        buffer.position(0);

        return buffer;
    }

    public static ByteBuffer storeArrayInBuffer(int[] array) {

        //8 bytes (64-bits) in a double, multiplied by the number of values in the array
        ByteBuffer buffer = BufferUtils.createByteBuffer(array.length * 4);

        for(int i : array) {
            buffer.putInt(i);
        }

        buffer.position(0);

        return buffer;
    }

    public static void loop(){
        while(true){}
    }
}
