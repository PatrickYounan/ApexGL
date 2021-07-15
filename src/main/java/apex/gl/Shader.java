package apex.gl;

import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class Shader implements IBindable, IDisposable {

    private final HashMap<String, Integer> uniforms = new HashMap<>();
    private final Queue<Integer> shaders = new ArrayDeque<>();

    private final GameStore store;

    private boolean binded;
    private int programId;

    private Shader(GameStore store) {
        this.store = store;
        // apex.gl.Shader format: apex.gl.Shader.create().attach(source, type).link();
    }

    private Shader load() {
        programId = glCreateProgram();
        bind();
        return this;
    }

    private Shader attachSource(String source, ShaderType shaderType) {
        int shaderId = glCreateShader(shaderType.getValue());
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error attaching shader source: " + glGetShaderInfoLog(shaderId));
        }

        glAttachShader(programId, shaderId);
        shaders.add(shaderId);
        return this;
    }

    public Shader attach(String file, ShaderType shaderType) {
        StringBuilder sb = new StringBuilder();
        URL path = getClass().getClassLoader().getResource(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(path.getPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;
                sb.append(line).append("\n");
            }
            return attachSource(sb.toString(), shaderType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to attach shader file: " + file + ".");
    }

    public int getLocation(String name) {
        if (!uniforms.containsKey(name)) {
            int location = glGetUniformLocation(programId, name);
            uniforms.put(name, location);
            return location;
        }
        return uniforms.get(name);
    }

    public Shader link() {
        glLinkProgram(programId);
        glValidateProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Error linking shader program id: " + glGetProgramInfoLog(programId));

        // Disposes the shaders after we have linked.
        while (!shaders.isEmpty()) glDeleteShader(shaders.poll());

        unbind();
        store.addDisposable(this);
        return this;
    }

    public void uploadInt(String name, int value) {
        glUniform1i(getLocation(name), value);
    }

    public void uploadFloat(String name, float value) {
        glUniform1f(getLocation(name), value);
    }

    public void uploadTexture(String name, int slot) {
        glUniform1i(getLocation(name), slot);
    }

    public void uploadCamera2D(String projection, String view, Camera2D camera) {
        camera.uploadProjection(getLocation(projection));
        camera.uploadView(getLocation(view));
    }

    public static Shader create(GameStore store) {
        return new Shader(store).load();
    }

    @Override
    public void dispose() {
        while (!shaders.isEmpty()) glDeleteShader(shaders.poll());
        glDeleteProgram(programId);
    }

    @Override
    public boolean isBinded() {
        return false;
    }

    @Override
    public void unbind() {
        if (!binded)
            return;
        glUseProgram(0);
        binded = false;
    }

    @Override
    public void bind() {
        if (binded)
            return;
        glUseProgram(programId);
        binded = true;
    }


}
