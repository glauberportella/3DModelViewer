package BasicModels;

import Useful.AppParams;
import de.matthiasmann.twl.utils.PNGDecoder;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class BasicModelScene extends Scene {
    private final ArrayList<FancyCube> cubeModels = new ArrayList<>();
    private final ArrayList<FancyQuad> quadModels = new ArrayList<>();
    private final ArrayList<FancyCube> axisMarkers = new ArrayList<>();
    //    private final Shader standardShader;
    private CameraRotatingAroundOrigin camera;
    private final BrightLighting lighting;
    private boolean drawAxisMarkers = false;
    //    private final Shader shadowGenShader;
    private final ShaderStore shaders = new ShaderStore();
    private final Mesh[] meshes;


    private boolean renderToDepth = true;
    private boolean renderDepthFramebuffer = false;
    private boolean drawFloor = true;
    private boolean shadowsEnabled = true;

    BasicModelScene() {
        lighting = new BrightLighting(shaders);
        MeshData[] meshData = MeshLoader.load("C:/dev/portfolio/3ddemo/out/production/resources/models/cube.obj", "C:/dev/portfolio/3ddemo/out/production/resources/images");
        meshes = new Mesh[meshData.length];
        Mesh mesh = new Mesh(new Vector4(0,0,0,1), Optional.of(Matrix4x4.scale(0.1f)), Optional.empty(), meshData[0]);
        meshes[0] = mesh;

        Materials materials = new Materials();
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
                    if (x == 0 && z == 0) continue;
                    // Cube goes -0.5f to 0.5f
                    // Want 10 cubes in a -1 to 1 space
                    // So each cube can be max 0.2 across, spaced every 0.2f
                    float xPos = x * (2.0f / numCubesX) - 1.0f;
                    float zPos = z * (2.0f / numCubesZ) - 1.0f;
                    Vector4 pos = new Vector4(xPos, 0, zPos, 1);
//                    Matrix4x4 scale = Matrix4x4.scale(0.16f); // to 0.05 box, taking up 25% of space
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
//            Vector4 pos = new Vector4(0,-0.05f,0, 1);
            Vector4 pos = new Vector4(0,-0.2f,0, 1);
//            Vector4 pos = new Vector4(0,-0.5f,0, 1);
            // Goes -0.5f to 0.5fc
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
            else if (key == GLFW_KEY_KP_6) drawFloor = !drawFloor;
            else if (key == GLFW_KEY_KP_5) shadowsEnabled = !shadowsEnabled;
        }
    }

    @Override public void draw(AppParams params) {
        shaders.reset();

//        glClearColor(1f, 1f, 1f, 1.0f);
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        try (ShaderUse su = new ShaderUse(shaders.standardShader)) {
            su.shader.setBoolean("shadowsEnabled", shadowsEnabled);
        }

        Matrix4x4 projectionMatrix =  createPerspectiveProjectionMatrix(params);
//        AppParams lightingParams = new AppParams();
//        lightingParams.widthPixels = 1024;
//        lightingParams.heightPixels = 1024;
//        lightingParams.fovDegrees = params.fovDegrees;
//        Matrix4x4 projectionMatrixLighting = createPerspectiveProjectionMatrix(lightingParams);

//        light.pos = camera.getPosition().toVector3();
//        Vector4 posToRenderFrom = camera.getPosition();
//        lighting.directional.setEnabled(true);

        Matrix4x4 lightProjection = SceneUtils.createOrthoProjectionMatrix(-1f, 1f, 1f, -1f, 0.001f, 1.5f);

//        if (lighting.directional.isEnabled() && lighting.directional.shadowsEnabled) {
        int textureToRender = lighting.directional.shadowTexture;
        if (lighting.directional.isEnabled()) {
            Vector4 posToRenderFrom = (lighting.directional.direction.$times(-1)).toVector4();
//            Vector4 posToRenderFrom = new Vector4(0.1f, 0.2f, 0.1f, 1f);
//        Vector4 posToRenderFrom = camera.getPosition();
//            int depthMapTexture =
            renderSceneFromPosition(posToRenderFrom, lightProjection, "lightSpaceMatrixDir", lighting.directional.shadowMap);
//            textureToRender = depthMapTexture;
//            lighting.directional.setShadowTexture(depthMapTexture);
        }
        for (int i = 0; i < lighting.points.length; i ++) {
            PointLight light = lighting.points[i];
//            if (light.isEnabled() && light.shadowsEnabled) {
            if (light.isEnabled()) {
                Vector4 posToRenderFrom = light.pos.toVector4();
//                int depthMapTexture =
                renderSceneFromPosition(posToRenderFrom, lightProjection, "lightSpaceMatrixes[" + i + "]", light.shadowMap);
//                light.setShadowTexture(depthMapTexture);
            }
        }

        // 2. then setup scene as normal with shadow mapping (using depth map)
        glViewport(0, 0, params.widthPixels, params.heightPixels);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glBindTexture(GL_TEXTURE_2D, depthMapTexture);

        {
            Shader shader = shaders.standardShader;
            Matrix4x4 cameraTranslate = camera.getMatrix();
            try (ShaderUse wrap = new ShaderUse(shader)) {
                int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
                int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");
//                int lightSpaceMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "lightSpaceMatrix");

                GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
                GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));
//                GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));

                renderScene(shader);
            }
        }

        if (renderDepthFramebuffer) {
            // No need for depth as we're just drawing a quad
            glDisable(GL_DEPTH_TEST);
            try (ShaderUse su = new ShaderUse(shaders.renderDepthMapShader)) {
//                int depthMapLocation = GL20.glGetUniformLocation(standardShader.getShaderId(), "depthMap");
//                GL20.glUniformMatrix4fv(depthMapLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
                Texture texture = new TextureFromExisting(textureToRender);

                su.shader.setInt("depthMap", 5);

                glActiveTexture(GL_TEXTURE5);
                glBindTexture(GL_TEXTURE_2D, texture.getTextureId());

                FancyQuad quad = new FancyQuad(new Vector4(0, 0, 0, 1), Optional.empty(), Optional.empty(), null, texture, texture, 1.0f);

                quad.draw(null, null, su.shader);
            }
            glEnable(GL_DEPTH_TEST);
        }


    }

    private void renderScene(Shader shader) {
//        assert (shader.isInUse());
        Matrix4x4 projectionMatrix = null;
        Matrix4x4 cameraTranslate = null;
        try (ShaderUse wrap = new ShaderUse(shader)) {
//            int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
//            int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");
//
//            GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(projectionMatrix));
//            GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(cameraTranslate));


            shader.setVec3("viewPos", camera.getPosition().toVector3());
            lighting.draw(projectionMatrix, cameraTranslate, shader, camera);
            if (drawAxisMarkers) {
                axisMarkers.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shaders.basicFlatShader));
            }
//            cubeModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shader));
            if (drawFloor) {
                quadModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shader));
            }
            Arrays.stream(meshes).forEach(mesh -> mesh.draw(projectionMatrix, cameraTranslate, wrap.shader));
        }

//        try (ShaderUse wrap = new ShaderUse(shaders.modelShader)) {
//            Arrays.stream(meshes).forEach(mesh -> mesh.draw(projectionMatrix, cameraTranslate, wrap.shader));
//        }

    }

    private void renderSceneFromPosition(Vector4 position, Matrix4x4 lightProjection, String shaderPosName, ShadowMap shadowMap) {

//        glBindTexture(GL_TEXTURE_2D, textureId);
//        if (renderToDepth) {
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (double[]) null);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        }
//        else {
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, (double[]) null);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        }
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//
//        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
//            System.err.println("Failed to create framebuffer");
//        }


//        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
//        if (renderToDepth) {
//            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureId, 0);
//            // Not rendering colour data
//            glDrawBuffer(GL_NONE);
//            glReadBuffer(GL_NONE);
//        }
//        else {
//            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
//
//        }
//
//        // Back to default framebugger (screen)
////        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//        // 1. first setup to depth map
//        glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
//        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
//        glEnable(GL_DEPTH_TEST);
//        glClear(GL_DEPTH_BUFFER_BIT);
//        Matrix4x4 lightProjection = SceneUtils.createOrthoProjectionMatrix(-1f, 1f, -1f, 1f, 0.001f, 7.5f);
//        Matrix4x4 lightProjection = SceneUtils.createPerspectiveProjectionMatrix(params);
        Matrix4x4 lightView = Matrix4x4.lookAt(position, new Vector4(0, 0, 0, 1), new Vector4(0, 1, 0, 1));

        Matrix4x4 lightSpaceMatrix = lightProjection.$times(lightView);

        try (ShaderUse su = new ShaderUse(shaders.shadowGenShader)) {
            shadowMap.setup(su.shader, lightSpaceMatrix);

            renderScene(shaders.shadowGenShader);
        }

//        Shader shader;
////        if (renderToDepth) {
//            shader = shadowGenShader;
//
//            try (ShaderUse su = new ShaderUse(shader)) {
//                int lightSpaceMatrixLocation = GL20.glGetUniformLocation(shadowGenShader.getShaderId(), "lightSpaceMatrix");
//                GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
//
//                glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
//                glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
//                glClear(GL_DEPTH_BUFFER_BIT);
//
//                // renderScene(shader);
//            }


//        }
//        else {
//            shader = standardShader;
//
//            try (ShaderUse su = new ShaderUse(shader)) {
//                shader.setBoolean("shadowsEnabled", false);
//
//                int projectionMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "projectionMatrix");
//                int viewMatrixLocation = GL20.glGetUniformLocation(shader.getShaderId(), "viewMatrix");
//
//                GL20.glUniformMatrix4fv(projectionMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
//                GL20.glUniformMatrix4fv(viewMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(Matrix4x4.identity()));
//
//                int lightSpaceMatrixLocation = GL20.glGetUniformLocation(shadowGenShader.getShaderId(), "lightSpaceMatrix");
//                GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
//
//                glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
//                glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
//                glClear(GL_DEPTH_BUFFER_BIT);
//                renderScene(shader);
//            }
//        }

        try (ShaderUse wrap = new ShaderUse(shaders.standardShader)) {
            int lightSpaceMatrixLocation = GL20.glGetUniformLocation(wrap.shader.getShaderId(), shaderPosName);
            GL20.glUniformMatrix4fv(lightSpaceMatrixLocation, false, MatrixLwjgl.convertMatrixToBuffer(lightSpaceMatrix));
        }

        // Back to default framebugger (screen)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        glDeleteFramebuffers(depthMapFBO);

//        return textureId;
    }

}
