package BasicModels;

import enterthematrix.Matrix4x4;

import java.nio.FloatBuffer;

class MeshData {
    protected final float[] vertices, normals;
    protected final int[] indices;
    protected final int indicesCount;

    public MeshData(float[] vertices, float[] normals, int[] indices, FloatBuffer textureBufFloat) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.indicesCount = indices.length;
    }

    /** @return The matrix required to scale this Mesh so it's longest dimension is 1.0
     */
    public Matrix4x4 getInitialScaleMatrix() {
        float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
        for (int i = 0; i < vertices.length; i += 3) {
            float x = vertices[i];
            float y = vertices[i + 1];
            float z = vertices[i + 2];
            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
            if (z < minZ) minZ = z;
            if (z > maxZ) maxZ = z;
        }
        float xDist = maxX - minX;
        float yDist = maxY - minY;
        float zDist = maxZ - minZ;
        float resizeFactor = 0;
        if (xDist > yDist) {
            if (xDist > zDist) resizeFactor = 1 / xDist;
            else resizeFactor = 1 / zDist;
        }
        else {
            if (yDist > zDist) resizeFactor = 1 / yDist;
            else resizeFactor = 1 / zDist;
        }
        return Matrix4x4.scale(resizeFactor);
    }


}
