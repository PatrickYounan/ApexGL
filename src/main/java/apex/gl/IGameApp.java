package apex.gl;

/**
 * @author Patrick Younan
 * @date Created on 11/07/2021 using IntelliJ IDEA.
 */
public interface IGameApp {

    void init(GameStore store);

    void render(GameStore store);

    void update(GameStore store, float delta);

    Window createWindow();
}
