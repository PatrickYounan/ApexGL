package apex.gl;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class Launcher {

    /**
     * A static function used to launch a {@link IGameApp}.
     *
     * @param app {@link IGameApp} to launch.
     */
    public static void launch(IGameApp app) {
        Window window = app.createWindow();
        window.init();

        GameStore store = new GameStore(window);
        app.init(store);

        long initialTime = System.nanoTime();

        final double updateTime = 1000000000 / window.getFrameCap();
        final double renderTime = 1000000000 / window.getFrameCap();

        double updateDelta = 0;
        double updateFrames = 0;

        int frames = 0;
        int ticks = 0;

        long timer = System.currentTimeMillis();

        while (window.isOpen()) {
            long currentTime = System.nanoTime();
            updateDelta += (currentTime - initialTime) / updateTime;
            updateFrames += (currentTime - initialTime) / renderTime;
            initialTime = currentTime;

            if (!window.isVsync()) {
                if (updateDelta >= 1) {
                    glfwPollEvents();
                    app.update(store, (float) updateDelta);
                    ticks++;
                    updateDelta--;
                }
                if (updateFrames >= 1) {
                    app.render(store);
                    window.swapBuffers();
                    frames++;
                    updateFrames--;
                }
            } else {
                glfwPollEvents();
                app.update(store, (float) updateFrames);
                ticks++;

                app.render(store);
                window.swapBuffers();
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                System.out.printf("UPS: %s, FPS: %s, VSYNC: %s%n", ticks, frames, window.isVsync());
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }

        store.cleanup();
        window.terminate();
    }

}
