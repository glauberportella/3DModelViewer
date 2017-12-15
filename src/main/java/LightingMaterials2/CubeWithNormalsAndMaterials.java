package LightingMaterials2;

import LightingMaterials2.Shaders.Shader;
import LightingMaterials2.Shaders.ShaderUse;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

class CubeWithNormalsAndMaterials {
    private final int vaoId;

    public Vector4 getPos() {
        return pos;
    }

    public void setPos(Vector4 pos) {
        this.pos = pos;
    }

    private Vector4 pos;
    private final Matrix4x4 otherTransform;
    private final Shader shader;
    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_NORMALS = 1;
    private final Material material;


    /**
     * @param otherTransform Used to place model into world. Scale, translate, then rotate.
     */
    public CubeWithNormalsAndMaterials(Vector4 pos, Matrix4x4 otherTransform, Shader shader, Material material) {
        this.pos = pos;
        this.otherTransform = otherTransform;
        this.shader = shader;
        this.material = material;

        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,

                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  0.0f,  1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f,  0.0f,  1.0f,

                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
                -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
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
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(VBO_INDEX_VERTICES, 3, GL_FLOAT, false, 6 * 4, 0);
        // normal attribute
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(VBO_INDEX_NORMALS, 3, GL_FLOAT, false, 6 * 4, 3 * 4);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Deselect VAO
        GL30.glBindVertexArray(0);
    }


    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate) {
//        shader.setVec3("material.ambient",  new Vector3(1.0f, 0.5f, 0.31f).$times(5f));
//        shader.setVec3("material.diffuse",  new Vector3(1.0f, 0.5f, 0.31f).$times(2f));
//        shader.setVec3("material.specular", new Vector3(0.1f, 0.1f, 0.1f));
//        shader.setFloat("material.shininess", 32.0f);

//        shader.setFloat("material.shininess", material.getShininess());

//        shader.setVec3("material.ambient", material.getAmbient());
//        shader.setVec3("material.diffuse", material.getDiffuse());
//        shader.setVec3("material.specular", material.getSpecular());

//        shader.setVec3("material.ambient", 0.0f, 0.1f, 0.06f);
//        shader.setVec3("material.diffuse", 0.0f, 0.50980392f, 0.50980392f);
//        shader.setVec3("material.specular", 0.50196078f, 0.50196078f, 0.50196078f);
//        shader.setFloat("material.shininess", 32.0f);

        try (ShaderUse wrap = new ShaderUse(shader)) {
            shader.setVec3("material.ambient", material.getAmbient());
            shader.setVec3("material.diffuse", material.getDiffuse());
            shader.setVec3("material.specular", material.getSpecular());
            shader.setFloat("material.shininess", material.getShininess());

            // Upload matrices to the uniform variables
            int modelMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "modelMatrix");
            int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
            int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");

            Matrix4x4 modelMatrix = Matrix4x4.translate(pos).$times(otherTransform);

            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));
            GL20.glUniformMatrix4fv(modelMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(modelMatrix));

            GL30.glBindVertexArray(vaoId);
            glEnableVertexAttribArray(VBO_INDEX_VERTICES);
            glEnableVertexAttribArray(VBO_INDEX_NORMALS);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

            // Put everything back to default (deselect)
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(VBO_INDEX_VERTICES);
            GL20.glDisableVertexAttribArray(VBO_INDEX_NORMALS);
            GL30.glBindVertexArray(0);
        }
    }
}
