package BasicLighting;

import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderUse implements AutoCloseable {
    public ShaderUse(Shader shader) {
        glUseProgram(shader.getShaderId());
    }

    @Override public void close() {
        glUseProgram(0);
    }
}
