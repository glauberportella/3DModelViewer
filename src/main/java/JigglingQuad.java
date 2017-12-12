import Useful.Drawable;
import Useful.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class JigglingQuad implements Drawable {
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


    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_COLOURS = 1;
    private final int VBO_INDEX_TEXTURES = 2;

    public JigglingQuad() {
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
    }

    public void update() {
        // Update vertices in the VBO, first bind the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

        // Apply and update vertex data
        for (int i = 0; i < vertices.length; i++) {
            TexturedVertex vertex = vertices[i];

            // Define offset
            float offsetX = (float) (Math.cos(Math.PI * Math.random()) * 0.2);
            float offsetY = (float) (Math.sin(Math.PI * Math.random()) * 0.2);

            // Offset the vertex position
            float[] xyz = vertex.getXYZ();
            vertex.setXYZ(xyz[0] + offsetX, xyz[1] + offsetY, xyz[2]);

            // Put the new data in a ByteBuffer (in the view of a FloatBuffer)
            FloatBuffer vertexFloatBuffer = vertexByteBuffer.asFloatBuffer();
            vertexFloatBuffer.rewind();
            vertexFloatBuffer.put(vertex.getElements());
            vertexFloatBuffer.flip();

            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, i * TexturedVertex.stride, vertexByteBuffer);

            // Restore the vertex data
            vertex.setXYZ(xyz[0], xyz[1], xyz[2]);
        }

        // And of course unbind
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void createShaders() {
        String vertexSource = "#version 150 core\n" +
                "\n" +
                "in vec4 in_Position;\n" +
                "in vec4 in_Color;\n" +
                "\n" +
                "out vec4 pass_Color;\n" +
                "\n" +
                "void main(void) {\n" +
                "   gl_Position = in_Position;\n" +
                "   pass_Color = in_Color;\n" +
                "}";

        String fragmentSource = "#version 150 core\n" +
                "\n" +
                "in vec4 pass_Color;\n" +
                "\n" +
                "out vec4 out_Color;\n" +
                "\n" +
                "void main(void) {\n" +
                "   out_Color = pass_Color;\n" +
                "}";

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexSource);
        GL20.glCompileShader(vertexShader);

        ShaderUtils.checkShaderStatus(vertexShader);

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentSource);
        GL20.glCompileShader(fragmentShader);

        ShaderUtils.checkShaderStatus(fragmentShader);

        // Final steps to use the shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(shaderProgram, 0, "in_Position");
        // Color information will be attribute 1
        GL20.glBindAttribLocation(shaderProgram, 1, "in_Color");

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
        glUseProgram(shaderProgram);

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
        GL20.glUseProgram(0);
    }


}
