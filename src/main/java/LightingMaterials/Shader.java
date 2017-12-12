package LightingMaterials;

import Useful.ShaderUtils;
import enterthematrix.Vector3;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;

/**
 * Think all shader operations have to be done inside a single glUseProgram block
 */
class Shader {
    public int getShaderId() {
        return shaderProgram;
    }

    private final int shaderProgram;
    private boolean inUse = false;

    Shader(String vertexResourceFilename, String fragmentResourceFilename) {
        // Load the vertex shader
        int vertexShader = ShaderUtils.loadShader(this.getClass().getResource(vertexResourceFilename), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fragmentShader = ShaderUtils.loadShader(this.getClass().getResource(fragmentResourceFilename), GL20.GL_FRAGMENT_SHADER);

        // Final steps to use the shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(shaderProgram, 0, "aPos");
        // Color information will be attribute 1
//        GL20.glBindAttribLocation(shaderProgram, 1, "in_Color");
        // Texture information will be attribute 2
//        GL20.glBindAttribLocation(shaderProgram, 2, "in_TextureCoord");

        glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == 0) {
            String error = glGetProgramInfoLog(shaderProgram);
            System.err.println("Failed to link shader: " + error);
        }

        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void assertInUse() {
        if (!inUse) {
            System.err.println("Not in use");
            assert (false);
        }
    }

    public void setFloat(String name, Float v) {
        assertInUse();
        int location = GL20.glGetUniformLocation(getShaderId(), name);
        glUniform1f(location, v);
    }

    public void setVec3(String name, float x, float y, float z) {
        assertInUse();

//        try (ShaderUse wrap = new ShaderUse(this)) {
            int location = GL20.glGetUniformLocation(getShaderId(), name);
//            if (location == -1) {
//                System.err.println("Could not get shader " + name);
//                assert (location != -1);
//            }
            glUniform3f(location, x, y, z);
//        }

    }

    public void setVec3(String name, Vector3 vec) {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }

    public void use() {
        glUseProgram(shaderProgram);
        inUse = true;
    }

    public void stop() {
        glUseProgram(0);
        inUse = false;
    }
}

