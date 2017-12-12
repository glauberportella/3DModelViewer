package ProjectedQuad;

import Useful.AppParams;
import Useful.GLWrapShaderProgram;
import enterthematrix.Matrix4x4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

class World {
    private ArrayList<Model> models;

    World() {
        models = new ArrayList<>();

        Matrix4x4 quad1Transform = Matrix4x4.translate(0, 0f, -0.3f);
        //        Matrix4x4 quad1Transform = Matrix4x4.translate(0, 0f, 0f).$times(Matrix4x4.rotateAroundZAxis(45));
        Model quad1 = new Model(quad1Transform);

        Matrix4x4 quad2Transform = Matrix4x4.translate(0f, 0f, 0.3f);//.$times(Matrix4x4.rotateAroundXAxis(90));
        Model quad2 = new Model(quad2Transform);

        // Draw floor
        Matrix4x4 floorTransform = Matrix4x4.rotateAroundAnyAxis(1, 0, 0, 89).$times(Matrix4x4.translate(0, 0, -0.8f));//.$times(Matrix4x4.scale(3));

        Model floor = new Model(floorTransform);

        models.add(quad1);
        models.add(quad2);
//        models.add(floor);
    }

    public void draw(int shaderProgram) {
        models.forEach(model -> model.draw(shaderProgram));
    }

}
