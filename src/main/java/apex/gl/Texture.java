package apex.gl;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * @author Patrick Younan
 * @date Created on 12/07/2021 using IntelliJ IDEA.
 */
public final class Texture implements IDisposable {

    private final int textureId;

    private int width;
    private int height;

    private Texture(GameStore store, String path) {
        this.textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.callocInt(1);
            IntBuffer height = stack.callocInt(1);
            IntBuffer channels = stack.callocInt(1);

            stbi_set_flip_vertically_on_load(true);

            ByteBuffer buffer = stbi_load(path, width, height, channels, 0);
            Objects.requireNonNull(buffer, "Unable to load texture from path: " + path);

            this.width = width.get();
            this.height = height.get();

            switch (channels.get()) {
            case 3:
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
                break;
            case 4:
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
                break;
            default:
                throw new RuntimeException("Unable to load texture due to unknown way of handling channel size: " + channels.get(0));
            }
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        store.addDisposable(this);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public void dispose() {
        glDeleteTextures(textureId);
    }

    public static Texture create(GameStore store, String path) {
        return new Texture(store, path);
    }

}
