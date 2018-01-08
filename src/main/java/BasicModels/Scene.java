package BasicModels;

import Useful.AppParams;
import Useful.HandyMaths;
import enterthematrix.Matrix4x4;

import static org.lwjgl.glfw.GLFW.*;

interface Scene extends BlipHandler {
    default Matrix4x4 createPerspectiveProjectionMatrix(AppParams params) {
        return SceneUtils.createPerspectiveProjectionMatrix(params);
    }

    default public void keyPressed(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        } else {
            keyPressedImpl(window, key, scancode, action, mods);
        }
    }

    abstract void keyPressedImpl(long window, int key, int scancode, int action, int mods);
    abstract public void draw(AppParams params);
    default public void doOneTick() {
    }
}

class SceneUtils {
    static public Matrix4x4 createPerspectiveProjectionMatrix(AppParams params) {
        float fieldOfView = params.fovDegrees;
        float aspectRatio = (float)params.widthPixels / (float)params.heightPixels;
        float near_plane = 0.001f;
        float far_plane = 1000f;

        float y_scale = HandyMaths.coTangent(HandyMaths.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        return new Matrix4x4(
                x_scale, 0, 0, 0,
                0, y_scale, 0, 0,
                0, 0, - ((far_plane + near_plane) / frustum_length), -((2 * far_plane * near_plane)) / frustum_length,
                0, 0, -1, 0);
    }

    static public Matrix4x4 createOrthoProjectionMatrix(float left, float right, float top, float bottom, float near, float far) {
        Matrix4x4 m = new Matrix4x4(2.0f / (right - left), 0.0f, 0.0f, 0.0f,
                0.0f,
                2.0f / (top - bottom),
                0.0f,
                0.0f,

                0.0f,
                0.0f,
                -2.0f / (far - near),
                0.0f,

                -(right + left) / (right - left),
                -(top + bottom) / (top - bottom),
                -(far + near) / (far - near),
                1.0f
        );

        return m;
    }

}