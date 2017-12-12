package BasicLighting2;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.util.ArrayList;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

class World {
    private final ArrayList<CubeWithNormals> models = new ArrayList<>();
    private final Shader lightingShader;
    private Camera cameraPos;
    private CubeWithNormals light;

    World() {
        lightingShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/basic_lighting2_fragment.glsl");
        Shader lampShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/basic_lighting_lamp_fragment.glsl");

        // Scale, translate, then rotate.
//        {
//            // The cube
//            Matrix4x4 transform = Matrix4x4.identity();
//            models.add(new CubeWithNormals(transform, lightingShader));
//        }

        {
            int numCubes = 5;
            // Cubes!
            for (int x = 0; x < numCubes; x ++)
            for (int z = 0; z < numCubes; z ++) {
                Vector4 pos = new Vector4(0.1f * ((numCubes / 2) - x), 0, 0.1f * ((numCubes / 2) - z), 1);
                Matrix4x4 transform = Matrix4x4.scale(0.05f);
                CubeWithNormals cube = new CubeWithNormals(pos, transform, lightingShader);
                models.add(cube);
            }
        }

        {
            // The lamp cube
            Vector4 lightPos = new Vector4(0.3f, 0.3f, 0, 1);
            Matrix4x4 transform = Matrix4x4.scale(0.01f);
            light = new CubeWithNormals(lightPos, transform, lampShader);
            models.add(light);
        }


        lightingShader.setVec3("objectColour", new Vector3(1.0f, 0.5f, 0.31f));
        lightingShader.setVec3("lightColour",  new Vector3(1.0f, 1.0f, 1.0f));

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
        float lightX = (float) Math.sin(glfwGetTime()) % 2.0f - 1.0f;
        Vector4 newLightPos = new Vector4(lightX, 0.3f, lightX, 1);
        light.setPos(newLightPos);

        Matrix4x4 transform = Matrix4x4.translate(light.getPos()).$times(Matrix4x4.scale(0.01f));
        lightingShader.setVec3("lightPos", light.getPos().toVector3());

        Matrix4x4 cameraTranslate = cameraPos.getMatrix();
        lightingShader.setVec3("viewPos", cameraPos.getPosition().toVector3());
        models.forEach(model -> model.draw(projectionMatrix, cameraTranslate));
    }

}
