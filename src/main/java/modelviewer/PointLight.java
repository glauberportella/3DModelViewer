package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE21;
import static org.lwjgl.opengl.GL13.glActiveTexture;

class PointLight extends Light {
    private final CubeWithNormals cube;
    private final int index;
    Vector3 pos;

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    private float constant, linear, quadratic;

    public PointLight(Vector4 pos, Matrix4x4 otherTransform, Shader shader, boolean enabled, int index, Vector3
            ambient, Vector3 diffuse, Vector3 specular, float constant, float linear, float quadratic) {
        super(enabled, ambient, diffuse, specular);
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
        cube = new CubeWithNormals(pos, otherTransform, shader);
        this.index = index;
        this.pos = pos.toVector3();
    }

    public Vector3 getPosition() {
        return pos;
    }

    @Override
    public void setupShader(Shader shader) {
        assert (shader.isInUse());
        String lightText = "pointLights[" + index + "]";
        super.setupShaderImpl(shader, lightText);
//        if (isEnabled()) {
        shader.setVec3(lightText + ".position", pos);
//            shader.setFloat(lightText + ".constant", 1.0f);
//            shader.setFloat(lightText + ".linear", 0.05f);
//            shader.setFloat(lightText + ".quadratic", 0.2f);
        shader.setVec3(lightText + ".ambient", ambient);
        shader.setVec3(lightText + ".diffuse", diffuse);
        shader.setVec3(lightText + ".specular", specular);

        shader.setFloat(lightText + ".constant", constant);
        shader.setFloat(lightText + ".linear", linear);
        shader.setFloat(lightText + ".quadratic", quadratic);
        shader.setBoolean(lightText + ".shadowsEnabled", shadowsEnabled);

//            if (shadowsEnabled) {
        // Hardcoded constant is clumsy attempt to avoid stepping on the regular textures
        shader.setInt(lightText + ".shadowMap", GL_TEXTURE21 + index);
        glActiveTexture(GL_TEXTURE21 + index);

//        shader.setInt(lightText +".shadowMap", index + 21);
//        glActiveTexture(GL_TEXTURE6 + index);

        glBindTexture(GL_TEXTURE_2D, shadowTexture);
//            }
//        }
    }

    @Override
    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate, Shader lampShader) {
        if (isEnabled() && shouldDraw) {
            try (ShaderUse wrap = new ShaderUse(lampShader)) {
                wrap.shader.setVec3("lamp_Color", diffuse);
            }
            cube.draw(projectionMatrix, cameraTranslate, lampShader);
        }
    }

}
