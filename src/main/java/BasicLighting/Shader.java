package BasicLighting;

import Useful.ShaderUtils;
import enterthematrix.Vector3;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

class Shader {
    public int getShaderId() {
        return shaderProgram;
    }

    private final int shaderProgram;

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
        // TextureFromFile information will be attribute 2
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

    public void setVec3(String name, Vector3 vec) {
        try (ShaderUse wrap = new ShaderUse(this)) {
            int location = GL20.glGetUniformLocation(getShaderId(), name);
            glUniform3f(location, vec.x(), vec.y(), vec.z());
        }
    }
}

