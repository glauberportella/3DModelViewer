package BasicLighting2;

import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderUse implements AutoCloseable {
    public ShaderUse(Shader shader) {
        shader.use();
    }

    @Override public void close() {
        glUseProgram(0);
    }
}
