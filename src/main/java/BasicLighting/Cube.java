package BasicLighting;

import Useful.GLWrapShaderProgram;
import Useful.TexturedVertex;
import enterthematrix.Matrix4x4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

class Cube {
    private final int vaoId;
    private Matrix4x4 modelMatrix;
    private final Shader shader;
    private final int VBO_INDEX_VERTICES = 0;

    /**
     * @param transform Used to place model into world. Scale, translate, then rotate.
     */
    public Cube(Matrix4x4 transform, Shader shader) {
        this.modelMatrix = transform;
        this.shader = shader;

        float vertices[] = {
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
        };


        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        // VAO stores how to do an object, and can consist of up to 16 VBOs, which store the real data
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Create a single VBO which will store everything
        int vboId = GL15.glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        glVertexAttribPointer(VBO_INDEX_VERTICES, 3, GL_FLOAT, false, 3 * 4, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Deselect VAO
        GL30.glBindVertexArray(0);

//        int vaoLampId = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(vaoLampId);
//        // we only need to bind to the VBO, the container's VBO's data already contains the correct data.
//        glBindBuffer(GL_ARRAY_BUFFER, vboId);
//        // set the vertex attributes (only position data for our lamp)
//        glVertexAttribPointer(VBO_INDEX_VERTICES, 3, GL_FLOAT, false, 3 * 4, 0);
//        glEnableVertexAttribArray(0);
//        GL30.glBindVertexArray(0);
    }

    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate) {
//        float angle = (float) (glfwGetTime() * 30 % 360);
//        System.out.println(angle);
//        modelMatrix = Matrix4x4.rotateAroundAnyAxis(0 ,0, 1, angle);//.$times(Matrix4x4.translate(0, 0, -0.6f));
//        modelMatrix = Matrix4x4.rotateAroundXAxis(angle);//.$times(Matrix4x4.translate(0, 0, -0.6f));

        try (ShaderUse wrap = new ShaderUse(shader)) {
            // Upload matrices to the uniform variables
            int modelMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "modelMatrix");
            int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
            int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");

            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));

            GL20.glUniformMatrix4fv(modelMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(modelMatrix));

            GL30.glBindVertexArray(vaoId);
            glEnableVertexAttribArray(VBO_INDEX_VERTICES);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

            // Put everything back to default (deselect)
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(VBO_INDEX_VERTICES);
            GL30.glBindVertexArray(0);
        }
    }
}
