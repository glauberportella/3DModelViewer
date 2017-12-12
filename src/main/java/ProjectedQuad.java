import GLWrap.GLWrapShaderProgram;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static java.lang.Math.PI;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

class CameraPosition {
    private Vector4 origin = Vector4.fill(0);
    private float rotationLeftRightAngleDegrees = 0.0f;
    private float rotationTopBottomAngleDegrees = 0.0f;

    public void moveUp(float v) {
        origin = origin.copy(origin.x(), origin.y() + v, origin.z(), origin.w());
    }
    public void moveDown(float v) {
        moveUp(v * -1);
    }
    public void moveRight(float v) {
        origin = origin.copy(origin.x() + v, origin.y(), origin.z(), origin.w());
    }
    public void moveLeft(float v) {
        moveRight(v * -1);
    }
    public void moveForward(float v) {
        origin = origin.copy(origin.x(), origin.y(), origin.z() + v, origin.w());
    }
    public void moveBackward(float v) {
        moveForward(v * -1);
    }
    public void rotateRight(float v) {
        rotationLeftRightAngleDegrees += v;
//        Matrix4x4 rotation = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotation.$times(rotationAxis);
    }
    public void rotateLeft(float v) {
        rotateRight(v * -1);
    }
    public void rotateUp(float v) {
        rotationTopBottomAngleDegrees += v;
//        Matrix4x4 rotation = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotation.$times(rotationAxis);
    }
    public void rotateDown(float v) {
        rotateUp(v * -1);
    }

    public Matrix4x4 getMatrix() {
        Vector4 yAxis = new Vector4(0, 1, 0, 1);
        Vector4 xAxis = new Vector4(1, 0, 0, 1);
        Matrix4x4 rotationLeftRight = Matrix4x4.rotateAroundAnyAxis(yAxis, rotationLeftRightAngleDegrees);
        Matrix4x4 rotationTopBottom = Matrix4x4.rotateAroundAnyAxis(xAxis, rotationTopBottomAngleDegrees);
        return Matrix4x4.translate(origin).$times(rotationLeftRight).$times(rotationTopBottom);
    }

}

class Model {
    private final int vaoId;
    private final int vboId;
    private final int vboIndicesId;
    TexturedVertex v0 = new TexturedVertex();
    TexturedVertex v1 = new TexturedVertex();
    TexturedVertex v2 = new TexturedVertex();
    TexturedVertex v3 = new TexturedVertex();
    private TexturedVertex[] vertices = null;
//    private ByteBuffer vertexByteBuffer = null;
//    private ByteBuffer verticesByteBuffer = null;
//    private Vector3 modelPos = null;
//    private Vector3 modelAngle = null;
//    private Vector3 modelScale = null;
    private Matrix4x4 modelMatrix;

    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_COLOURS = 1;
    private final int VBO_INDEX_TEXTURES = 2;

    /**
     * @param transform Used to place model into world
     */
    public Model(Matrix4x4 transform) {
        this.modelMatrix = transform;

        v0.setXYZ(-0.5f, 0.5f, 0f); v0.setRGB(1, 0, 0); v0.setST(0, 0);
        v1.setXYZ(-0.5f, -0.5f, 0f); v1.setRGB(0, 1, 0); v1.setST(0, 1);
        v2.setXYZ(0.5f, -0.5f, 0f); v2.setRGB(0, 0, 1); v2.setST(1, 1);
        v3.setXYZ(0.5f, 0.5f, 0f); v3.setRGB(1, 1, 1); v3.setST(1, 0);

        vertices = new TexturedVertex[] {v0, v1, v2, v3};

        // Create a FloatBufer of the appropriate size for one vertex
        ByteBuffer vertexByteBuffer = BufferUtils.createByteBuffer(TexturedVertex.stride);

        // Put each 'Vertex' in one FloatBuffer
        ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * TexturedVertex.stride);
        FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesFloatBuffer.put(vertices[i].getElements());
        }
        verticesFloatBuffer.flip();

        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
                // Left bottom triangle
                0, 1, 2,
                // Right top triangle
                2, 3, 0
        };
        int indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        float[] colors = {
                1f, 0f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 0f, 1f, 1f,
                1f, 1f, 1f, 1f,
        };
        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors);
        colorsBuffer.flip();



        // VAO stores how to do an object, and can consist of up to 16 VBOs, which store the real data
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        // Create a single VBO which will store everything
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);
        GL20.glVertexAttribPointer(VBO_INDEX_VERTICES, TexturedVertex.positionElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.positionByteOffset);
        GL20.glVertexAttribPointer(VBO_INDEX_COLOURS, TexturedVertex.colorElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.colorByteOffset);
        GL20.glVertexAttribPointer(VBO_INDEX_TEXTURES, TexturedVertex.textureElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride, TexturedVertex.textureByteOffset);
        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Deselect VAO
        GL30.glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind)
        // Note this isn't bound into the VAO. Not sure why for this example.
        vboIndicesId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Set the default quad rotation, scale and position values
//        modelPos = new Vector3(0, 0, 0);
//        modelAngle = new Vector3(0, 0, 0);
//        modelScale = new Vector3(1, 1, 1);
    }

    public void draw(int shaderProgram) {
//        float angle = (float) (glfwGetTime() * 30 % 360);
//        System.out.println(angle);
//        modelMatrix = Matrix4x4.rotateAroundAnyAxis(0 ,0, 1, angle);//.$times(Matrix4x4.translate(0, 0, -0.6f));
//        modelMatrix = Matrix4x4.rotateAroundXAxis(angle);//.$times(Matrix4x4.translate(0, 0, -0.6f));

        try (GLWrapShaderProgram shader = new GLWrapShaderProgram(shaderProgram)) {
            int modelMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "modelMatrix");

            GL20.glUniformMatrix4fv(modelMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(modelMatrix));


            int indicesCount = 6;

            // Bind to the VAO that has all the information about the quad vertices
            GL30.glBindVertexArray(vaoId);
            GL20.glEnableVertexAttribArray(VBO_INDEX_VERTICES);
            GL20.glEnableVertexAttribArray(VBO_INDEX_COLOURS);

            // Bind to the index VBO that has all the information about the order of the vertices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);

            // Draw the vertices
            GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

            // Put everything back to default (deselect)
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(VBO_INDEX_VERTICES);
            GL20.glDisableVertexAttribArray(VBO_INDEX_COLOURS);
            GL30.glBindVertexArray(0);
        }
    }
}

public class ProjectedQuad extends GLFWKeyCallback implements Drawable {
    private int shaderProgram;

    private CameraPosition cameraPos = null;
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
        Matrix4x4 floorTransform = Matrix4x4.rotateAroundAnyAxis(1, 0, 0, 89).$times(Matrix4x4.scale(3));
        Model floor = new Model(floorTransform);

        models.add(quad1);
        models.add(quad2);
        models.add(floor);

        cameraPos = new CameraPosition();
    }

    public Matrix4x4 createProjectionMatrix(AppParams params) {
        float fieldOfView = params.fovDegrees;
        float aspectRatio = (float)params.widthPixels / (float)params.heightPixels;
        float near_plane = 0.001f;
        float far_plane = 100f;

        float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

//        return new Matrix4x4(x_scale, 0,0, 0,
//                0, y_scale, 0, 0,
//                0, 0, -((far_plane + near_plane) / frustum_length), -1,
//                0, 0, -((2 * near_plane * far_plane) / frustum_length), 0);
        return Matrix4x4.identity();
    }


    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        } else {

            //-- Input processing
            float rotationDelta = 0.1f;
            float scaleDelta = 0.1f;
            float posDelta = 0.1f;
//            Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
//            Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta,
//                    -scaleDelta);

            if (key == GLFW_KEY_UP) cameraPos.rotateUp(rotationDelta);
            else if (key == GLFW_KEY_DOWN) cameraPos.rotateDown(rotationDelta);
            else if (key == GLFW_KEY_LEFT) cameraPos.rotateLeft(rotationDelta);
            else if (key == GLFW_KEY_RIGHT) cameraPos.rotateRight(rotationDelta);
            else if (key == GLFW_KEY_W) cameraPos.moveForward(posDelta);
            else if (key == GLFW_KEY_S) cameraPos.moveBackward(posDelta);
            else if (key == GLFW_KEY_A) cameraPos.moveLeft(posDelta);
            else if (key == GLFW_KEY_D) cameraPos.moveRight(posDelta);

            //-- Update matrices
            // Reset view and model matrices
//            viewMatrix = new Matrix4f();
//            modelMatrix = new Matrix4f();
//
//            // Translate camera
//            Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
//
//            // Scale, translate and rotate model
//            Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
//            Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
//            Matrix4f.rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1),
//                    modelMatrix, modelMatrix);
//            Matrix4f.rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0),
//                    modelMatrix, modelMatrix);
//            Matrix4f.rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0),
//                    modelMatrix, modelMatrix);
//
//            // Upload matrices to the uniform variables
//            GL20.glUseProgram(pId);
//
//            projectionMatrix.store(matrix44Buffer);
//            matrix44Buffer.flip();
//            GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
//            viewMatrix.store(matrix44Buffer);
//            matrix44Buffer.flip();
//            GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
//            modelMatrix.store(matrix44Buffer);
//            matrix44Buffer.flip();
//            GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
//
//            GL20.glUseProgram(0);
        }
    }

//    @Override public void update() {
//        //-- Update matrices
//        // Reset view and model matrices
////        Matrix4x4 = modelMatrix = Matrix4x4.identity();
//    }

    private float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    private float degreesToRadians(float degrees) {
        return degrees * (float)(PI / 180d);
    }

    private void createShaders() {
        // Load the vertex shader
        int vertexShader = ShaderUtils.loadShader(this.getClass().getResource("shaders/projected_quad_vertex.glsl"), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fragmentShader = ShaderUtils.loadShader(this.getClass().getResource("shaders/projected_quad_fragment.glsl"), GL20.GL_FRAGMENT_SHADER);

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
        Matrix4x4 projectionMatrix = createProjectionMatrix(params);

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
