package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

public class BrightLighting implements BlipHandler{
    final DirectionalLight directional;
    final PointLight[] points;
    public static final int MAX_POINT_LIGHTS = 4;
    private final Shader lampShader;
    private BlipHandler app;
    private final float defaultConstant = 1.0f;
    private final float defaultLinear = 0.7f;
    private final float defaultQuadratic = 1.8f;

    BrightLighting(BlipHandler app, ShaderStore shaders) {
        this.app = app;
        lampShader = shaders.basicFlatShader;
        Matrix4x4 standardLight = Matrix4x4.scale(0.01f);
//        lampShader =  Shader.create("../shaders/basic_lighting2_vertex.glsl", "../shaders/lighting_materials_lamp_fragment.glsl");
        points = new PointLight[MAX_POINT_LIGHTS];

//        lampShader.addVariable(ShaderVariable.changesEveryRun("projectionMatrix"));
//        lampShader.addVariable(ShaderVariable.changesEveryRun("viewMatrix"));
//        lampShader.addVariable(ShaderVariable.changesEveryRun("modelMatrix"));
//        lampShader.addVariable(ShaderVariable.changesEveryRun("lamp_Color"));

        float fullStrength = 1.0f;
        float halfStrength = 0.5f;
        float ambientStrength = 0.6f;
        float diffuseStrength = 0.9f;
//        float diffuseStrength = 3f;
        float directionalModifier = 2f;
        float specularDirectionalForce = 0.0f;
        float pointModifier = 50f;

        Vector3 ambient = new Vector3(ambientStrength * pointModifier, ambientStrength * pointModifier, ambientStrength * pointModifier);
        Vector3 diffuse = new Vector3(diffuseStrength * pointModifier, diffuseStrength * pointModifier, diffuseStrength * pointModifier);
        Vector3 specular = new Vector3(fullStrength * pointModifier, fullStrength * pointModifier, fullStrength * pointModifier);

        Vector3 ambientDirectional = new Vector3(ambientStrength * directionalModifier, ambientStrength * directionalModifier, ambientStrength * directionalModifier);
        Vector3 diffuseDirectional = new Vector3(diffuseStrength * directionalModifier, diffuseStrength * directionalModifier, diffuseStrength * directionalModifier);
        Vector3 specularDirectional = new Vector3(specularDirectionalForce, specularDirectionalForce, specularDirectionalForce);

        Vector3 directionalDir = new Vector3(-0.5f, -0.5f, -0.5f);

        directional = new DirectionalLight(directionalDir, true, ambientDirectional, diffuseDirectional, specularDirectional);
//        directional.setEnabled(false);


        // Putting into a -1 to 1 space
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {

            // Put in a circle
//            float pos = (float) Math.sin(HandyMaths.degreesToRadians(360 / MAX_POINT_LIGHTS) * i) * 0.1f;
            float pos = (i * (2.0f / MAX_POINT_LIGHTS)) - 1.0f;
            Vector4 lightPos = new Vector4(pos, 0.5f, pos, 1);
            PointLight light = new PointLight(lightPos, standardLight, lampShader, true, i, ambient, diffuse, specular, defaultConstant, defaultLinear, defaultQuadratic);
            points[i] = light;

            final int x = i;

//            light.setEnabled(false);
        }

    }

    void handleKeyDown(int key) {
//        if (key == GLFW_KEY_0) directional.setEnabled(!directional.isEnabled());
//        if (key == GLFW_KEY_0) directional.setEnabled(!directional.isEnabled());
//        else if (key == GLFW_KEY_1) points[0].setEnabled(!points[0].isEnabled());
//        else if (key == GLFW_KEY_2) points[1].setEnabled(!points[1].isEnabled());
//        else if (key == GLFW_KEY_3) points[2].setEnabled(!points[2].isEnabled());
//        else if (key == GLFW_KEY_4) points[3].setEnabled(!points[3].isEnabled());
    }

    void setupShader(Shader shader) {
        try (ShaderUse su = new ShaderUse(shader)) {
            directional.setupShader(su.shader);

            for (int i = 0; i < points.length; i ++) {
                Light light = points[i];
                light.setupShader(su.shader);
            }
        }
    }

    void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader shader, ICamera camera) {
//        float lightX = (float) (Math.sin(glfwGetTime())) * 0.1f + 0.2f;
//        Vector4 newLightPos = new Vector4(lightX, 0.1f, lightX, 1);
//        light.setPos(newLightPos);

        directional.draw(projectionMatrix, cameraTranslate, lampShader);
        Arrays.stream(points).forEach(light -> light.draw(projectionMatrix, cameraTranslate, lampShader));
    }

    @Override
    public void handle(Blip blip) {
        if (blip instanceof BlipSceneStart) {
            List<BlipUI> elements = new ArrayList<>();

            elements.add(BlipUICheckbox.create("Directional", directional.isEnabled(), directional::setEnabled, Optional.of(GLFW_KEY_0)));

            for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
                final int x = i;
                elements.add(BlipUICheckbox.create("Point " + i, points[i].isEnabled(), (v) -> {
                    points[x].setEnabled(v);
                }, Optional.of(GLFW_KEY_1 + i)));
            }

            elements.add(BlipUITextField.create(Optional.of("Constant"), String.valueOf(defaultConstant), (v) -> {
                float value = defaultConstant;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setConstant(x));
            }));
            elements.add(BlipUITextField.create(Optional.of("Linear"), String.valueOf(defaultLinear), (v) -> {
                float value = defaultLinear;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setLinear(x));
            }));
            elements.add(BlipUITextField.create(Optional.of("Quadratic"), String.valueOf(defaultQuadratic), (v) -> {
                float value = defaultQuadratic;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setQuadratic(x));
            }));

            app.handle(BlipUITitledSection.create("Lighting", BlipUIHStack.create(elements)));
        }
    }
}
