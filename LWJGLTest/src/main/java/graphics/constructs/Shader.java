package graphics.constructs;

import graphics.reload.GPUReloadRegistry;
import graphics.reload.GPUUnloadRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
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

        load();
        use();
        bindAttributes();

        GPUUnloadRegistry.register(e -> {

            unload();
            Window.checkGL("unload " + vertex_path);
        });
        GPUReloadRegistry.register(e -> {

            Window.checkGL("pre-reload " + vertex_path);
            load();
            Window.checkGL("reload " + vertex_path);
            use();
            Window.checkGL("use " + vertex_path);
            bindAttributes();
            Window.checkGL("bind " + vertex_path);
        });
    }

    public void reload(){
        unload();
        load();
    }

    private void unload(){
        use();
        glDetachShader(handle, vertex_handle);
        glDeleteShader(vertex_handle);
        glDetachShader(handle, fragment_handle);
        glDeleteShader(fragment_handle);
        glDeleteProgram(handle);
    }

    private void load(){
        handle = glCreateProgram();

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

    private final HashMap<String, Integer> uniforms = new HashMap<>();
    public int getUniformLocation(String uniformName) {
        if(uniforms.containsKey(uniformName)) return uniforms.get(uniformName);
        int res = glGetUniformLocation(handle, uniformName);
        if(res == -1) throw new RuntimeException("invalid uniform name");
        uniforms.put(uniformName, res);
        return res;
    }
}
