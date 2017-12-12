package ProjectedQuad;

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
     * @param transform Used to place model into world. Scale, translate, then rotate.
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
        GL20.glVertexAttribPointer(VBO_INDEX_VERTICES, TexturedVertex.positionElementCount, GL11.GL_FLOAT, false,
                TexturedVertex.stride, TexturedVertex.positionByteOffset);
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
