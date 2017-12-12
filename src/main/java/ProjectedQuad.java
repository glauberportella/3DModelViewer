import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static java.lang.Math.PI;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

class GLWrapShaderProgram implements AutoCloseable {
    public GLWrapShaderProgram(int shaderId) {
        System.out.println("Using shader " + shaderId);
        glUseProgram(shaderId);
    }

    @Override
    public void close() {
        System.out.println("Resetting shader");
        glUseProgram(0);
    }
}

//public class ProjectedQuad extends GLFWKeyCallback implements Drawable {
public class ProjectedQuad implements Drawable {
    private final int vaoId;
    private final int vboId;
    private final int vboIndicesId;
    private int shaderProgram;
    TexturedVertex v0 = new TexturedVertex();
    TexturedVertex v1 = new TexturedVertex();
    TexturedVertex v2 = new TexturedVertex();
    TexturedVertex v3 = new TexturedVertex();
    private TexturedVertex[] vertices = null;
    private ByteBuffer vertexByteBuffer = null;
    private ByteBuffer verticesByteBuffer = null;

    private int projectionMatrixLocation = 0;
    private int viewMatrixLocation = 0;
    private int modelMatrixLocation = 0;
    private Matrix4x4 projectionMatrix = null;
    private Matrix4x4 viewMatrix = null;
    private Matrix4x4 modelMatrix = null;
    private Vector3 modelPos = null;
    private Vector3 modelAngle = null;
    private Vector3 modelScale = null;
    private Vector3 cameraPos = null;
    private FloatBuffer matrix44Buffer = null;

    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_COLOURS = 1;
    private final int VBO_INDEX_TEXTURES = 2;

    public ProjectedQuad(AppParams params) {
        createShaders();

        v0.setXYZ(-0.5f, 0.5f, 0); v0.setRGB(1, 0, 0); v0.setST(0, 0);
        v1.setXYZ(-0.5f, -0.5f, 0); v1.setRGB(0, 1, 0); v1.setST(0, 1);
        v2.setXYZ(0.5f, -0.5f, 0); v2.setRGB(0, 0, 1); v2.setST(1, 1);
        v3.setXYZ(0.5f, 0.5f, 0); v3.setRGB(1, 1, 1); v3.setST(1, 0);

        vertices = new TexturedVertex[] {v0, v1, v2, v3};

        // Create a FloatBufer of the appropriate size for one vertex
        vertexByteBuffer = BufferUtils.createByteBuffer(TexturedVertex.stride);

        // Put each 'Vertex' in one FloatBuffer
        verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * TexturedVertex.stride);
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
        modelPos = new Vector3(0, 0, 0);
        modelAngle = new Vector3(0, 0, 0);
        modelScale = new Vector3(1, 1, 1);
        cameraPos = new Vector3(0, 0, -1);

        setupMatrices(params);
    }

//    @Override
//    public void invoke(long window, int key, int scancode, int action, int mods) {
//        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//        } else {
//
//            //-- Input processing
//            float rotationDelta = 15f;
//            float scaleDelta = 0.1f;
//            float posDelta = 0.1f;
//            Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
//            Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta,
//                    -scaleDelta);
//
//            if (key == GLFW_KEY_UP) modelPos.y += posDelta;
//            else if (key == GLFW_KEY_DOWN) modelPos.y -= posDelta;
//            else if (key == GLFW_KEY_P) Vector3f.add(modelScale, scaleAddResolution, modelScale);
//            else if (key == GLFW_KEY_M) Vector3f.add(modelScale, scaleMinusResolution, modelScale);
//            else if (key == GLFW_KEY_LEFT) modelAngle.z += rotationDelta;
//            else if (key == GLFW_KEY_RIGHT) modelAngle.z -= rotationDelta;
//
//            //-- Update matrices
//            // Reset view and model matrices
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
//        }
//    }

    @Override public void update() {
        float rotationDelta = 15f;
        float scaleDelta = 0.1f;
        float posDelta = 0.1f;

        Vector3 position = new Vector3(0, 0, 1);
        Vector3 origin = Vector3.fill(0);

        Vector3 scaleAddResolution = new Vector3(scaleDelta, scaleDelta, scaleDelta);
        Vector3 scaleMinusResolution = new Vector3(-scaleDelta, -scaleDelta, -scaleDelta);

//            if (key == GLFW_KEY_UP) modelPos.y += posDelta;
//            else if (key == GLFW_KEY_DOWN) modelPos.y -= posDelta;
//            else if (key == GLFW_KEY_P) Vector3f.add(modelScale, scaleAddResolution, modelScale);
//            else if (key == GLFW_KEY_M) Vector3f.add(modelScale, scaleMinusResolution, modelScale);
//            else if (key == GLFW_KEY_LEFT) modelAngle.z += rotationDelta;
//            else if (key == GLFW_KEY_RIGHT) modelAngle.z -= rotationDelta;

        //-- Update matrices
        // Reset view and model matrices
        viewMatrix = Matrix4x4.identity();
        modelMatrix = Matrix4x4.identity();

//        // Translate camera
//        cameraPos = Matrix4x4.forTranslation(
//        Matrix4x4.translate(cameraPos, viewMatrix, viewMatrix);
//
//        // Scale, translate and rotate model
//        Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
//        Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
//        Matrix4f.rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1),
//                modelMatrix, modelMatrix);
//        Matrix4f.rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0),
//                modelMatrix, modelMatrix);
//        Matrix4f.rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0),
//                modelMatrix, modelMatrix);

        // Upload matrices to the uniform variables
        GL20.glUseProgram(shaderProgram);



//        projectionMatrix.store(matrix44Buffer);
//        matrix44Buffer.flip();
//        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
//        viewMatrix.store(matrix44Buffer);
//        matrix44Buffer.flip();
//        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
//        modelMatrix.store(matrix44Buffer);
//        matrix44Buffer.flip();
//        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);

        GL20.glUseProgram(0);
    }

    private float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    private float degreesToRadians(float degrees) {
        return degrees * (float)(PI / 180d);
    }

    private void setupMatrices(AppParams params) {
        // Setup projection enterthematrix
        float fieldOfView = params.fovDegrees;
        float aspectRatio = (float)params.widthPixels / (float)params.heightPixels;
        float near_plane = 0.1f;
        float far_plane = 100f;

        float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        projectionMatrix = new Matrix4x4(x_scale, 0,0, 0,
                0, y_scale, 0, 0,
                0, 0, -((far_plane + near_plane) / frustum_length), -1,
                0, 0, -((2 * near_plane * far_plane) / frustum_length), 0);

//        projectionMatrix.m00 = x_scale;
//        projectionMatrix.m11 = y_scale;
//        projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
//        projectionMatrix.m23 = -1;
//        projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
//        projectionMatrix.m33 = 0;

        // Setup view enterthematrix
        viewMatrix = Matrix4x4.identity();

        // Setup model enterthematrix
        modelMatrix = Matrix4x4.identity();

        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
    }

    private void createShaders() {
        // Load the vertex shader
        int vertexShader = ShaderUtils.loadShader(this.getClass().getResource("shaders/projected_quad_vertex.glsl"), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fragmentShader = ShaderUtils.loadShader(this.getClass().getResource("shaders/projected_quad_fragment.glsl"), GL20.GL_FRAGMENT_SHADER);

        // Get matrices uniform locations
        projectionMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "projectionMatrix");
        viewMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "viewMatrix");
        modelMatrixLocation = GL20.glGetUniformLocation(shaderProgram, "modelMatrix");

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

    public void draw() {
        try (GLWrapShaderProgram shader = new GLWrapShaderProgram(shaderProgram)) {
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
