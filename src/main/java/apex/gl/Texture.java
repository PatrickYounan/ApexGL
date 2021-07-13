package apex.gl;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * @author Patrick Younan
 * @date Created on 12/07/2021 using IntelliJ IDEA.
 */
public final class Texture implements IDisposable {

    private final int textureId;
    private final int width;
    private final int height;
    private final int channels;

    private Texture(GameStore store, String path) {
        this.textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        stbi_set_flip_vertically_on_load(true);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        this.width = width.get();
        this.height = height.get();
        this.channels = channels.get();

        ByteBuffer buffer = stbi_load(ClassLoader.getSystemResource(path).getPath(), width, height, channels, 0);
        if (buffer == null)
            return;
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(buffer);
        store.addDisposable(this);
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0); // temporary.
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

    public int getChannels() {
        return channels;
    }

    @Override
    public void dispose() {
        glDeleteTextures(textureId);
    }

    public static Texture create(GameStore store, String path) {
        return new Texture(store, path);
    }

}
