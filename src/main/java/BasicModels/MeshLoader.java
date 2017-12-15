package BasicModels;

import enterthematrix.Vector3;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.assimp.Assimp.*;


public class MeshLoader {
    public static MeshData[] loadResource(String resourcePathLocal, String texturesDir) {
        URL resourceFull = AppWrapper.class.getResource(resourcePathLocal);
        String resourcePathAbs = resourceFull.getPath().toString();

        URL textureFull = AppWrapper.class.getResource(texturesDir);
        String texturePathAbs = textureFull.getPath().toString();

        return load(resourcePathAbs, texturePathAbs);
    }

    public static MeshData[] load(String resource, String texturesDir) {
        return load(resource, texturesDir, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }



    public static MeshData[] load(String resourcePath, String texturesDir, int flags) {
        // https://learnopengl.com/#!Model-Loading/Assimp great guide to Assimp

        AIScene aiScene = aiImportFile(resourcePath, flags);
        if (aiScene == null) {
            System.out.println("Error loading model");
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
//            processMaterial(aiMaterial, materials, texturesDir);
            int x=1;
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        MeshData[] meshes = new MeshData[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                int x=1;
            MeshData mesh = processMesh(aiMesh, materials);
            meshes[i] = mesh;
        }


        return meshes;

    }
//
    private static MeshData processMesh(AIMesh aiMesh, List<Material> materials) {
//        List<Vector3> vertices = new ArrayList<>();
//        List<Vector3> normals = new ArrayList<>();
//        List<Vector3> textures = new ArrayList<>();
//        List<Integer> indices = new ArrayList<>();


        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        float[] vertices = new float[aiVertices.remaining() * 3];
        int index = 0;

        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
//            vertices.add(new Vector3(aiVertex.x(), aiVertex.y(), aiVertex.z()));
            vertices[index] = aiVertex.x();
            vertices[index + 1] = aiVertex.y();
            vertices[index + 2] = aiVertex.z();
            index += 3;
        }

        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        float[] normals = new float[aiNormals.remaining() * 3];
        index = 0;

        while (aiNormals.remaining() > 0) {
            AIVector3D ai = aiNormals.get();
//            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
            normals[index] = aiNormals.x();
            normals[index + 1] = aiNormals.y();
            normals[index + 2] = aiNormals.z();
            index += 3;
        }

        AIFace.Buffer aiFaces = aiMesh.mFaces();
//        ArrayList<Integer> indices = new float[aiFaces.sizeof() * 3];
        ArrayList<Integer> indices = new ArrayList<>();
        index = 0;

        while (aiFaces.remaining() > 0) {
            AIFace ai = aiFaces.get();
            IntBuffer buffer = ai.mIndices();
            while (buffer.hasRemaining()) {
                indices.add(buffer.get());
            }
        }

        int[] indicesArray = new int[indices.size()];

        for (int i = 0; i < indices.size(); i ++) {
            indicesArray[i] = indices.get(i);
        }
        // .toArray(indicesArray);

//        PointerBuffer textureBuf = aiMesh.mTextureCoords();
//        FloatBuffer textureBufFloat = textureBuf.getFloatBuffer(textureBuf.remaining());

//        if(mesh->mTextureCoords[0]) // does the mesh contain texture coordinates?
//        {
//            glm::vec2 vec;
//            vec.x = mesh->mTextureCoords[0][i].x;
//            vec.y = mesh->mTextureCoords[0][i].y;
//            vertex.TexCoords = vec;
//        }
//        else
//            vertex.TexCoords = glm::vec2(0.0f, 0.0f);

//        AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords();

//        while (aiTextures.remaining() > 0) {
//            AIVector3D ai = aiNormals.get();
//            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
//        }


//        Mesh mesh = new Mesh(vertices, normals, indicesArray, textureBufFloat);
        MeshData mesh = new MeshData(vertices, normals, indicesArray, null);

//        Material material;
//        int materialIdx = aiMesh.mMaterialIndex();
//        if (materialIdx >= 0 && materialIdx < materials.size()) {
//            material = materials.get(materialIdx);
//        } else {
//            material = new Material();
//        }
//        mesh.setMaterial(material);

        return mesh;
    }




//    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) throws Exception {
//        AIColor4D colour = AIColor4D.create();
//
//        AIString path = AIString.calloc();
//        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
//        String textPath = path.dataString();
//        Texture texture = null;
//        if (textPath != null && textPath.length() > 0) {
//            TextureCache textCache = TextureCache.getInstance();
//            texture = textCache.getTexture(texturesDir + "/" + textPath);
//        }
//
//        Vector4f ambient = Material.DEFAULT_COLOUR;
//        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
//        if (result == 0) {
//            ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
//        }
//
//        Vector4f diffuse = Material.DEFAULT_COLOUR;
//        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
//        if (result == 0) {
//            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
//        }
//
//        Vector4f specular = Material.DEFAULT_COLOUR;
//        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
//        if (result == 0) {
//            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
//        }
//
//        Material material = new Material(ambient, diffuse, specular, 1.0f);
//        material.setTexture(texture);
//        materials.add(material);
//    }

}