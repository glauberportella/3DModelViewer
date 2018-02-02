package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Mesh extends Model {

    private final MeshData data;
    private final int vaoId, vboIndicesId;
    private int indicesToDraw;
    private final Material material;
    //    private final int textureId;
//    private final Texture texture, specularMap;

    public Vector4 getPos() {
        return pos;
    }

    public void setPos(Vector4 pos) {
        this.pos = pos;
    }


    private Vector4 pos;
    private final int VBO_INDEX_VERTICES = 0;
    private final int VBO_INDEX_NORMALS = 1;
    private final int VBO_INDEX_TEXTURE = 2;
    private final int VBO_INDEX_INDICES = 3;


    Mesh(Vector4 pos, Optional<Matrix4x4> scale, Optional<Matrix4x4> rotate, MeshData data, Material material) {
        super(pos, scale, rotate);
        this.material = material;
        this.data = data;
        this.indicesToDraw = data.indicesCount;
//        this.vertices = data.vertices;
//        this.normals = normals;
//        this.indicesCount = indices.length;

        // VAO stores how to do an object, and can consist of up to 16 VBOs, which store the real data
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        {
            FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(data.vertices.length);
            verticesBuffer.put(data.vertices);
            verticesBuffer.flip();

            int vboVertices = GL15.glGenBuffers();
            glEnableVertexAttribArray(VBO_INDEX_VERTICES);
            glBindBuffer(GL_ARRAY_BUFFER, vboVertices);
            GL15.glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STREAM_DRAW);
            glVertexAttribPointer(VBO_INDEX_VERTICES, 3, GL_FLOAT, false, 0, 0);
        }

        if (data.normals != null) {
            FloatBuffer normalsBuffer = BufferUtils.createFloatBuffer(data.normals.length);
            normalsBuffer.put(data.normals);
            normalsBuffer.flip();

            int vboNormals = GL15.glGenBuffers();
            glEnableVertexAttribArray(VBO_INDEX_NORMALS);
            glBindBuffer(GL_ARRAY_BUFFER, vboNormals);
            GL15.glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL15.GL_STATIC_DRAW);
            glVertexAttribPointer(VBO_INDEX_NORMALS, 3, GL_FLOAT, false, 0, 0);
        }

        if (data.texCoords != null) {
            FloatBuffer texCoordsBuffer = BufferUtils.createFloatBuffer(data.texCoords.length);
            texCoordsBuffer.put(data.texCoords);
            texCoordsBuffer.flip();

            int vboTexCoords = GL15.glGenBuffers();
            glEnableVertexAttribArray(VBO_INDEX_TEXTURE);
//            glEnable(VBO_INDEX_TEXTURE);
            glBindBuffer(GL_ARRAY_BUFFER, vboTexCoords);
            GL15.glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL15.GL_STATIC_DRAW);
            glVertexAttribPointer(VBO_INDEX_TEXTURE, 2, GL_FLOAT, false, 0, 0);
//            glTexCoordPointer(2, GL_FLOAT, 0, texCoordsBuffer);

//                glBindBuffer(GL_ARRAY_BUFFER, vboTexHandle);
//        glBufferData(GL_ARRAY_BUFFER, textCoords, GL_STATIC_DRAW);
//    glTexCoordPointer(3, GL_FLOAT, 0, 0L);

//        }

//        if (material.getDiffuseTextures().size() > 0) {
//            // VBO 2 = textures
//            int vboTextures = GL15.glGenBuffers();
//            glEnableVertexAttribArray(VBO_INDEX_TEXTURE);
//            glBindBuffer(GL_ARRAY_BUFFER, vboTextures);
//            GL15.glBufferData(GL_ARRAY_BUFFER, material.getDiffuseTextures().get(0)., GL15.GL_STATIC_DRAW);
//            glVertexAttribPointer(VBO_INDEX_TEXTURE, 2, GL_FLOAT, false, 0, 0);
        }


        // Deselect VAO
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        {
            IntBuffer indicesBuffer = BufferUtils.createIntBuffer(data.indices.length);
            indicesBuffer.put(data.indices);
            indicesBuffer.flip();

            // Create a new VBO for the indices and select it (bind)
            // Note this isn't bound into the VAO. Not sure why for this example.
            vboIndicesId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        }
    }


    @Override
    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader shader) {
        try (ShaderUse wrap = new ShaderUse(shader)) {
            shader.setVec3("material.ambient", material.getAmbient());
            shader.setVec3("material.diffuse", material.getDiffuse());
            shader.setVec3("material.specular", material.getSpecular());
            shader.setFloat("material.shininess", material.getShininess());


            //            shader.setInt("material.diffuse", 0);
//            shader.setVec3("material.specular", material.getSpecular());
//            shader.setInt("material.specular", 1);
//            shader.setFloat("material.shininess", material.getShininess());

            // Upload matrices to the uniform variables
//            int modelMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "modelMatrix");
//            int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
//            int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");

//            Matrix4x4 modelMatrix = Matrix4x4.identity();// Matrix4x4.translate(pos).$times(otherTransform);
            Matrix4x4 modelMatrix = getModelMatrix();
            shader.setMatrix("modelMatrix", modelMatrix);

//            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer
// (projectionMatrix));
//            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));
//            GL20.glUniformMatrix4fv(modelMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(modelMatrix));

            GL30.glBindVertexArray(vaoId);
            glEnableVertexAttribArray(VBO_INDEX_VERTICES);
            glEnableVertexAttribArray(VBO_INDEX_NORMALS);
            glEnableVertexAttribArray(VBO_INDEX_TEXTURE);

            if (material.getDiffuseTextures().size() > 0) {
                TextureFromFile texture = material.getDiffuseTextures().get(0);
                shader.setInt("material.diffuseTexture", GL_TEXTURE0);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            }
            if (material.getSpecularTextures().size() > 0) {
                TextureFromFile texture = material.getSpecularTextures().get(0);
                shader.setInt("material.specularTexture", GL_TEXTURE1);
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            }

//            glActiveTexture(GL_TEXTURE1);
//            glBindTexture(GL_TEXTURE_2D, specularMap.getTextureId());

            // Bind to the index VBO that has all the information about the order of the vertices
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndicesId);

            // Draw the vertices
//            GL11.glDrawElements(GL11.GL_TRIANGLES, data.indicesCount, GL11.GL_UNSIGNED_INT, 0);
            GL11.glDrawElements(GL11.GL_TRIANGLES, indicesToDraw, GL11.GL_UNSIGNED_INT, 0);

            // Put everything back to default (deselect)
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
            GL20.glDisableVertexAttribArray(VBO_INDEX_VERTICES);
            GL20.glDisableVertexAttribArray(VBO_INDEX_NORMALS);
            GL20.glDisableVertexAttribArray(VBO_INDEX_TEXTURE);
            GL30.glBindVertexArray(0);
        }
    }

    public void setIndicesToDraw(int indicesToDraw) {
        this.indicesToDraw = indicesToDraw;
    }

    public int getIndicesToDraw() {
        return indicesToDraw;
    }
}
