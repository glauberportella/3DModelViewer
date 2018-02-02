package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;

class DirectionalLight extends Light {
    final Vector3 direction;
    float ambientMin;

    public DirectionalLight(Vector3 direction, boolean enabled, Vector3 ambient, Vector3 diffuse, Vector3 specular, float ambientMin) {
        super(enabled, ambient, diffuse, specular);
        this.direction = direction;
        this.ambientMin = ambientMin;
    }

    @Override public void setupShader(Shader shader) {
        assert (shader.isInUse());
        String lightText = "dirLight";
        super.setupShaderImpl(shader, lightText);
//        if (isEnabled()) {
            shader.setVec3(lightText + ".direction", direction);
            shader.setVec3(lightText + ".ambient", ambient);
            shader.setFloat(lightText + ".ambientMin", ambientMin);
            shader.setVec3(lightText + ".diffuse", diffuse);
            shader.setVec3(lightText + ".specular", specular);

            shader.setBoolean(lightText +".shadowsEnabled", shadowsEnabled);

//            if (shadowsEnabled) {
                shader.setInt(lightText +".shadowMap", 20);
                glActiveTexture(GL_TEXTURE3);
                glBindTexture(GL_TEXTURE_2D, shadowTexture);
//            }
//        }
    }


    @Override
    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lampShader) {
    }

}