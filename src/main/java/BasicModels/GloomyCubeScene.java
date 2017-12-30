package BasicModels;

import Useful.AppParams;
import de.matthiasmann.twl.utils.PNGDecoder;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

class GloomyCubeScene extends Scene {
    private final ArrayList<FancyCube> cubeModels = new ArrayList<>();
    private final ArrayList<FancyQuad> quadModels = new ArrayList<>();
    private final Shader lightingShader;
    private Camera camera;
    private final GloomyLighting lighting;

    GloomyCubeScene() {
        lightingShader = Shader.create("../shaders/lighting_materials_vertex.glsl", "../shaders/lighting_materials2_fragment.glsl");
        Materials materials = new Materials();
        lighting = new GloomyLighting();
        TextureFromFile texture = new TextureFromFile("../images/container2.png", PNGDecoder.Format.RGBA);
        TextureFromFile specularMap = new TextureFromFile("../images/container2_specular.png", PNGDecoder.Format.RGBA);


        // Scale, translate, then rotate.
        // Putting into a -1 to 1 space
        {
            int numCubesX = 10;
            int numCubesZ = 10;

            // Cubes!
            for (int x = 0; x < numCubesX; x ++)
                for (int z = 0; z < numCubesZ; z ++) {
                    // Cube goes -0.5f to 0.5f
                    // Want 10 cubes in a -1 to 1 space
                    // So each cube can be max 0.2 across, spaced every 0.2f
                    float xPos = x * (2.0f / numCubesX) - 1.0f;
                    float zPos = z * (2.0f / numCubesZ) - 1.0f;
                    Vector4 pos = new Vector4(xPos, 0, zPos, 1);
                    Matrix4x4 scale = Matrix4x4.scale(0.1f); // to 0.05 box, taking up 25% of space
                    FancyCube cube = new FancyCube(pos, Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap);
                    cubeModels.add(cube);
                }
        }


        {
            TextureFromFile floorTexture = new TextureFromFile("../images/step2b.png", PNGDecoder.Format.RGB);
//            Optional<Matrix4x4> rotate = Optional.empty();
            Optional<Matrix4x4> rotate = Optional.of(Matrix4x4.rotateAroundXAxis(90));
//            Optional<Matrix4x4> scale = Optional.empty();
            Optional<Matrix4x4> scale = Optional.of(Matrix4x4.scale(4));
//            Vector4 pos = new Vector4(-2f, -0.5f, -2f, 1);
            Vector4 pos = new Vector4(0,-0.05f,0, 1);
            // Goes -0.5f to 0.5f
            // Want it -2 to 2
            Material material = new Material("dull", null, null, null, 2);
            FancyQuad floor = new FancyQuad(pos, scale, rotate, material, floorTexture, floorTexture, 5);
            quadModels.add(floor);
        }


        camera = new Camera();

    }

    @Override public void keyPressedImpl(long window, int key, int scancode, int action, int mods) {
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

    @Override public void draw(AppParams params) {
        glClearColor(0f, 0f, 0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
        Matrix4x4 cameraTranslate = camera.getMatrix();
        lighting.draw(projectionMatrix, cameraTranslate, lightingShader, camera);
        cubeModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, lightingShader));
        quadModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, lightingShader));
    }

    @Override
    public void handle(Blip blip) {

    }
}
