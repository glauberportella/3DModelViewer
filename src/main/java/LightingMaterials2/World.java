package LightingMaterials2;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

class World {
    private final ArrayList<CubeWithNormalsAndMaterialsAndDiffuseMap> models = new ArrayList<>();
    private final Shader lightingShader;
    private Camera camera;
    private final Lighting lighting;

    World() {
        lightingShader = new Shader("../shaders/lighting_materials_vertex.glsl", "../shaders/lighting_materials2_fragment.glsl");
        Materials materials = new Materials();
        lighting = new Lighting();

        // Scale, translate, then rotate.
//        {
//            // The cube
//            Matrix4x4 transform = Matrix4x4.identity();
//            models.add(new CubeWithNormalsAndMaterials(transform, lightingShader));
//        }

        {
            int numCubesX = 10;
            int numCubesZ = 10;
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


        camera = new Camera();

    }

    public void invoke(long window, int key, int scancode, int action, int mods) {
        //-- Input processing
        float rotationDelta = 1.0f;
        float posDelta = 0.05f;

        if (key == GLFW_KEY_UP) camera.rotateUp(rotationDelta);
        else if (key == GLFW_KEY_DOWN) camera.rotateDown(rotationDelta);
        else if (key == GLFW_KEY_LEFT) camera.rotateLeft(rotationDelta);
        else if (key == GLFW_KEY_RIGHT) camera.rotateRight(rotationDelta);
        else if (key == GLFW_KEY_W) camera.moveForward(posDelta);
        else if (key == GLFW_KEY_S) camera.moveBackward(posDelta);
        else if (key == GLFW_KEY_R) camera.moveUp(posDelta);
        else if (key == GLFW_KEY_F) camera.moveDown(posDelta);
        else if (key == GLFW_KEY_A) camera.moveLeft(posDelta);
        else if (key == GLFW_KEY_D) camera.moveRight(posDelta);

        if (action == GLFW_PRESS) {
            lighting.handleKeyDown(key);
        }
    }

    public void draw(Matrix4x4 projectionMatrix) {
        Matrix4x4 cameraTranslate = camera.getMatrix();
        lighting.draw(projectionMatrix, cameraTranslate, lightingShader, camera);
        models.forEach(model -> model.draw(projectionMatrix, cameraTranslate));
    }

}
