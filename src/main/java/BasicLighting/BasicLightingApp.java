package BasicLighting;

import Useful.AppParams;
import Useful.Drawable;
import Useful.GLWrapShaderProgram;
import Useful.ShaderUtils;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL20;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class BasicLightingApp extends GLFWKeyCallback implements Drawable {
    private int shaderProgram;
    private Camera cameraPos;
    private World world;


    public BasicLightingApp(AppParams params) {
        createShaders();
        world = new World();
//        cameraPos = new Camera();
        cameraPos = new Camera();
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

            //-- Input processing
            float rotationDelta = 1.0f;
            float posDelta = 0.1f;

            if (key == GLFW_KEY_UP) cameraPos.rotateUp(rotationDelta);
            else if (key == GLFW_KEY_DOWN) cameraPos.rotateDown(rotationDelta);
            else if (key == GLFW_KEY_LEFT) cameraPos.rotateLeft(rotationDelta);
            else if (key == GLFW_KEY_RIGHT) cameraPos.rotateRight(rotationDelta);
            else if (key == GLFW_KEY_W) cameraPos.moveForward(posDelta);
            else if (key == GLFW_KEY_S) cameraPos.moveBackward(posDelta);
            else if (key == GLFW_KEY_R) cameraPos.moveUp(posDelta);
            else if (key == GLFW_KEY_F) cameraPos.moveDown(posDelta);
            else if (key == GLFW_KEY_A) cameraPos.moveLeft(posDelta);
            else if (key == GLFW_KEY_D) cameraPos.moveRight(posDelta);
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
        Matrix4x4 cameraTranslate = cameraPos.getMatrix();
        Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
//        Matrix4x4 projectionMatrix = Matrix4x4.identity();

//        try (GLWrapShaderProgram shader = new GLWrapShaderProgram(shaderProgram)) {
//            // Upload matrices to the uniform variables
//            int projectionMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "projectionMatrix");
//            int viewMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "viewMatrix");
//
//            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
//            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));
//        }

        world.draw(projectionMatrix, cameraTranslate);
    }
}

