package LightingMaterials;

import Useful.AppParams;
import Useful.Drawable;
import enterthematrix.Matrix4x4;
import org.lwjgl.glfw.GLFWKeyCallback;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;

public class BasicLighting2App extends GLFWKeyCallback implements Drawable {
    private int shaderProgram;
    private World world;


    public BasicLighting2App(AppParams params) {
        createShaders();
        world = new World();
//        cameraPos = new Camera();
    }

    private Matrix4x4 createPerspectiveProjectionMatrix(AppParams params) {
        float fieldOfView = params.fovDegrees;
        float aspectRatio = (float)params.widthPixels / (float)params.heightPixels;
        float near_plane = 0.001f;
        float far_plane = 10f;

        float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
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


    private float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    private float degreesToRadians(float degrees) {
        return degrees * (float)(PI / 180d);
    }

    private void createShaders() {

    }

    @Override public void draw(AppParams params) {
        Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
//        Matrix4x4 projectionMatrix = Matrix4x4.identity();
        world.draw(projectionMatrix);
    }
}

