package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

abstract class Light {
    protected Vector3 ambient, diffuse, specular;
    final ShadowMap shadowMap;
    protected int shadowTexture;
    boolean shadowsEnabled = false;

    public boolean isShouldDraw() {
        return shouldDraw;
    }

    protected final boolean shouldDraw = true;

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

