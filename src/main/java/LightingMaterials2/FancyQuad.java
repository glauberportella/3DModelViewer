package LightingMaterials2;

import LightingMaterials2.Shaders.Shader;
import LightingMaterials2.Shaders.ShaderUse;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

class FancyQuad extends Model {
    private final int vaoId;
    private final Texture texture, specularMap;

//    private final Shader shader;
    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_NORMALS = 1;
    private final int VBO_INDEX_DIFFUSE_MAP = 2;
    private final Material material;


    public FancyQuad(Vector4 pos, Optional<Matrix4x4> scale, Optional<Matrix4x4> rotate, Material material, Texture texture, Texture specularMap, float textureRepeats) {
        super(pos, scale, rotate);
//        this.shader = shader;
        this.material = material;
        this.texture = texture;
        this.specularMap = specularMap;

        float tc = textureRepeats;

        float vertices[] = {
                // positions          // normals           // texture coords
                -0.5f, -0.5f, 0f,  0.0f,  0.0f, 1.0f,  0.0f, 0.0f,
                0.5f, -0.5f, 0f,  0.0f,  0.0f, 1.0f,  tc, 0.0f,
                0.5f,  0.5f, 0,  0.0f,  0.0f, 1.0f,  tc, tc,
                0.5f,  0.5f, 0,  0.0f,  0.0f, 1.0f,  tc, tc,
                -0.5f,  0.5f, 0,  0.0f,  0.0f, 1.0f,  0.0f, tc,
                -0.5f, -0.5f, 0,  0.0f,  0.0f, 1.0f,  0.0f, 0.0f,

//                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,
//                0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 0.0f,
//                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
//                0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   1.0f, 1.0f,
//                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 1.0f,
//                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,   0.0f, 0.0f,
//
//                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
//                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
//                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
//                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
//                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
//                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
//
//                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
//                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 1.0f,
//                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
//                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 1.0f,
//                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  0.0f, 0.0f,
//                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,  1.0f, 0.0f,
//
//                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,
//                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 1.0f,
//                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
//                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  1.0f, 0.0f,
//                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 0.0f,
//                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,  0.0f, 1.0f,
//
//                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f,
//                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 1.0f,
//                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
//                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  1.0f, 0.0f,
//                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 0.0f,
//                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,  0.0f, 1.0f
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
        glEnableVertexAttribArray(VBO_INDEX_VERTICES);
        glVertexAttribPointer(VBO_INDEX_VERTICES, 3, GL_FLOAT, false, 8 * 4, 0);
        // normal attribute
        glEnableVertexAttribArray(VBO_INDEX_NORMALS);
        glVertexAttribPointer(VBO_INDEX_NORMALS, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
        // texture attribute
        glEnableVertexAttribArray(VBO_INDEX_DIFFUSE_MAP);
        glVertexAttribPointer(VBO_INDEX_DIFFUSE_MAP, 2, GL_FLOAT, false, 8 * 4, 6 * 4);

        glBindBuffer(GL_ARRAY_BUFFER, 0);


        // Deselect VAO
        GL30.glBindVertexArray(0);

    }



    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader shader) {
        try (ShaderUse wrap = new ShaderUse(shader)) {
            shader.setInt("material.texture", 0);
            shader.setInt("material.diffuse", 0);
            shader.setInt("material.specular", 1);
            if (material != null) {
                shader.setFloat("material.shininess", material.getShininess());
            }
            else {
                shader.setFloat("material.shininess", 32.0f);
            }

            // Upload matrices to the uniform variables
            int modelMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "modelMatrix");

            Matrix4x4 modelMatrix = getModelMatrix();

            GL20.glUniformMatrix4fv(modelMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(modelMatrix));

            GL30.glBindVertexArray(vaoId);
            glEnableVertexAttribArray(VBO_INDEX_VERTICES);
            glEnableVertexAttribArray(VBO_INDEX_NORMALS);
            glEnableVertexAttribArray(VBO_INDEX_DIFFUSE_MAP);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, specularMap.getTextureId());

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            // Put everything back to default (deselect)
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(VBO_INDEX_VERTICES);
            GL20.glDisableVertexAttribArray(VBO_INDEX_NORMALS);
            GL20.glDisableVertexAttribArray(VBO_INDEX_DIFFUSE_MAP);
            GL30.glBindVertexArray(0);
        }
    }
}
