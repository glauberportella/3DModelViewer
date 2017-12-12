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
import static org.lwjgl.opengl.GL20.glDeleteShader;

public class QuadWithColours implements Drawable {
    private final int vaoId;
    private final int vboIndicesId;
    private final int vboColoursId;
    private int shaderProgram;

    private final int VAO_INDEX_VERTICES = 0;
    private final int VAO_INDEX_COLOURS = 1;

    public QuadWithColours() {
        createShaders();

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

        // VBO 0 is the vertices
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        // Put the VBO in the attributes list at index 0
        GL20.glVertexAttribPointer(VAO_INDEX_VERTICES, 4, GL11.GL_FLOAT, false, 0, 0);
        // Deselect (bind to 0) the VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // VBO 1 is the colours
        // Create a new VBO for the indices and select it (bind) - COLORS
        vboColoursId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColoursId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorsBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(VAO_INDEX_COLOURS, 4, GL11.GL_FLOAT, false, 0, 0);
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
        GL20.glEnableVertexAttribArray(VAO_INDEX_VERTICES);
        GL20.glEnableVertexAttribArray(VAO_INDEX_COLOURS);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(VAO_INDEX_VERTICES);
        GL20.glDisableVertexAttribArray(VAO_INDEX_COLOURS);
        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);
    }


}
