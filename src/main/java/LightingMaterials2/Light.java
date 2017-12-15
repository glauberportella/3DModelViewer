package LightingMaterials2;

import LightingMaterials2.Shaders.Shader;
import LightingMaterials2.Shaders.ShaderUse;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE6;
import static org.lwjgl.opengl.GL13.glActiveTexture;

abstract class Light {
    protected final Vector3 ambient, diffuse, specular;
    final ShadowMap shadowMap;
    protected int shadowTexture;
    boolean shadowsEnabled = false;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private boolean enabled;

    public void setShadowTexture(int shadowTexture) {
        this.shadowTexture = shadowTexture;
        shadowsEnabled = true;
    }

    public Light(boolean enabled, Vector3 ambient, Vector3 diffuse, Vector3 specular) {
        shadowMap = new ShadowMap();
        this.enabled = enabled;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        setShadowTexture(shadowMap.textureId);
    }

    abstract public void setupShader(Shader lightingShader);
//    abstract public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lightingShader, Camera camera);


    protected void setupShaderImpl(Shader lightingShader, String lightText) {
        lightingShader.setBoolean(lightText + ".enabled", isEnabled());
//        if (isEnabled()) {
            lightingShader.setVec3(lightText + ".ambient", ambient);
            lightingShader.setVec3(lightText + ".diffuse", diffuse); // darken the light a bit to fit the scene
            lightingShader.setVec3(lightText + ".specular", specular);
//        }
    }

    public abstract void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lampShader);
}

class PointLight extends Light {
    private final CubeWithNormals cube;
    private int index;
    Vector3 pos;

    public PointLight(Vector4 pos, Matrix4x4 otherTransform, Shader shader, boolean enabled, int index, Vector3 ambient, Vector3 diffuse, Vector3 specular) {
        super(enabled, ambient, diffuse, specular);
        cube = new CubeWithNormals(pos, otherTransform, shader);
        this.index = index;
        this.pos = pos.toVector3();
    }

    public Vector3 getPosition() {
        return pos;
    }

    @Override public void setupShader(Shader shader) {
        assert (shader.isInUse());
        String lightText = "pointLights[" + index + "]";
        super.setupShaderImpl(shader, lightText);
//        if (isEnabled()) {
            shader.setVec3(lightText + ".position", pos);
//            shader.setFloat(lightText + ".constant", 1.0f);
//            shader.setFloat(lightText + ".linear", 0.05f);
//            shader.setFloat(lightText + ".quadratic", 0.2f);
            shader.setFloat(lightText + ".constant", 1.0f);
            shader.setFloat(lightText + ".linear", 0.7f);
            shader.setFloat(lightText + ".quadratic", 1.8f);
            shader.setBoolean(lightText +".shadowsEnabled", shadowsEnabled);

//            if (shadowsEnabled) {
                shader.setInt(lightText +".shadowMap", index + 6);
                glActiveTexture(GL_TEXTURE6 + index);
                glBindTexture(GL_TEXTURE_2D, shadowTexture);
//            }
//        }
    }

    @Override
    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lampShader) {
        try (ShaderUse su2 = new ShaderUse(lampShader)) {
            lampShader.setVec3("lamp_Color", diffuse);
        }
        cube.draw(projectionMatrix, cameraTranslate);
    }

}