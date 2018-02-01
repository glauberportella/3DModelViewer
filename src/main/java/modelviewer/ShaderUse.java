package modelviewer;

public class ShaderUse implements AutoCloseable {
    public final Shader shader;

    public ShaderUse(Shader shader) {
        this.shader = shader;
        shader.use();
    }

    @Override public void close() {
        shader.stop();
    }
}
