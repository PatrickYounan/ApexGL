package apex.gl;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

/**
 * @author Patrick Younan
 * @date Created on 14/07/2021 using IntelliJ IDEA.
 */
public final class Camera2D implements ICamera, IResized {

    private final FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);

    private final Vector3f frontPosition = new Vector3f(Vector.FRONT);
    private final Vector3f position = new Vector3f();

    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();

    private int width;
    private int height;

    public Camera2D(GameStore store) {
        store.addResizeHook(this);
        this.width = store.getWidth();
        this.height = store.getHeight();
        this.position.z = 20f;
        adjustOrthoProjection();
    }

    private void adjustOrthoProjection() {
        projection.identity();
        projection.ortho(0, width, height, 0, 0f, 100f);
    }

    public void uploadProjection(int location) {
        projectionBuffer.clear();
        projection.get(projectionBuffer);
        glUniformMatrix4fv(location, false, projectionBuffer);
    }

    public void uploadView(int location) {
        viewBuffer.clear();
        getView().get(viewBuffer);
        glUniformMatrix4fv(location, false, viewBuffer);
    }

    public void move(float x, float y) {
        position.add(x, y, 0);
    }

    @Override
    public void resize(int width, int height) {
        adjustOrthoProjection();
        this.width = width;
        this.height = height;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getProjection() {
        return projection;
    }

    public Matrix4f getView() {
        frontPosition.set(Vector.FRONT);
        view.identity();
        view.lookAt(position, frontPosition.add(position.x, position.y, 0f), Vector.UP);
        return view;
    }

}
