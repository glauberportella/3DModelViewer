package LightingMaterials2.Shaders;

import LightingMaterials2.BrightLighting;

import java.util.ArrayList;
import java.util.Arrays;

public class ShaderStore {
    public final Shader basicFlatShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/lighting_materials_lamp_fragment.glsl", false);
    //    private final Shader standardShader = new Shader("../shaders/shadows_vertex.glsl", "../shaders/shadows_fragment.glsl");
    public final Shader standardShader = new Shader("../shaders/shadows_vertex.glsl", "../shaders/shadows_fragment.glsl", false);
    //    private final Shader standardShader = new Shader("../shaders/lighting_materials_vertex.glsl", "../shaders/lighting_materials2_fragment.glsl");
    public final Shader shadowGenShader = Shader.create("../shaders/shadow_mapping.vtx", "../shaders/empty.frag", true);
    public final Shader passthroughShader = new Shader("../shaders/passthrough_vertex.glsl", "../shaders/passthrough_fragment.glsl", true);
    public final Shader renderDepthMapShader = new Shader("../shaders/passthrough_vertex.glsl", "../shaders/render_depth_map_fragment.glsl", true);

    private final ArrayList<Shader> shaders = new ArrayList<>(Arrays.asList(basicFlatShader, standardShader, shadowGenShader, passthroughShader, renderDepthMapShader));

    public ShaderStore() {
        basicFlatShader.addVariable(ShaderVariable.changesEveryRun("projectionMatrix"));
        basicFlatShader.addVariable(ShaderVariable.changesEveryRun("viewMatrix"));
        basicFlatShader.addVariable(ShaderVariable.changesEveryRun("modelMatrix"));
        basicFlatShader.addVariable(ShaderVariable.changesEveryRun("lamp_Color"));

        standardShader.addVariable(ShaderVariable.changesEveryRun("projectionMatrix"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("viewMatrix"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("modelMatrix"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("lightSpaceMatrixDir"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.enabled"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.direction"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.ambient"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.diffuse"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.specular"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.shadowsEnabled"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("dirLight.shadowMap"));
        for (int i = 0; i < BrightLighting.MAX_POINT_LIGHTS; i ++) {
            standardShader.addVariable(ShaderVariable.changesEveryRun("lightSpaceMatrixes["+i+"]"));

            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].enabled"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].position"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].constant"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].linear"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].quadratic"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].ambient"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].diffuse"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].specular"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].shadowsEnabled"));
            standardShader.addVariable(ShaderVariable.changesInfrequently("pointLights["+i+"].shadowMap"));
        }
        standardShader.addVariable(ShaderVariable.changesEveryRun("viewPos"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("material.texture"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("material.diffuse"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("material.specular"));
        standardShader.addVariable(ShaderVariable.changesEveryRun("material.shininess"));
        standardShader.addVariable(ShaderVariable.changesInfrequently("shadowsEnabled"));

        shadowGenShader.addVariable(ShaderVariable.changesEveryRun("lightSpaceMatrix"));
        shadowGenShader.addVariable(ShaderVariable.changesEveryRun("modelMatrix"));

        renderDepthMapShader.addVariable(ShaderVariable.changesEveryRun("depthMap"));

    }

    public void reset() {
        shaders.forEach(shader -> shader.reset());
    }

//    public Shader create(String vertexResourceFilename, String fragmentResourceFilename) {
//        Shader out = new Shader(vertexResourceFilename, fragmentResourceFilename, false);
//        shaders.add(out);
//        return out;
//    }
//
//    public Shader create(String vertexResourceFilename, String fragmentResourceFilename, boolean ignoreUnknownVariables) {
//        Shader out = new Shader(vertexResourceFilename, fragmentResourceFilename, ignoreUnknownVariables);
//        shaders.add(out);
//        return out;
//    }


}
