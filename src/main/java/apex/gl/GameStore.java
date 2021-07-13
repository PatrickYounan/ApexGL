package apex.gl;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public final class GameStore {

    private final Queue<IDisposable> disposables = new ArrayDeque<>();

    private final Window window;

    private int frames;

    protected GameStore(Window window) {
        this.window = window;
    }

    public void cleanup() {
        while (!disposables.isEmpty()) disposables.poll().dispose();
    }

    public int getWidth() {
        return window.getWidth();
    }

    public int getHeight() {
        return window.getHeight();
    }

    public String getTitle() {
        return window.getTitle();
    }

    public void addDisposable(IDisposable disposable) {
        disposables.add(disposable);
    }

    public void addDisposables(IDisposable... disposables) {
        for (IDisposable dispose : disposables) addDisposable(dispose);
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public int getFrames() {
        return frames;
    }

    public Window getWindow() {
        return window;
    }

}
