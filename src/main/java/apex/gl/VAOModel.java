package apex.gl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class VAOModel implements Model {

    private Texture texture;
    private int vao, vbo, ebo;

    private boolean binded;

    private VAOModel(GameStore store, float[] vertices, int[] elements, VAOModelAttribute... attributes) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        bind();
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

        int pointer = 0;
        for (VAOModelAttribute attribute : attributes) {
            glVertexAttribPointer(attribute.getIndex(), attribute.getSize(), GL_FLOAT, false, attribute.getStride(), pointer);
            glEnableVertexAttribArray(attribute.getIndex());
            pointer += attribute.getSize() * Float.BYTES;
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        unbind();

        store.addDisposable(this);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void bind() {
        if (binded)
            return;

        if (texture != null)
            texture.bind();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        binded = true;
    }

    @Override
    public void unbind() {
        if (!binded)
            return;

        if (texture != null)
            texture.unbind();

        glBindVertexArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        binded = false;
    }

    @Override
    public void dispose() {
        unbind();
        glDeleteVertexArrays(vao);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
    }

    @Override
    public boolean isBinded() {
        return binded;
    }

    public static VAOModel create(GameStore store, float[] vertices, int[] elements, VAOModelAttribute... attributes) {
        return new VAOModel(store, vertices, elements, attributes);
    }

}

