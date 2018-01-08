package BasicModels;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
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

class BirdView {
    private Model cube;
    private boolean leader;

    public BirdView(Vector4 pos, Optional<Matrix4x4> scale, Optional<Matrix4x4> rotate, Material material, boolean leader) {
//        super(pos, scale, rotate);
        cube = new SimpleColouredCube(pos, scale, rotate, material);
        this.leader = leader;
    }

    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader shader) {
        cube.draw(projectionMatrix, cameraTranslate, shader);
    }

    void setPos(Vector3 pos) {
        cube.setPos(pos.toVector4());
    }
}
