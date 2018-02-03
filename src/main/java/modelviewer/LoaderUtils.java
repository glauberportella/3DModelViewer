package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;
import jassimp.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoaderUtils {
    public static Mesh[] loadModel(File file) throws URISyntaxException, IOException {
        JassimpWrapperProvider wrapper = new JassimpWrapperProvider();
        Jassimp.setWrapperProvider(wrapper);
        MeshLoader meshLoader = new MeshLoaderJAssimp();

        ModelData modelData = meshLoader.load(file.toURI());

        ArrayList<Material> materials = new ArrayList<Material>();
        List<AiMaterial> materialsRaw = modelData.getScene().getMaterials();


        for (int materialIdx = 0; materialIdx < materialsRaw.size(); materialIdx++) {
            AiMaterial mat = materialsRaw.get(materialIdx);
            Colour ambient = (Colour) mat.getAmbientColor(wrapper);
            Colour diffuse = (Colour) mat.getDiffuseColor(wrapper);
            Colour specular = (Colour) mat.getSpecularColor(wrapper);
            float shininess = mat.getShininess();

            float reflectivity = mat.getReflectivity();
            float bumpScaling = mat.getBumpScaling();

            List<TextureFromFile> diffuseTextures = new ArrayList<>();

            if (mat.getNumTextures(AiTextureType.AMBIENT) > 0) {
                System.out.println("Don't handle more than 0 ambient textures yet");
            }

            if (mat.getNumTextures(AiTextureType.EMISSIVE) > 0) {
                System.out.println("Don't handle more than 0 emissive textures yet");
            }

            if (mat.getNumTextures(AiTextureType.DISPLACEMENT) > 0) {
                System.out.println("Don't handle more than 0 displacement textures yet");
            }

            if (mat.getNumTextures(AiTextureType.HEIGHT) > 0) {
                System.out.println("Don't handle more than 0 height textures yet");
            }

            if (mat.getNumTextures(AiTextureType.DIFFUSE) > 1) {
                System.out.println("Don't handle more than 1 diffuse textures yet");
            }

            if (mat.getNumTextures(AiTextureType.SPECULAR) > 1) {
                System.out.println("Don't handle more than 1 specular textures yet");
            }

            for (int idx = 0; idx < mat.getNumTextures(AiTextureType.DIFFUSE); idx += 1) {
                AiTextureInfo textureInfo = mat.getTextureInfo(AiTextureType.DIFFUSE, idx);
                String path = file.getParent() + "/" + textureInfo.getFile();
                AiTextureMapMode mapModeU = mat.getTextureMapModeU(AiTextureType.DIFFUSE, idx);
                AiTextureMapMode mapModeV = mat.getTextureMapModeV(AiTextureType.DIFFUSE, idx);
                AiTextureOp op = mat.getTextureOp(AiTextureType.DIFFUSE, idx);
                TextureFromFile texture = new TextureFromFile(path, mapModeU, mapModeV);
                diffuseTextures.add(texture);
            }

            List<TextureFromFile> specularTextures = new ArrayList<>();

            for (int idx = 0; idx < mat.getNumTextures(AiTextureType.SPECULAR); idx += 1) {
                AiTextureInfo textureInfo = mat.getTextureInfo(AiTextureType.SPECULAR, idx);
                String path = file.getParent() + "/" + textureInfo.getFile();
                AiTextureMapMode mapModeU = mat.getTextureMapModeU(AiTextureType.SPECULAR, idx);
                AiTextureMapMode mapModeV = mat.getTextureMapModeV(AiTextureType.SPECULAR, idx);
                TextureFromFile texture = new TextureFromFile(path, mapModeU, mapModeV);
                specularTextures.add(texture);
            }

            Material material = new Material(mat.getName(),
                    new Vector3(ambient.r, ambient.g, ambient.b),
                    new Vector3(diffuse.r, diffuse.g, diffuse.b),
                    new Vector3(specular.r, specular.g, specular.b),
                    shininess,
                    diffuseTextures,
                    specularTextures);
            materials.add(material);

        }

        Mesh[] meshes = new Mesh[modelData.getMeshes().length];
        MeshData[] meshData = modelData.getMeshes();
//        Matrix4x4 meshScale = Matrix4x4.identity();// MeshDataUtils.getInitialMatrix(modelData.getMeshes());
        Matrix4x4 meshScale = MeshDataUtils.getInitialMatrix(modelData.getMeshes());
        for (int i = 0; i < meshData.length; i++) {
            int matIndex = meshData[i].materialIndex;
            Material material = materials.get(matIndex);
            Mesh mesh = new Mesh(new Vector4(0, 0, 0, 1), Optional.of(meshScale), Optional.empty(), meshData[i],
                    material);
            final int x = i;
//            modelUI.add(BlipUITextField.create(Optional.of("Mesh indices"), String.valueOf(meshData[i].indices
// .length), (v) -> {
//                int indices = meshData[x].indices.length;
//                try {
//                    indices = Integer.parseInt(v);
//                }
//                catch(Exception e) {
//                }
//                mesh.setIndicesToDraw(indices);
//            }));
            meshes[i] = mesh;
        }

        return meshes;
    }

}
