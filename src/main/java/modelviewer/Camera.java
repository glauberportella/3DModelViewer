package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

interface ICamera {
    Vector4 getPosition();
    Matrix4x4 getMatrix();
}

class Camera implements ICamera {
    // Remember: normalized co-ords have to be between -1 to 1. If using a projection matrix (perspective or ortho),
    // this will normalize from whatever range we want into that.  Else, have to output in that range.
    private final Vector4 initialPosition = new Vector4(1.5f, 1.5f, 1.5f, 0);
//    private final Vector4 origin = new Vector4(0, 0, 0, 0);
//    private final Vector4 initialPosition = new Vector4(0, 0, 0.1f, 0);
    private final Vector4 initialTarget = new Vector4(0, 0, 0, 0);
    // Note this is really the reverse direction from where we're facing - it's meant to be
    private final Vector4 initialDirection = initialPosition.$minus(initialTarget).normalize();

    public Vector4 getPosition() {
        return position;
    }

    private Vector4 position = initialPosition;
    private float rotationLeftRightAngleDegrees = 0.0f;
    private float rotationTopBottomAngleDegrees = 0.0f;

    public void moveUp(float v) {
        position = position.copy(position.x(), position.y() + v, position.z(), position.w());
        dump();
    }
    public void moveDown(float v) {
        moveUp(v * -1);
    }
    public void moveRight(float v) {
        position = position.copy(position.x() + v, position.y(), position.z(), position.w());
        dump();
    }
    public void moveLeft(float v) {
        moveRight(v * -1);
    }
    public void moveForward(float v) {
        // Remember right hand system, further into the screen more negative z gets
        position = position.copy(position.x(), position.y(), position.z() - v, position.w());
        dump();
    }
    public void moveBackward(float v) {
        moveForward(v * -1);

    }
    public void rotateRight(float v) {
        rotationLeftRightAngleDegrees += v;
        dump();
        //        Matrix4x4 rotation = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotation.$times(rotationAxis);
    }
    public void rotateLeft(float v) {
        rotateRight(v * -1);
    }
    public void rotateUp(float v) {
        rotationTopBottomAngleDegrees += v;
        dump();
//        Matrix4x4 rotation = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotation.$times(rotationAxis);
    }
    public void rotateDown(float v) {
        rotateUp(v * -1);
    }
    public void dump() {
        System.out.println(this);
    }
    @Override public String toString() { return "x=" + position.x() + " y=" + position.y() + " z=" + position.z() + " rlr=" + rotationLeftRightAngleDegrees + " rtb=" + rotationTopBottomAngleDegrees; }

    // https://learnopengl.com/#!Getting-started/Camera
    // To define camera need position, where it's looking, an up axis and a right axis
    protected Matrix4x4 calcLookat(Vector4 right, Vector4 up, Vector4 dir) {
        Matrix4x4 lookat = new Matrix4x4(
                right.x(), right.y(), right.z(), 0,
                up.x(), up.y(), up.z(), 0,
                dir.x(), dir.y(), dir.z(), 0,
                0, 0, 0, 1
        ).$times(new Matrix4x4(
                1, 0, 0, - position.x(),
                0, 1, 0, - position.y(),
                0, 0, 1, - position.z(),
                0, 0, 0, 1
        ));

        return lookat;
    }

    public Matrix4x4 getMatrix() {

//        return Matrix4x4.lookAt(getPosition(), origin);

        // Force it always pointing straight up
        Vector4 up = new Vector4(0, 1, 0, 1);
        Vector4 xAxis = new Vector4(1, 0, 0, 1);
        Matrix4x4 rotationLeftRight = Matrix4x4.rotateAroundAnyAxis(up, rotationLeftRightAngleDegrees);
        Matrix4x4 rotationTopBottom = Matrix4x4.rotateAroundAnyAxis(xAxis, rotationTopBottomAngleDegrees);
        // This is the direction we're looking in
        Vector4 dir = rotationLeftRight.$times(rotationTopBottom).$times(initialDirection);

        // Cross product gives us vector perpendicular to both up and the direction we're looking - e.g. it's our right axis
        Vector4 right = up.crossProduct(dir).normalize();
        Matrix4x4 out = calcLookat(right, up, dir);
        return out;
//        return Matrix4x4.translate(position).$times(rotationLeftRight).$times(rotationTopBottom);
    }

}
