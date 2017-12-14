package LightingMaterials2;

import Useful.AppParams;
import de.matthiasmann.twl.utils.PNGDecoder;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.*;

class ShinyCubeShadowsScene extends Scene {
    private final ArrayList<FancyCube> cubeModels = new ArrayList<>();
    private final ArrayList<FancyQuad> quadModels = new ArrayList<>();
    private final ArrayList<FancyCube> axisMarkers = new ArrayList<>();
    //    private final Shader standardShader;
    private CameraRotatingAroundOrigin camera;
    private final BrightLighting lighting;
    private boolean drawAxisMarkers = false;
    //    private final Shader shadowGenShader;
    private final Shader basicFlatShader = new Shader("../shaders/basic_lighting2_vertex.glsl", "../shaders/lighting_materials_lamp_fragment.glsl");
//    private final Shader standardShader = new Shader("../shaders/shadows_vertex.glsl", "../shaders/shadows_fragment.glsl");
    private final Shader standardShader = new Shader("../shaders/shadows_vertex.glsl", "../shaders/shadows_fragment.glsl");
//    private final Shader standardShader = new Shader("../shaders/lighting_materials_vertex.glsl", "../shaders/lighting_materials2_fragment.glsl");
    private final Shader shadowGenShader = new Shader("../shaders/shadow_mapping.vtx", "../shaders/empty.frag");
    private final Shader passthroughShader = new Shader("../shaders/passthrough_vertex.glsl", "../shaders/passthrough_fragment.glsl");
    private boolean renderToDepth = true;
    private boolean renderDepthFramebuffer = true;

    ShinyCubeShadowsScene() {
        Materials materials = new Materials();
        lighting = new BrightLighting();
        TextureFromFile texture = new TextureFromFile("../images/container2.png", PNGDecoder.Format.RGBA);
        TextureFromFile specularMap = new TextureFromFile("../images/container2_specular.png", PNGDecoder.Format.RGBA);

        // Scale, translate, then rotate.
        // Putting into a -1 to 1 space
        {
            int numCubesX = 10;
            int numCubesZ = 10;

            // Cubes!
            for (int x = 0; x < numCubesX; x ++)
                for (int z = 0; z < numCubesZ; z ++) {
                    // Cube goes -0.5f to 0.5f
                    // Want 10 cubes in a -1 to 1 space
                    // So each cube can be max 0.2 across, spaced every 0.2f
                    float xPos = x * (2.0f / numCubesX) - 1.0f;
                    float zPos = z * (2.0f / numCubesZ) - 1.0f;
                    Vector4 pos = new Vector4(xPos, 0, zPos, 1);
                    Matrix4x4 scale = Matrix4x4.scale(0.1f); // to 0.05 box, taking up 25% of space
                    FancyCube cube = new FancyCube(pos, Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap);
                    cubeModels.add(cube);
                }
        }

        {
            Matrix4x4 scale = Matrix4x4.scale(0.01f);
            // axis
            axisMarkers.add(new FancyCube(new Vector4(0,0,0,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));

            axisMarkers.add(new FancyCube(new Vector4(-1,0,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,0,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,0,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(-1,0,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));

            axisMarkers.add(new FancyCube(new Vector4(-1,1,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,1,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,1,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(-1,1,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));

            axisMarkers.add(new FancyCube(new Vector4(-1,-1,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,-1,-1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(1,-1,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));
            axisMarkers.add(new FancyCube(new Vector4(-1,-1,1,1), Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap));

        }


        {
            TextureFromFile floorTexture = new TextureFromFile("../images/WM_IndoorWood-44_1024.png", PNGDecoder.Format.RGBA);
//            Optional<Matrix4x4> rotate = Optional.empty();
            Optional<Matrix4x4> rotate = Optional.of(Matrix4x4.rotateAroundXAxis(90));
//            Optional<Matrix4x4> scale = Optional.empty();
            Optional<Matrix4x4> scale = Optional.of(Matrix4x4.scale(4));
//            Vector4 pos = new Vector4(-2f, -0.5f, -2f, 1);
            Vector4 pos = new Vector4(0,-0.05f,0, 1);
            // Goes -0.5f to 0.5f
            // Want it -2 to 2
            Material material = new Material("dull", null, null, null, 32);
            FancyQuad floor = new FancyQuad(pos, scale, rotate, material, floorTexture, floorTexture, 10);
            quadModels.add(floor);
        }


        camera = new CameraRotatingAroundOrigin();

    }

    @Override public void keyPressedImpl(long window, int key, int scancode, int action, int mods) {
        //-- Input processing
        float rotationDelta = 1.0f;
        float posDelta = 0.05f;

        if (key == GLFW_KEY_UP) camera.rotateUp(rotationDelta);
        else if (key == GLFW_KEY_DOWN) camera.rotateDown(rotationDelta);
        else if (key == GLFW_KEY_LEFT) camera.rotateLeft(rotationDelta);
        else if (key == GLFW_KEY_RIGHT) camera.rotateRight(rotationDelta);
        else if (key == GLFW_KEY_W) camera.moveForward(posDelta);
        else if (key == GLFW_KEY_S) camera.moveBackward(posDelta);
        else if (key == GLFW_KEY_R) camera.moveUp(posDelta);
        else if (key == GLFW_KEY_F) camera.moveDown(posDelta);
        else if (key == GLFW_KEY_A) camera.moveLeft(posDelta);
        else if (key == GLFW_KEY_D) camera.moveRight(posDelta);

        if (action == GLFW_PRESS) {
            lighting.handleKeyDown(key);

            if (key == GLFW_KEY_KP_9) drawAxisMarkers = !drawAxisMarkers;
            else if (key == GLFW_KEY_KP_8) renderToDepth = !renderToDepth;
            else if (key == GLFW_KEY_KP_7) renderDepthFramebuffer = !renderDepthFramebuffer;
        }
    }

    @Override public void draw(AppParams params) {
        glClearColor(1f, 1f, 1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//        PointLight light = lighting.points[3];

//        light.pos = camera.getPosition().toVector3();
        int depthMap = renderSceneFromPosition(new Vector3(0.1f, 0.1f, .1f));
        lighting.directional.setShadowTexture(depthMap);

        // 2. then render scene as normal with shadow mapping (using depth map)
        glViewport(0, 0, params.widthPixels, params.heightPixels);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glBindTexture(GL_TEXTURE_2D, depthMap);


        {
            Shader shader = standardShader;
            Matrix4x4 projectionMatrix = createPerspectiveProjectionMatrix(params);
            Matrix4x4 cameraTranslate = camera.getMatrix();
            try (ShaderUse wrap = new ShaderUse(shader)) {
                int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
                int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");

                GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
                GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));

                renderScene(shader);
            }
        }

        if (renderDepthFramebuffer) {
            try (ShaderUse su = new ShaderUse(passthroughShader)) {
                Texture texture = new TextureFromExisting(depthMap);
                FancyQuad quad = new FancyQuad(new Vector4(0, 0, 0, 1), Optional.empty(), Optional.empty(), null, texture, texture, 1.0f);

                quad.draw(null, null, passthroughShader);
            }
        }


    }

    private void renderScene(Shader shader) {
        assert (shader.isInUse());
//        try (ShaderUse wrap = new ShaderUse(shader)) {
//            int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
//            int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");
//
//            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
//            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));

        Matrix4x4 projectionMatrix = null;
        Matrix4x4 cameraTranslate = null;

        lighting.draw(projectionMatrix, cameraTranslate, standardShader, camera);
        if (drawAxisMarkers) {
            axisMarkers.forEach(model -> model.draw(projectionMatrix, cameraTranslate, basicFlatShader));
        }
        cubeModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shader));
//        quadModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shader));
//        }
    }

    private int renderSceneFromPosition(Vector3 position) {
        int depthMapFBO = glGenFramebuffers();
        int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;


        // Create a texture whcih the framebuffer will render too
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        if (renderToDepth) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (double[]) null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }
        else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, (double[]) null);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Failed to create framebuffer");
        }


        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        if (renderToDepth) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureId, 0);
            // Not rendering colour data
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        }
        else {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);

        }

        // Back to default framebugger (screen)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // 1. first render to depth map
        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glEnable(GL_DEPTH_TEST);
        glClear(GL_DEPTH_BUFFER_BIT);
        Matrix4x4 lightProjection = SceneUtils.createOrthoProjectionMatrix(-1f, 1f, -1f, 1f, 0.001f, 7.5f);
        Matrix4x4 lightView = Matrix4x4.lookAt(position, new Vector3(0, 0, 0), new Vector3(0, 1, 0));

        Matrix4x4 lightSpaceMatrix = lightProjection.$times(lightView);

        Shader shader;
        if (renderToDepth) {
            shader = shadowGenShader;
        }
        else {
            shader = standardShader;
        }
        try (ShaderUse su = new ShaderUse(shader)) {
            int lightSpaceMatrixLocation = GL20.glGetUniformLocation(shadowGenShader.getShaderId(), "lightSpaceMatrix");
            GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));

            glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
            glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
            glClear(GL_DEPTH_BUFFER_BIT);
            renderScene(shader);
            // Back to default framebugger (screen)
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(depthMapFBO);
        }

        return textureId;
    }

}
