import Useful.Drawable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class QuadWithElements implements Drawable {
    private final int vaoId;
    private final int vboIndicesId;

    public QuadWithElements() {
        // Create a new Vertex Buffer Object in memory and select it (bind)
        // A VBO is a collection of Vectors which in this case resemble the location of each vertex.
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        float[] vertices = {
                -0.5f, 0.5f, 0f, 1f,    // Left top         ID: 0
                -0.5f, -0.5f, 0f, 1f,   // Left bottom      ID: 1
                0.5f, -0.5f, 0f, 1f,    // Right bottom     ID: 2
                0.5f, 0.5f, 0f, 1f  // Right left       ID: 3
        };

        // Sending data to OpenGL requires the usage of (flipped) byte buffers
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

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

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        // Put the VBO in the attributes list at index 0
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Create a new VBO for the indices and select it (bind)
        vboIndicesId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);
    }

    public void draw() {
        int indicesCount = 6;

        // Bind to the VAO that has all the information about the quad vertices
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


}
