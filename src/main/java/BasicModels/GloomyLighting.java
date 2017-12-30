package BasicModels;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class GloomyLighting {
    private final Light directional;
    private final Light[] points;
    static final int MAX_POINT_LIGHTS = 4;
    private final Shader lampShader;

    GloomyLighting() {
        Matrix4x4 standardLight = Matrix4x4.scale(0.01f);
        lampShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/lighting_materials_lamp_fragment.glsl", false);
        points = new Light[MAX_POINT_LIGHTS];

        float fullStrength = 1.0f;
        float halfStrength = 0.5f;
        float ambientStrength = 0.2f;
        float diffuseStrength = 0.6f;
        float directionalModifier = 1.0f;

        Vector3 ambient = new Vector3(ambientStrength, ambientStrength, ambientStrength);
        Vector3 diffuse = new Vector3(diffuseStrength, diffuseStrength, diffuseStrength);
        Vector3 specular = new Vector3(fullStrength, fullStrength, fullStrength);

        Vector3 ambientDirectional = new Vector3(ambientStrength * directionalModifier, ambientStrength * directionalModifier, ambientStrength * directionalModifier);
        Vector3 diffuseDirectional = new Vector3(diffuseStrength * directionalModifier, diffuseStrength * directionalModifier, diffuseStrength * directionalModifier);
        Vector3 specularDirectional = new Vector3(fullStrength * directionalModifier, fullStrength * directionalModifier, fullStrength * directionalModifier);

        Vector3 directionalDir = new Vector3(-1, -1, 0);

        directional = new DirectionalLight(directionalDir, true, ambientDirectional, diffuseDirectional, specularDirectional);

        // Putting into a -1 to 1 space
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {

            // Put in a circle
//            float pos = (float) Math.sin(HandyMaths.degreesToRadians(360 / MAX_POINT_LIGHTS) * i) * 0.1f;
            float pos = (i * (2.0f / MAX_POINT_LIGHTS)) - 1.0f;
            Vector4 lightPos = new Vector4(pos, 0.1f, pos, 1);
            Light light = new PointLight(lightPos, standardLight, lampShader, true, i, ambient, diffuse, specular);
            points[i] = light;
        }
    }

    void handleKeyDown(int key) {
        if (key == GLFW_KEY_0) directional.setEnabled(!directional.isEnabled());
        else if (key == GLFW_KEY_1) points[0].setEnabled(!points[0].isEnabled());
        else if (key == GLFW_KEY_2) points[1].setEnabled(!points[1].isEnabled());
        else if (key == GLFW_KEY_3) points[2].setEnabled(!points[2].isEnabled());
        else if (key == GLFW_KEY_4) points[3].setEnabled(!points[3].isEnabled());
    }


    void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lightingShader, Camera camera) {
//        float lightX = (float) (Math.sin(glfwGetTime())) * 0.1f + 0.2f;
//        Vector4 newLightPos = new Vector4(lightX, 0.1f, lightX, 1);
//        light.setPos(newLightPos);

        try (ShaderUse su = new ShaderUse(lightingShader)) {
            directional.setupShader(lightingShader);

            for (int i = 0; i < points.length; i ++) {
                Light light = points[i];
                light.setupShader(lightingShader);
            };

            lightingShader.setVec3("viewPos", camera.getPosition().toVector3());
        }

        directional.draw(projectionMatrix, cameraTranslate, lampShader);
        Arrays.stream(points).forEach(light -> light.draw(projectionMatrix, cameraTranslate, lampShader));
    }

}
