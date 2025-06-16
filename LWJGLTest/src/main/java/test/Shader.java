package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    protected int handle;

    private String vertex_path;
    private int vertex_handle;

    private String fragment_path;
    private int fragment_handle;
    public Shader(String vertex_path, String fragment_path){
        this.vertex_path = vertex_path;
        this.fragment_path = fragment_path;

        handle = glCreateProgram();

        load();
        use();
        bindAttributes();
    }

    public void reload(){
        System.out.println("reloading shader!");
        use();
        glDetachShader(handle, vertex_handle);
        glDeleteShader(vertex_handle);

        glDetachShader(handle, fragment_handle);
        glDeleteShader(fragment_handle);
        load();
    }

    private void load(){
        vertex_handle = loadShader(new File(vertex_path), GL_VERTEX_SHADER);
        fragment_handle = loadShader(new File(fragment_path), GL_FRAGMENT_SHADER);

        glAttachShader(handle, vertex_handle);
        glAttachShader(handle, fragment_handle);

        glLinkProgram(handle);

        glValidateProgram(handle);
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
