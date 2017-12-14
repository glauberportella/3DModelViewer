package LightingMaterials2;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

class CameraAutoRotatingAroundOrigin extends Camera {
    private final float radius = 10.0f;
//    private final Vector4 initialPosition = new Vector4(0, 0, radius, 0);
//    private final Vector4 initialTarget = new Vector4(0, 0, 0, 0);
//    private final Vector4 initialDirection = initialPosition.$minus(initialTarget).normalize();
//    private Vector4 origin = initialPosition;


    private float getX() {
        return (float) Math.sin(glfwGetTime()) * radius;
    }
    private float getZ() {
        return (float) Math.cos(glfwGetTime()) * radius;
    }

    public Matrix4x4 getMatrix() {
        Vector4 position = new Vector4(getX(), 0, getZ(), 0);
        Vector4 origin = new Vector4(0, 0, 0, 0);
        // Note this is really the reverse direction from where we're facing - it's meant to be
        Vector4 dir = position.$minus(origin).normalize();
        // Force it always pointing straight up
        Vector4 up = new Vector4(0, 1, 0, 1);
        Vector4 right = up.crossProduct(dir).normalize();
        Matrix4x4 lookat = calcLookat(right, up, dir);
        return lookat;
    }

}
