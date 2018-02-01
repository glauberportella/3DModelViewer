package modelviewer;

import enterthematrix.Matrix4x4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
    int depthMapFBO = glGenFramebuffers();
    final int SHADOW_WIDTH = 2048, SHADOW_HEIGHT = SHADOW_WIDTH;

    // Create a texture which the framebuffer will setup too
    int textureId = glGenTextures();

    ShadowMap() {
        glBindTexture(GL_TEXTURE_2D, textureId);
//        if (renderToDepth) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (double[]) null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


        // Prevent over-sampling
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

//        }
//        else {
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, (double[]) null);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        }
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Failed to create framebuffer");
        }

    }

    public void setup(Shader shadowGenShader, Matrix4x4 lightSpaceMatrix) {
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
//        if (renderToDepth) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureId, 0);
            // Not rendering colour data
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
//        }
//        else {
//            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
//
//        }

        // Back to default framebugger (screen)
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // 1. first setup to depth map
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glEnable(GL_DEPTH_TEST);
        glClear(GL_DEPTH_BUFFER_BIT);

        Shader shader;
//        if (renderToDepth) {
        shader = shadowGenShader;

        try (ShaderUse su = new ShaderUse(shader)) {
//            int lightSpaceMatrixLocation = GL20.glGetUniformLocation(shadowGenShader.getShaderId(), "lightSpaceMatrix");
//            GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
            shader.setMatrix("lightSpaceMatrix", lightSpaceMatrix);

            glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
            glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
            glClear(GL_DEPTH_BUFFER_BIT);

            // renderScene(shader);
        }


    }
}
