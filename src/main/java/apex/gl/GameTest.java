package apex.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class GameTest implements IGameApp {

    private VAOModel testModel;
    private Shader testShader;
    private Camera2D camera2D;

    public static void main(String[] args) {
        Launcher.launch(new GameTest());
    }

    @Override
    public Window createWindow() {
        return new Window("Game", 800, 600);
    }

    @Override
    public void init(GameStore store) {
        store.getWindow().setVsync(false);
        camera2D = new Camera2D(store);

        float[] testModelVertices = new float[]{
                -50f, -50f, 0, 1f, 1f, 1f, 1f, 1, 0,  // bottom right
                50f, 50f, 0, 1f, 1f, 1f, 1f, 0, 1,  // top left
                -50f, 50f, 0, 1f, 1f, 1f, 1f, 1, 1,  // top right
                50f, -50f, 0, 1f, 1f, 1f, 1f, 0, 0,  // bottom left
        };

        int[] testElements = new int[]{
                2, 1, 0,
                0, 1, 3
        };

        testModel = VAOModel.create(store, testModelVertices, testElements,
                VAOModelAttribute.create(0, 3, 9),
                VAOModelAttribute.create(1, 4, 9),
                VAOModelAttribute.create(2, 2, 9)
        );

        testShader = Shader.create(store).
                attach("frag.glsl", ShaderType.FRAGMENT).
                attach("vert.glsl", ShaderType.VERTEX).link();

        Texture testTexture = Texture.create(store, "spritesheet.png");
        testModel.setTexture(testTexture);
    }

    @Override
    public void render(GameStore store) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        testShader.bind();
        testShader.uploadCamera2D("uProjection", "uView", camera2D);

        testShader.uploadTexture("texSample", 0);
        glActiveTexture(GL_TEXTURE0);

        testModel.bind();
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void update(GameStore store, float delta) {
        camera2D.move(1f, 0f);
    }

}
