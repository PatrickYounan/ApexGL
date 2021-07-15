package apex.gl;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Patrick
 */
public final class Window {

    static final int DEFAULT_WIDTH = 800;
    static final int DEFAULT_HEIGHT = 600;

    private final List<IResized> resizedList = new ArrayList<>();

    private double frameCap = 60;

    private long window;

    private boolean vsync = true;
    private String title;
    private int height;
    private int width;

    public Window(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public Window() {
        this("Apex Application", DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if (System.getProperty("os.name").contains("OS X")) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });
        GLFWVidMode video = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));
        glfwSetWindowPos(window, (video.width() - width) / 2, (video.height() - height) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync ? 1 : 0);
        glfwShowWindow(window);

        createCapabilities();
        glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
            glViewport(0, 0, w, h);
            this.width = w;
            this.height = h;
        });
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            for (int i = 0; i < resizedList.size(); i++) {
                IResized resized = resizedList.get(i);
                if (resized == null)
                    continue;
                resized.resize(this.width, this.height);
            }
        });

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glViewport(0, 0, width, height);
        glClearColor(0f, 0f, 0f, 0f);
    }

    public void terminate() {
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void addResizeHook(IResized resized) {
        resizedList.add(resized);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public boolean isOpen() {
        return !glfwWindowShouldClose(window);
    }

    public void setFrameCap(double frameCap) {
        this.frameCap = frameCap;
    }

    public double getFrameCap() {
        return frameCap;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public boolean isVsync() {
        return vsync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }
}
