package modelviewer;

import enterthematrix.Matrix4x4;

public class MeshDataUtils {
    /**
     * @return The matrix required to scale and place all meshes so they fit inside a -1 to 1 box, centred at the origin
     */
    static public Matrix4x4 getInitialMatrix(MeshData[] meshes) {
        Matrix4x4 scale = getInitialScaleMatrix(meshes);
        Matrix4x4 translate = getInitialTranslationMatrix(meshes);
        return scale.$times(translate);
    }

    /**
     * @return The matrix required to scale all meshes so their longest dimension is 1.0
     */
    static public Matrix4x4 getInitialScaleMatrix(MeshData[] meshes) {
        float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
        for (int m = 0; m < meshes.length; m++) {
            MeshData mesh = meshes[m];
            for (int i = 0; i < mesh.vertices.length; i += 3) {
                float x = mesh.vertices[i];
                float y = mesh.vertices[i + 1];
                float z = mesh.vertices[i + 2];
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
                if (z < minZ) minZ = z;
                if (z > maxZ) maxZ = z;
            }
        }
        float xDist = maxX - minX;
        float yDist = maxY - minY;
        float zDist = maxZ - minZ;
        float resizeFactor = 0;
        if (xDist > yDist) {
            // 2 because it goes from -1 to 1
            if (xDist > zDist) resizeFactor = 2 / xDist;
            else resizeFactor = 2 / zDist;
        } else {
            if (yDist > zDist) resizeFactor = 2 / yDist;
            else resizeFactor = 2 / zDist;
        }
        return Matrix4x4.scale(resizeFactor);
    }

    /**
     * @return The matrix required to move all meshes so the combined centre is (0,0,0)
     */
    static public Matrix4x4 getInitialTranslationMatrix(MeshData[] meshes) {
        float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
        for (int m = 0; m < meshes.length; m++) {
            MeshData mesh = meshes[m];
            for (int i = 0; i < mesh.vertices.length; i += 3) {
                float x = mesh.vertices[i];
                float y = mesh.vertices[i + 1];
                float z = mesh.vertices[i + 2];
                if (x < minX) minX = x;
                if (x > maxX) maxX = x;
                if (y < minY) minY = y;
                if (y > maxY) maxY = y;
                if (z < minZ) minZ = z;
                if (z > maxZ) maxZ = z;
            }
        }
        float xDist = -minX - maxX;
        float yDist = -minY - maxY;
        float zDist = -minZ - maxZ;
        return Matrix4x4.translate(xDist / 2, yDist / 2, zDist / 2);
    }

}
