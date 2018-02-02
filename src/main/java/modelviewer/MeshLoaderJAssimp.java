package modelviewer;

import jassimp.AiMesh;
import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.Jassimp;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Assimp general:
// Great guide to Assimp:
// https://learnopengl.com/#!Model-Loading/Assimp

// Models online:
// It's very hard to find good free models that actually work.  E.g. quite often the OBJ file contains hardcoded
// paths on the creator's machine.
// FBX seems a bit more solid.
// Often models are in .max or .c4d format - Assimp doesn't support these
// In general, check with the Assimp model viewer if it can display it properly, first.

// Oddness:
// Gives 24 vertices for a simple cube, rather than 8.
// https://sourceforge.net/p/assimp/discussion/817654/thread/026e9640/
// Basically, because of UV texture co-ords, it needs to duplicate vertices.

// Hmmm... Issues with Kotlin Assimp
// 1. Can't import everything e.g. IronMan fails mysteriously
// 2. Doesn't seem to import well e.g. cube produces 6 quads rather than the requested triangles.

// But LGJWL Assimp issues:
// 1. Can't make heads nor tails out of how to parse the materials
// 2. Also not always clean imports, e.g. weird glitches on Lego.
//        AIScene aiScene2 = aiImportFile(resourcePath.getPath().substring(1), flags);
//
// JAssimp, after getting past the PITA that was building the native libs:
// 1. Works soooo much better than the other two.  All the weird glitches disappear.
interface MeshLoader {
    ModelData load(URI resourcePath, String texturesDir, int flags) throws IOException;
}

// Loads a mesh (model) with JAssimp lib
public class MeshLoaderJAssimp implements MeshLoader {
    public ModelData load(URI resourcePath, String texturesDir, int flags) throws IOException {

        String fullPath = resourcePath.getPath().substring(1);

        // Make sure the libs are preloaded in reverse order so there's no lookup fails
        System.loadLibrary("assimp-vc140-mt");
        System.loadLibrary("jassimp");

        Set<AiPostProcessSteps> steps = new HashSet<AiPostProcessSteps>();
        steps.add(AiPostProcessSteps.TRIANGULATE);
        steps.add(AiPostProcessSteps.JOIN_IDENTICAL_VERTICES);
        steps.add(AiPostProcessSteps.GEN_SMOOTH_NORMALS);
        steps.add(AiPostProcessSteps.FIND_INVALID_DATA);
        // Convert weird mappings like cube or sphere to regular
        steps.add(AiPostProcessSteps.GEN_UV_COORDS);
//        steps.add(AiPostProcessSteps.TRANSFORM_UV_COORDS);

        AiScene scene = Jassimp.importFile(fullPath, steps);

        int numMeshes = scene.getNumMeshes();
        List<AiMesh> aiMeshes = scene.getMeshes();
        MeshData[] meshes = new MeshData[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AiMesh aiMesh = aiMeshes.get(i);
            MeshData mesh = processMesh(aiMesh, scene);
            meshes[i] = mesh;
        }

        return new ModelData(meshes, scene);

    }

    private MeshData processMesh(AiMesh aiMesh, AiScene scene) {
        int numVertices = aiMesh.getNumVertices();
        float[] vertices = new float[numVertices * 3];
        for (int vertex = 0; vertex < numVertices; vertex++) {
            int idx = vertex * 3;
            vertices[idx] = aiMesh.getPositionX(vertex);
            vertices[idx + 1] = aiMesh.getPositionY(vertex);
            vertices[idx + 2] = aiMesh.getPositionZ(vertex);
        }

        float[] normals = new float[numVertices * 3];
        for (int normal = 0; normal < numVertices; normal++) {
            int idx = normal * 3;
            normals[idx] = aiMesh.getPositionX(normal);
            normals[idx + 1] = aiMesh.getPositionY(normal);
            normals[idx + 2] = aiMesh.getPositionZ(normal);
        }

        float[] texCoords = new float[numVertices * 2];
        // Assimp allows each vertex to have 8 sets of tex coords, but we only care about the first
        if (aiMesh.hasTexCoords() && aiMesh.hasTexCoords(0)) {
            for (int vertex = 0; vertex < numVertices; vertex++) {
                int idx = vertex * 2;
                float u = aiMesh.getTexCoordU(vertex, 0);
                float v = aiMesh.getTexCoordV(vertex, 0);
//                assert(u >= 0);
//                assert(u <= 1.0f);
//                assert(v >= 0);
//                assert(v <= 1.0f);
                texCoords[idx] = u;
                texCoords[idx + 1] =  v;

//            texCoords[idx + 2] = aiMesh.getPositionZ(vertex, 0);
            }
        }

        ArrayList<Integer> indices = new ArrayList<Integer>();
//        float[] normals = new float[numVertices * 3];
        int numFaces = aiMesh.getNumFaces();
        for (int face = 0; face < numFaces; face++) {
            int numIndicesForFace = aiMesh.getFaceNumIndices(face);
            for (int index = 0; index < numIndicesForFace; index++) {
                int vertex = aiMesh.getFaceVertex(face, index);
                indices.add(vertex);
            }
        }
        int[] indicesRaw = new int[indices.size()];
        for (int index = 0; index < indices.size(); index++) {
            indicesRaw[index] = indices.get(index);
        }

        return new MeshData(vertices, normals, indicesRaw, texCoords, aiMesh.getMaterialIndex());
    }
}