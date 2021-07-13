package apex;

import apex.gl.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class TestApplication implements IGameApp {

    private VAOModel testModel;
    private Shader testShader;

    public static void main(String[] args) {
        Launcher.launch(new TestApplication());
    }

    @Override
    public Window createWindow() {
        return new Window("Game", 800, 600);
    }

    @Override
    public void update(GameStore store, double delta) {

    }

    @Override
    public void init(GameStore store) {
        store.getWindow().setVsync(false);

        float[] testModelVertices = new float[]{
                -0.5f, 0.5f, 0.0f,  // top right
                -0.5f, -0.5f, 0.0f,  // bottom right
                0.5f, -0.5f, 0.0f,  // bottom left
                0.5f, 0.5f, 0.0f   // top left
        };

        int[] testElements = new int[]{0, 1, 3, 3, 1, 2};

        testModel = VAOModel.create(store, testModelVertices, testElements,
                VAOModelAttribute.create(0, 3, 3)
        );

        testShader = Shader.create(store).attach("frag.glsl", ShaderType.FRAGMENT).attach("vert.glsl", ShaderType.VERTEX).link();
    }

    @Override
    public void render(GameStore store) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        testShader.bind();
        testModel.bind();
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

}
