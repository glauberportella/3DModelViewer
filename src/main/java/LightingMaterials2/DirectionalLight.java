package LightingMaterials2;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

class DirectionalLight extends Light {
    final private Vector3 direction;

    public DirectionalLight(Vector3 direction, boolean enabled, Vector3 ambient, Vector3 diffuse, Vector3 specular) {
        super(enabled, ambient, diffuse, specular);
        this.direction = direction;
    }

    @Override public void setupShader(Shader lightingShader) {
        assert (lightingShader.isInUse());
        String lightText = "dirLight";
        super.setupShaderImpl(lightingShader, lightText);
        if (isEnabled()) {
            lightingShader.setVec3(lightText + ".direction", direction);
        }
    }

    @Override
    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lampShader) {
    }

}