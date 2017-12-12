package ProjectedQuad;

import Useful.GLWrapShaderProgram;
import Useful.AppParams;
import Useful.Drawable;
import Useful.ShaderUtils;
import enterthematrix.Matrix4x4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class ProjectedQuad extends GLFWKeyCallback implements Drawable {
    private int shaderProgram;

    private CameraRotatingAroundOrigin cameraPos;
    private ArrayList<Model> models;


    public ProjectedQuad(AppParams params) {
        createShaders();

        models = new ArrayList<>();

        // Draw quad1, putting it back along the z-axis a bit.
        Matrix4x4 quad1Transform = Matrix4x4.translate(0, 0.5f, -0.6f).$times(Matrix4x4.rotateAroundZAxis(45));
        Model quad1 = new Model(quad1Transform);

        // Draw quad2, putting it back along the z-axis a bit more and off to the right.
        Matrix4x4 quad2Transform = Matrix4x4.translate(0.2f, 0.5f, -0.8f);//.$times(Matrix4x4.rotateAroundXAxis(90));
        Model quad2 = new Model(quad2Transform);

        // Draw floor
        Matrix4x4 floorTransform = Matrix4x4.rotateAroundAnyAxis(1, 0, 0, 89).$times(Matrix4x4.translate(0, 0, -0.8f));//.$times(Matrix4x4.scale(3));
        Model floor = new Model(floorTransform);

        models.add(quad1);
        models.add(quad2);
//        models.add(floor);

        cameraPos = new CameraRotatingAroundOrigin();
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


//        return new Matrix4x4(x_scale, 0,0, 0,
//                0, y_scale, 0, 0,
//                0, 0, -((far_plane + near_plane) / frustum_length), -1,
//                0, 0, -((2 * near_plane * far_plane) / frustum_length), 0);
//        return Matrix4x4.identity();
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
        // Load the vertex shader
        int vertexShader = ShaderUtils.loadShader(this.getClass().getResource("../shaders/projected_quad_vertex.glsl"), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fragmentShader = ShaderUtils.loadShader(this.getClass().getResource("../shaders/projected_quad_fragment.glsl"), GL20.GL_FRAGMENT_SHADER);

        // Final steps to use the shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(shaderProgram, 0, "in_Position");
        // Color information will be attribute 1
        GL20.glBindAttribLocation(shaderProgram, 1, "in_Color");
        // Texture information will be attribute 2
        GL20.glBindAttribLocation(shaderProgram, 2, "in_TextureCoord");

        glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == 0) {
            String error = glGetProgramInfoLog(shaderProgram);
            System.err.println("Failed to link shader: " + error);
        }

        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    @Override public void draw(AppParams params) {
        Matrix4x4 cameraTranslate = cameraPos.getMatrix();
        Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
//        Matrix4x4 projectionMatrix = Matrix4x4.identity();

        try (GLWrapShaderProgram shader = new GLWrapShaderProgram(shaderProgram)) {
            // Upload matrices to the uniform variables
            // Get matrices uniform locations
            int projectionMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "projectionMatrix");
            int viewMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "viewMatrix");

            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));
        }

        models.stream().forEach(model -> model.draw(shaderProgram));

    }


}
