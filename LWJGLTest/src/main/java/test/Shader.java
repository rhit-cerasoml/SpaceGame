package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    protected int handle;
    private int vertex_handle;
    private int fragment_handle;
    public Shader(String vertex_path, String fragment_path){
        vertex_handle = loadShader(new File(vertex_path), GL_VERTEX_SHADER);
        fragment_handle = loadShader(new File(fragment_path), GL_FRAGMENT_SHADER);
        handle = glCreateProgram();

        glAttachShader(handle, vertex_handle);
        glAttachShader(handle, fragment_handle);

        glLinkProgram(handle);

        glValidateProgram(handle);

        glUseProgram(handle);

        bindAttributes();
    }

    protected abstract void bindAttributes();

    public void use(){
        glUseProgram(handle);
    }

    private static int loadShader(File file, int type) {
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
}
