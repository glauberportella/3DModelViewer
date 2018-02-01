package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

class CameraRotatingAroundOrigin implements ICamera {
    // Remember: normalized co-ords have to be between -1 to 1. If using a projection matrix (perspective or ortho),
    // this will normalize from whatever range we want into that.  Else, have to output in that range.
    private float rotationAngleDegrees = 0.7f;
    private final float initialZoom = 1.5f;
    private float zoom = initialZoom;
    private final Vector4 initialPosition = new Vector4(0.90f, 0.65f, 0.93f, 0);
    private final Vector4 origin = new Vector4(0, 0, 0, 0);

    public Vector4 getPosition() {
        return position;
    }

    private Vector4 position = initialPosition;

    public void moveUp(float v) {
        position = position.copy(position.x(), position.y() + v, position.z(), position.w());
        dump();
    }
    public void moveDown(float v) {
        moveUp(v * -1);
    }
    public void moveRight(float v) {
        rotationAngleDegrees -= v;
//        position = position.copy(position.x() + v, position.y(), position.z(), position.w());
        dump();
    }
    public void moveLeft(float v) {
        moveRight(v * -1);
    }
    public void moveForward(float v) {
        // Remember right hand system, further into the screen more negative z gets
//        position = position.copy(position.x(), position.y(), position.z() - v, position.w());
        zoom -= v;
        if (zoom <= 0.1f) zoom = 0.1f;
        dump();
    }
    public void moveBackward(float v) {
        moveForward(v * -1);

    }
    public void rotateRight(float v) {
//        rotationAngleDegrees += v;
        dump();
        //        Matrix4x4 rotationAngleDegrees = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotationAngleDegrees.$times(rotationAxis);
    }
    public void rotateLeft(float v) {
        rotateRight(v * -1);
    }
    public void rotateUp(float v) {
//        rotationTopBottomAngleDegrees += v;
        dump();
//        Matrix4x4 rotationAngleDegrees = Matrix4x4.rotateAroundYAxis(v);
//        rotationAxis = rotationAngleDegrees.$times(rotationAxis);
    }
    public void rotateDown(float v) {
        rotateUp(v * -1);
    }
    public void dump() {
        System.out.println(this);
    }
    @Override public String toString() {
        return "x=" + position.x() + " y=" + position.y() + " z=" + position.z() + " rlr=" + rotationAngleDegrees;
    }

    public Matrix4x4 getMatrix() {

        float x = (float) (zoom * Math.cos(rotationAngleDegrees));
        float z = (float) (zoom * Math.sin(rotationAngleDegrees));

        position = new Vector4(x, position.y(), z, 1);

        return Matrix4x4.lookAt(getPosition(), origin, new Vector4(0f,1f,0f,1f));
    }

}
