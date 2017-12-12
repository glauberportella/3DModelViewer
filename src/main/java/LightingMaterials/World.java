package LightingMaterials;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

class World {
    private final ArrayList<CubeWithNormalsAndMaterialsAndDiffuseMap> models = new ArrayList<>();
    private final Shader lightingShader;
    private final Shader lampShader;
    private Camera cameraPos;
    private CubeWithNormals light;

    World() {
        lightingShader = new Shader("../shaders/lighting_materials_vertex.glsl", "../shaders/lighting_materials_fragment.glsl");
        lampShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/lighting_materials_lamp_fragment.glsl");
        Materials materials = new Materials();

        // Scale, translate, then rotate.
//        {
//            // The cube
//            Matrix4x4 transform = Matrix4x4.identity();
//            models.add(new CubeWithNormalsAndMaterials(transform, lightingShader));
//        }

        {
            int numCubesX = 4;
            int numCubesZ = 6;
            // Cubes!
            for (int x = 0; x < numCubesX; x ++)
                for (int z = 0; z < numCubesZ; z ++) {
                    Vector4 pos = new Vector4(0.1f * ((numCubesX / 2) - x), 0, 0.1f * ((numCubesZ / 2) - z), 1);
                    Matrix4x4 transform = Matrix4x4.scale(0.05f);
//                    Material material = materials.get(new Random().nextInt(materials.getLength()));
//                    Material material = materials.get(x + z);
                    Material material = materials.get(0);
                    CubeWithNormalsAndMaterialsAndDiffuseMap cube = new CubeWithNormalsAndMaterialsAndDiffuseMap(pos, transform, lightingShader, material);
                    models.add(cube);
                }
        }

        {
            // The lamp cube
            Vector4 lightPos = new Vector4(0.3f, 0.3f, 0, 1);
            Matrix4x4 transform = Matrix4x4.scale(0.01f);
            light = new CubeWithNormals(lightPos, transform, lampShader);
        }



        cameraPos = new Camera();

    }

    public void invoke(long window, int key, int scancode, int action, int mods) {
        //-- Input processing
        float rotationDelta = 1.0f;
        float posDelta = 0.05f;

        if (key == GLFW_KEY_UP) cameraPos.rotateUp(rotationDelta);
        else if (key == GLFW_KEY_DOWN) cameraPos.rotateDown(rotationDelta);
        else if (key == GLFW_KEY_LEFT) cameraPos.rotateLeft(rotationDelta);
        else if (key == GLFW_KEY_RIGHT) cameraPos.rotateRight(rotationDelta);
        else if (key == GLFW_KEY_W) cameraPos.moveForward(posDelta);
        else if (key == GLFW_KEY_S) cameraPos.moveBackward(posDelta);
        else if (key == GLFW_KEY_R) cameraPos.moveUp(posDelta);
        else if (key == GLFW_KEY_F) cameraPos.moveDown(posDelta);
        else if (key == GLFW_KEY_A) cameraPos.moveLeft(posDelta);
        else if (key == GLFW_KEY_D) cameraPos.moveRight(posDelta);
    }

    public void draw(Matrix4x4 projectionMatrix) {
        float lightX = (float) (Math.sin(glfwGetTime())) * 0.1f + 0.2f;
        Vector4 newLightPos = new Vector4(lightX, 0.1f, lightX, 1);
        light.setPos(newLightPos);

//        Matrix4x4 transform = Matrix4x4.translate(light.getPos()).$times(Matrix4x4.scale(0.01f));
        try (ShaderUse su = new ShaderUse(lightingShader)) {
            float fullStrength = 1.0f;
            float halfStrength = 0.5f;
            float ambientStrength = halfStrength;
            float diffuseStrength = fullStrength;

//            Vector3 lightDiffuse = new Vector3(diffuseStrength * (float) Math.sin(glfwGetTime()), diffuseStrength * (float) Math.cos(glfwGetTime()), diffuseStrength);
            Vector3 lightDiffuse = new Vector3(diffuseStrength, diffuseStrength, diffuseStrength);

            lightingShader.setVec3("light.ambient", ambientStrength, ambientStrength, ambientStrength);
            lightingShader.setVec3("light.diffuse", lightDiffuse); // darken the light a bit to fit the scene
            lightingShader.setVec3("light.specular", 1.0f, 1.0f, 1.0f);
            lightingShader.setVec3("light.position", light.getPos().toVector3());
            lightingShader.setVec3("viewPos", cameraPos.getPosition().toVector3());

            try (ShaderUse su2 = new ShaderUse(lampShader)) {
                lampShader.setVec3("lamp_Color", lightDiffuse);
            }
        }

        Matrix4x4 cameraTranslate = cameraPos.getMatrix();
        models.forEach(model -> model.draw(projectionMatrix, cameraTranslate));
        light.draw(projectionMatrix, cameraTranslate);
    }

}
