package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

import java.util.Optional;

abstract public class Model {
    public Vector4 getPos() {
        return pos;
    }

    public void setPos(Vector4 pos) {
        this.pos = pos;
    }

    private Vector4 pos;
    private Optional<Matrix4x4> scale, rotate;

    public Model(Vector4 pos, Optional<Matrix4x4> scale, Optional<Matrix4x4> rotate) {
        this.pos = pos;
        this.scale = scale;
        this.rotate = rotate;
    }

    abstract public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader shader);
    protected Matrix4x4 getModelMatrix() {
        return ModelUtils.getModelMatrix(pos, scale, rotate);
    }
}

class ModelUtils {
    public static Matrix4x4 getModelMatrix(Vector4 pos, Optional<Matrix4x4> scale, Optional<Matrix4x4> rotate) {
        // Scale, rotate then translate.
        // Remember the matrix operations have to be reversed. E.g. if we want to scale then translate, then we do translateMatrix * scaleMatrix.
        // E.g. read these right to left.
        Matrix4x4 translate =  Matrix4x4.translate(pos);
        if (scale.isPresent() && rotate.isPresent()) {
//            return scale.get().$times(translate).$times(rotate.get());
            return translate.$times(rotate.get().$times(scale.get()));
        }
        else if (scale.isPresent()) {
//            return scale.get().$times(translate);
            return translate.$times(scale.get());
        }
        else if (rotate.isPresent()) {
//            return translate.$times(rotate.get());
            return translate.$times(rotate.get());
        }
        else {
            return translate;
        }
    }
}
