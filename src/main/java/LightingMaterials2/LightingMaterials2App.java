package LightingMaterials2;

import Useful.AppParams;
import Useful.Drawable;
import Useful.HandyMaths;
import enterthematrix.Matrix4x4;
import org.lwjgl.glfw.GLFWKeyCallback;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;

public class LightingMaterials2App extends GLFWKeyCallback implements Drawable {
    private int shaderProgram;
    private World world;


    public LightingMaterials2App(AppParams params) {
        createShaders();
        world = new World();
//        cameraPos = new Camera();
    }

    private Matrix4x4 createPerspectiveProjectionMatrix(AppParams params) {
        float fieldOfView = params.fovDegrees;
        float aspectRatio = (float)params.widthPixels / (float)params.heightPixels;
        float near_plane = 0.001f;
        float far_plane = 10f;

        float y_scale = HandyMaths.coTangent(HandyMaths.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        return new Matrix4x4(
                x_scale, 0, 0, 0,
                0, y_scale, 0, 0,
                0, 0, - ((far_plane + near_plane) / frustum_length), -((2 * far_plane * near_plane)) / frustum_length,
                0, 0, -1, 0);
    }



    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        } else {
            world.invoke(window, key, scancode, action, mods);
        }
    }


    private void createShaders() {

    }

    @Override public void draw(AppParams params) {
        Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
//        Matrix4x4 projectionMatrix = Matrix4x4.identity();
        world.draw(projectionMatrix);
    }
}

