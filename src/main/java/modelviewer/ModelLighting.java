package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;

public class ModelLighting implements BlipHandler{
    final DirectionalLight directional;
    final PointLight[] points;
    public static final int MAX_POINT_LIGHTS = 4;
    private final Shader lampShader;
    BlipHandler app;
//    private final float defaultConstant = 1.0f;
//    private final float defaultLinear = 0.7f;
//    private final float defaultQuadratic = 1.8f;
    private final float defaultConstant = 0.01f;
    private final float defaultLinear = 0.01f;
    private final float defaultQuadratic = 0.2f;

    ModelLighting(BlipHandler app, ShaderStore shaders) {
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
        float ambientStrength = 0.4f;
        float diffuseStrength = 0.1f;
//        float diffuseStrength = 3f;
        float directionalModifier = 2f;
//        float specularDirectionalForce = 0.0f;
        float ambientMin = 0.1f;

        float directionalAmbient = 0.4f;
        float directionalDiffuse = 0.7f;
        float directionalSpecular = 0.5f;

        Vector3 ambient = new Vector3(ambientStrength, ambientStrength, ambientStrength);
        Vector3 diffuse = new Vector3(diffuseStrength, diffuseStrength, diffuseStrength);
        Vector3 specular = new Vector3(fullStrength, fullStrength, fullStrength);

        Vector3 ambientDirectional = new Vector3(directionalAmbient, directionalAmbient, directionalAmbient);
        Vector3 diffuseDirectional = new Vector3(directionalDiffuse, directionalDiffuse, directionalDiffuse);
        Vector3 specularDirectional = new Vector3(directionalSpecular, directionalSpecular, directionalSpecular);

        Vector3 directionalDir = new Vector3(-0.5f, -0.5f, -0.5f);

        directional = new DirectionalLight(directionalDir, true, ambientDirectional, diffuseDirectional, specularDirectional, ambientMin);
//        directional.setEnabled(false);


        // Putting into a -1 to 1 space
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {

            // Put in a circle
//            float pos = (float) Math.sin(HandyMaths.degreesToRadians(360 / MAX_POINT_LIGHTS) * i) * 0.1f;
            float pos = (i * (2.0f / MAX_POINT_LIGHTS)) - 1.0f;
            Vector4 lightPos = new Vector4(pos, 1f, pos, 1);
            PointLight light = new PointLight(lightPos, standardLight, lampShader, true, i, ambient, diffuse, specular, defaultConstant, defaultLinear, defaultQuadratic);
            points[i] = light;

            final int x = i;

            light.setEnabled(false);
        }

//        points[0].setEnabled(true);
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
            List<BlipUI> elements1 = new ArrayList<>();
            List<BlipUI> elementsDir = new ArrayList<>();
            List<BlipUI> elements2 = new ArrayList<>();
            List<BlipUI> sections = new ArrayList<>();

            elements1.add(BlipUICheckbox.create("Directional", directional.isEnabled(), directional::setEnabled, Optional.of(GLFW_KEY_0)));

            for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
                final int x = i;
                elements1.add(BlipUICheckbox.create("Point " + i, points[i].isEnabled(), (v) -> {
                    points[x].setEnabled(v);
                }, Optional.of(GLFW_KEY_1 + i)));
            }

            elementsDir.add(BlipUITextField.create(Optional.of("Ambient Min"), String.valueOf(directional.ambientMin), (v) -> {
                float value = directional.ambientMin;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                directional.ambientMin = value;
            }));

            elementsDir.add(BlipUITextField.create(Optional.of("Dir Ambient"), String.valueOf(directional.ambient.x()), (v) -> {
                float value = directional.ambient.x();
                try { value = Float.valueOf(v); } catch(Exception e) {}
                directional.ambient = Vector3.fill(value);
            }));

            elementsDir.add(BlipUITextField.create(Optional.of("Dir Diffuse"), String.valueOf(directional.diffuse.x()), (v) -> {
                float value = directional.diffuse.x();
                try { value = Float.valueOf(v); } catch(Exception e) {}
                directional.diffuse = Vector3.fill(value);
            }));

            elementsDir.add(BlipUITextField.create(Optional.of("Dir Specular"), String.valueOf(directional.specular.x()), (v) -> {
                float value = directional.specular.x();
                try { value = Float.valueOf(v); } catch(Exception e) {}
                directional.specular = Vector3.fill(value);
            }));

            elements2.add(BlipUITextField.create(Optional.of("Point Constant"), String.valueOf(defaultConstant), (v) -> {
                float value = defaultConstant;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setConstant(x));
            }));
            elements2.add(BlipUITextField.create(Optional.of("Point Linear"), String.valueOf(defaultLinear), (v) -> {
                float value = defaultLinear;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setLinear(x));
            }));
            elements2.add(BlipUITextField.create(Optional.of("Point Quadratic"), String.valueOf(defaultQuadratic), (v) -> {
                float value = defaultQuadratic;
                try { value = Float.valueOf(v); } catch(Exception e) {}
                final float x = value;
                Arrays.stream(points).forEach(p -> p.setQuadratic(x));
            }));

            sections.add(BlipUIHStack.create(elements1));
            sections.add(BlipUIHStack.create(elementsDir));
            sections.add(BlipUIHStack.create(elements2));
            BlipUIVStack done = BlipUIVStack.create(sections);

            app.handle(BlipUITitledSection.create("Lighting", done));
        }
    }
}
