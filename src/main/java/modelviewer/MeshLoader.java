//package modelviewer;
//
////import assimp.AiMaterial;
////import assimp.AiMesh;
////import assimp.AiScene;
////import assimp.Importer;
//import enterthematrix.Vector3;
//import glm_.vec3.Vec3;
//import org.lwjgl.PointerBuffer;
//import org.lwjgl.assimp.*;
//
//import java.lang.reflect.Array;
//import java.net.URI;
//import java.net.URL;
//import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static org.lwjgl.assimp.Assimp.*;
//
//
//public class MeshLoader {
////    public static MeshData[] loadResource(String resourcePathLocal, String texturesDir) {
////        URL resourceFull = AppWrapper.class.getResource(resourcePathLocal);
////        String resourcePathAbs = resourceFull.getPath().toString();
////
////        URL textureFull = AppWrapper.class.getResource(texturesDir);
////        String texturePathAbs = textureFull.getPath().toString();
////
////        return load(resourcePathAbs, texturePathAbs);
////    }
////
////    public static MeshData[] load(String resource, String texturesDir) {
////        return load(URI.create(resource), texturesDir, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
////    }
//
//
//
//
//    public static MeshData[] load(URI resourcePath, String texturesDir, int flags) {
////    public static MeshData[] load(String path, String texturesDir, int flags) {
//        // https://learnopengl.com/#!Model-Loading/Assimp great guide to Assimp
//
////        URI uri = URI.create(path);
////        String fullPath = "file://" + uri.getPath();
//
//        // Assimp general:
//        // Gives 24 vertices for a simple cube, rather than 8.
//        // https://sourceforge.net/p/assimp/discussion/817654/thread/026e9640/
//        // Basically, because of UV texture co-ords, it needs to duplicate vertices.
//
//        // Hmmm... Issues with Kotlin Assimp
//        // 1. Can't import everything e.g. IronMan fails mysteriously
//        // 2. Doesn't seem to import well e.g. cube produces 6 quads rather than the requested triangles.
////        AiScene aiScene = new Importer().readFile(resourcePath, flags);
//
//        // But LGJWL Assimp issues:
//        // 1. Can't make heads nor tails out of how to parse the materials
//        // 2. Also not always clean imports, e.g. weird glitches on Lego.
//        AIScene aiScene2 = aiImportFile(resourcePath.getPath().substring(1), flags);
//
////        if (aiScene == null || aiScene2 == null) {
////            System.out.println("Error loading model " + resourcePath);
////        }
//
////                MeshData[] meshes = new MeshData[numMeshes];
////        MeshData[] meshes = new MeshData[0];
//
////        ArrayList<AiMaterial> materials2 = aiScene.getMaterials();
////        materials.forEach(mat -> {
////            mat.
////        });
////        PointerBuffer aiMaterials = aiScene.mMaterials();
////        List<Material> materials = new ArrayList<>();
////        for (int i = 0; i < materials2.size(); i++) {
////            AiMaterial mat = materials2.get(i);
//////            processMaterial(aiMaterial, materials, texturesDir);
//////            aiMaterial.mProperties().get(AI_MATKEY_COLOR_DIFFUSE);
////
////
//////            mat.getShininess();
//////            Material m = new Material(mat.getName(), mat.get);
////
////            int x=1;
////        }
//
//
////        List<AiMaterial> materials = new ArrayList<>();
//
//        int numMeshes = aiScene2.mNumMeshes();
//        PointerBuffer aiMeshes = aiScene2.mMeshes();
//        MeshData[] meshes = new MeshData[numMeshes];
////        Mesh[] meshes = new Mesh[numMeshes];
//        for (int i = 0; i < numMeshes; i++) {
//            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
//            int x=1;
//            MeshData mesh = processMesh(aiMesh, materials2);
//            meshes[i] = mesh;
//        }
////        int numMeshes = aiScene.getNumMeshes();
////        ArrayList<AiMesh> aiMeshes = aiScene.getMeshes();
////        for (int i = 0; i < numMeshes; i++) {
////            AiMesh aiMesh = aiMeshes.get(i);
////            int x=1;
////            MeshData mesh = processMesh(aiMesh, materials);
////            meshes[i] = mesh;
////        }
//
//
//        return meshes;
//
//    }
//
//
//
////    private ArrayList<Texture> loadMaterialTextures(AIMaterial mat, AiTextureType typ, String typeName)
////    {
////        ArrayList<Texture> textures = new ArrayList<>();
////        for(int i = 0; i < mat. GetTextureCount(typ); i++)
////        {
////            aiString str;
////            mat->GetTexture(typ, i, &str);
////            Texture texture;
////            texture.id = TextureFromFile(str.C_Str(), directory);
////            texture.typ = typeName;
////            texture.path = str;
////            textures.push_back(texture);
////        }
////        return textures;
////    }
//
////    @Deprecated
////    private static MeshData processMesh(AIMesh aiMesh, List<AiMaterial> materials) {
//////        List<Vector3> vertices = new ArrayList<>();
//////        List<Vector3> normals = new ArrayList<>();
//////        List<Vector3> textures = new ArrayList<>();
//////        List<Integer> indices = new ArrayList<>();
////
////
////        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
////        float[] vertices = new float[aiVertices.remaining() * 3];
////        int index = 0;
////
////        while (aiVertices.remaining() > 0) {
////            AIVector3D aiVertex = aiVertices.get();
//////            vertices.add(new Vector3(aiVertex.x(), aiVertex.y(), aiVertex.z()));
////            vertices[index] = aiVertex.x();
////            vertices[index + 1] = aiVertex.y();
////            vertices[index + 2] = aiVertex.z();
////            index += 3;
////        }
////
////        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
////        float[] normals = null;
////        if (aiNormals != null && aiNormals.remaining() > 0) {
////            normals = new float[aiNormals.remaining() * 3];
////            index = 0;
////
////            while (aiNormals.remaining() > 0) {
////                AIVector3D ai = aiNormals.get();
//////            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
////                normals[index] = aiNormals.x();
////                normals[index + 1] = aiNormals.y();
////                normals[index + 2] = aiNormals.z();
////                index += 3;
////            }
////        }
////
////        AIFace.Buffer aiFaces = aiMesh.mFaces();
//////        ArrayList<Integer> indices = new float[aiFaces.sizeof() * 3];
////        ArrayList<Integer> indices = new ArrayList<>();
////        index = 0;
////
////        while (aiFaces.remaining() > 0) {
////            AIFace ai = aiFaces.get();
////            IntBuffer buffer = ai.mIndices();
////            while (buffer.hasRemaining()) {
////                indices.add(buffer.get());
////            }
////        }
////
////        int[] indicesArray = new int[indices.size()];
////
////        for (int i = 0; i < indices.size(); i ++) {
////            indicesArray[i] = indices.get(i);
////        }
////
////        AiMaterial material = materials.get(aiMesh.mMaterialIndex());
//////        aiMesh.mMaterialIndex()
////
////        // .toArray(indicesArray);
////
//////        PointerBuffer textureBuf = aiMesh.mTextureCoords();
//////        FloatBuffer textureBufFloat = textureBuf.getFloatBuffer(textureBuf.remaining());
////
//////        if(mesh->mTextureCoords[0]) // does the mesh contain texture coordinates?
//////        {
//////            glm::vec2 vec;
//////            vec.x = mesh->mTextureCoords[0][i].x;
//////            vec.y = mesh->mTextureCoords[0][i].y;
//////            vertex.TexCoords = vec;
//////        }
//////        else
//////            vertex.TexCoords = glm::vec2(0.0f, 0.0f);
////
//////        AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords();
////
//////        while (aiTextures.remaining() > 0) {
//////            AIVector3D ai = aiNormals.get();
//////            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
//////        }
////
////
//////        Mesh mesh = new Mesh(vertices, normals, indicesArray, textureBufFloat);
//////        MeshData mesh = new MeshData(vertices, normals, indicesArray, null, Optional.of(material));
////        MeshData mesh = new MeshData(vertices, normals, indicesArray, null);
////
//////        Material material;
//////        int materialIdx = aiMesh.mMaterialIndex();
//////        if (materialIdx >= 0 && materialIdx < materials.size()) {
//////            material = materials.get(materialIdx);
//////        } else {
//////            material = new Material();
//////        }
//////        mesh.setMaterial(material);
////
////        return mesh;
////    }
//
//
////    static MeshData processMesh(AiMesh aiMesh, List<AiMaterial> materials) {
////        List<Vec3> aiVertices = aiMesh.getVertices();
////        float[] vertices = new float[aiVertices.size() * 3];
////        int index = 0;
////
////        for (int i = 0; i < aiVertices.size(); i ++) {
////            Vec3 aiVertex = aiVertices.get(i);
//////            AIVector3D aiVertex = aiVertices.get();
//////            vertices.add(new Vector3(aiVertex.x(), aiVertex.y(), aiVertex.z()));
////            vertices[index] = aiVertex.x;
////            vertices[index + 1] = aiVertex.y;
////            vertices[index + 2] = aiVertex.z;
////            index += 3;
////        }
////
////        List<Vec3> aiNormals = aiMesh.getNormals();
////        float[] normals = new float[aiNormals.size() * 3];
////        index = 0;
////
////        for (int i = 0; i < aiNormals.size(); i ++) {
//////        if (aiNormals != null && aiNormals.remaining() > 0) {
//////            normals = new float[aiNormals.remaining() * 3];
//////            index = 0;
////
//////            while (aiNormals.remaining() > 0) {
//////                AIVector3D ai = aiNormals.get();
//////            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
////            Vec3 aiNormal = aiNormals.get(i);
////
////            normals[index] = aiNormal.x;
////            normals[index + 1] = aiNormal.y;
////            normals[index + 2] = aiNormal.z;
////            index += 3;
//////            }
////        }
////
////        List<List<Integer>> aiFaces = aiMesh.getFaces();
//////        ArrayList<Integer> indices = new float[aiFaces.sizeof() * 3];
////        ArrayList<Integer> indices = new ArrayList<>();
////        index = 0;
////
////        for (int i= 0; i < aiFaces.size(); i ++) {
////            List<Integer> aiFace = aiFaces.get(i);
//////        while (aiFaces.remaining() > 0) {
//////            AIFace ai = aiFaces.get();
//////            IntBuffer buffer =  ai.mIndices();
//////            while (buffer.hasRemaining()) {
//////                indices.add(buffer.get());
//////            }
////            indices.addAll(aiFace);
//////            indices.add(aiFace);
////        }
////
////        int[] indicesArray = new int[indices.size()];
////
////        for (int i = 0; i < indices.size(); i ++) {
////            indicesArray[i] = indices.get(i);
////        }
////        // .toArray(indicesArray);
////
//////        PointerBuffer textureBuf = aiMesh.mTextureCoords();
//////        FloatBuffer textureBufFloat = textureBuf.getFloatBuffer(textureBuf.remaining());
////
//////        if(mesh->mTextureCoords[0]) // does the mesh contain texture coordinates?
//////        {
//////            glm::vec2 vec;
//////            vec.x = mesh->mTextureCoords[0][i].x;
//////            vec.y = mesh->mTextureCoords[0][i].y;
//////            vertex.TexCoords = vec;
//////        }
//////        else
//////            vertex.TexCoords = glm::vec2(0.0f, 0.0f);
////
//////        AIVector3D.Buffer aiTextures = aiMesh.mTextureCoords();
////
//////        while (aiTextures.remaining() > 0) {
//////            AIVector3D ai = aiNormals.get();
//////            normals.add(new Vector3(ai.x(), ai.y(), ai.z()));
//////        }
////
////
//////        Mesh mesh = new Mesh(vertices, normals, indicesArray, textureBufFloat);
//////        MeshData mesh = new MeshData(vertices, normals, indicesArray, null, Optional.empty());
////        MeshData mesh = new MeshData(vertices, normals, indicesArray, null);
////
//////        Material material;
//////        int materialIdx = aiMesh.mMaterialIndex();
//////        if (materialIdx >= 0 && materialIdx < materials.size()) {
//////            material = materials.get(materialIdx);
//////        } else {
//////            material = new Material();
//////        }
//////        mesh.setMaterial(material);
////
////        return mesh;
////    }
//
////    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) throws Exception {
////        AIColor4D colour = AIColor4D.create();
////
////        AIString path = AIString.calloc();
////        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
////        String textPath = path.dataString();
////        Texture texture = null;
////        if (textPath != null && textPath.length() > 0) {
////            TextureCache textCache = TextureCache.getInstance();
////            texture = textCache.getTexture(texturesDir + "/" + textPath);
////        }
////
////        Vector4f ambient = Material.DEFAULT_COLOUR;
////        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
////        if (result == 0) {
////            ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
////        }
////
////        Vector4f diffuse = Material.DEFAULT_COLOUR;
////        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
////        if (result == 0) {
////            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
////        }
////
////        Vector4f specular = Material.DEFAULT_COLOUR;
////        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
////        if (result == 0) {
////            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
////        }
////
////        Material material = new Material(ambient, diffuse, specular, 1.0f);
////        material.setTexture(texture);
////        materials.add(material);
////    }
//
//}