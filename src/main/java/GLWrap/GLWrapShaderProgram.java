package GLWrap;

import static org.lwjgl.opengl.GL20.glUseProgram;

public class GLWrapShaderProgram implements AutoCloseable {
    public GLWrapShaderProgram(int shaderId) {
        glUseProgram(shaderId);
    }

    @Override
    public void close() {
        glUseProgram(0);
    }
}
