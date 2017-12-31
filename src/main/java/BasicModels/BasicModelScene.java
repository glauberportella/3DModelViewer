package BasicModels;

import Useful.AppParams;
import de.matthiasmann.twl.utils.PNGDecoder;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector4;
import matrixlwjgl.MatrixLwjgl;
import org.lwjgl.opengl.GL20;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class BasicModelScene extends Scene {
    private final BlipHandler app;
    private final ArrayList<FancyCube> cubeModels = new ArrayList<>();
    private final ArrayList<FancyQuad> quadModels = new ArrayList<>();
    private final ArrayList<FancyCube> axisMarkers = new ArrayList<>();
    //    private final Shader standardShader;
    private CameraRotatingAroundOrigin camera;
    private final ModelLighting lighting;
    //    private final Shader shadowGenShader;
    private final ShaderStore shaders = new ShaderStore();
    private final Mesh[] meshes;


    private boolean drawAxisMarkers = false;
    private boolean renderToDepth = true;
    private boolean renderDepthFramebuffer = false;
    private boolean drawFloor = false;
    private boolean drawModel = true;
    private boolean renderLightsEnabled = true;
    private boolean shadowsEnabled = true;
    private boolean debugShader = false;

    private List<BlipUI> ui = new ArrayList<BlipUI>();


    @Override
    public void handle(Blip blip) {
        lighting.handle(blip);

        if (blip instanceof BlipSceneStart) {
            app.handle(BlipUITitledSection.create("Scene", BlipUIHStack.create(ui)));
        }
    }

    BasicModelScene(BlipHandler app) throws URISyntaxException {
        this.app = app;
        lighting = new ModelLighting(app, shaders);

//        if (key == GLFW_KEY_KP_9) drawAxisMarkers = !drawAxisMarkers;
//        else if (key == GLFW_KEY_KP_8) renderToDepth = !renderToDepth;
//        else if (key == GLFW_KEY_KP_7) renderDepthFramebuffer = !renderDepthFramebuffer;
//        else if (key == GLFW_KEY_KP_6) drawFloor = !drawFloor;
//        else if (key == GLFW_KEY_KP_5) shadowsEnabled = !shadowsEnabled;

        ui.add(BlipUICheckbox.create("Model", drawModel, (v) -> drawModel = v, Optional.empty()));
        ui.add(BlipUICheckbox.create("Axis markers", drawAxisMarkers, (v) -> drawAxisMarkers = v, Optional.empty()));
        ui.add(BlipUICheckbox.create("Floor", drawFloor, (v) -> drawFloor = v, Optional.of(GLFW_KEY_KP_6)));
        ui.add(BlipUICheckbox.create("Shadows", shadowsEnabled, (v) -> shadowsEnabled = v, Optional.of(GLFW_KEY_KP_5)));
        ui.add(BlipUICheckbox.create("Lights", renderLightsEnabled, (v) -> renderLightsEnabled = v, Optional.empty()));
        ui.add(BlipUICheckbox.create("Debug Shader", debugShader, (v) -> {
            debugShader = v;
            shaders.debugShader.setCheckErrors(debugShader);
            shaders.standardShader.setCheckErrors(!debugShader);
        }, Optional.empty()));

//        MeshData[] meshData = MeshLoader.load("C:/dev/portfolio/3ddemo/out/production/resources/models/cube.obj", "C:/dev/portfolio/3ddemo/out/production/resources/images");
        MeshData[] meshData = MeshLoader.load(AppWrapper.class.getResource("../models/cube.obj").toURI(), "C:/dev/portfolio/3ddemo/out/production/resources/images", aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
//        MeshData[] meshData = MeshLoader.load(AppWrapper.class.getResource("../models/IronMan.obj").toURI(), "C:/dev/portfolio/3ddemo/out/production/resources/images", aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
//        MeshData[] meshData = MeshLoader.load(AppWrapper.class.getResource("../models/lego obj.obj").toURI(), "C:/dev/portfolio/3ddemo/out/production/resources/images", aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
//        MeshData[] meshData = MeshLoader.load("C:/dev/portfolio/3ddemo/out/production/resources/models/LEGO_Man.obj", "C:/dev/portfolio/3ddemo/out/production/resources/images");
//        MeshData[] meshData = MeshLoader.load("C:/dev/portfolio/3ddemo/out/production/resources/models/lego obj.obj", "C:/dev/portfolio/3ddemo/out/production/resources/models");
//        MeshData[] meshData = MeshLoader.load("C:/dev/portfolio/3ddemo/out/production/resources/models/IronMan.obj", "C:/dev/portfolio/3ddemo/out/production/resources/models");
        meshes = new Mesh[meshData.length];
        Matrix4x4 meshScale = MeshDataUtils.getInitialScaleMatrix(meshData);
        for(int i = 0; i < meshData.length; i ++) {
//            Mesh mesh = new Mesh(new Vector4(0, 0, 0, 1), Optional.of(Matrix4x4.scale(0.1f)), Optional.empty(), meshData[i]);
            Mesh mesh = new Mesh(new Vector4(0, 0, 0, 1), Optional.of(meshScale), Optional.empty(), meshData[i]);
            final int x = i;
            ui.add(BlipUITextField.create(Optional.of("Mesh indices"), String.valueOf(meshData[i].indices.length), (v) -> {
                int indices = meshData[x].indices.length;
                try {
                    indices = Integer.parseInt(v);
                }
                catch(Exception e) {
                }
                mesh.setIndicesToDraw(indices);
            }));
            meshes[i] = mesh;
        }

        Materials materials = new Materials();
        TextureFromFile texture = new TextureFromFile("../images/container2.png", PNGDecoder.Format.RGBA);
        TextureFromFile specularMap = new TextureFromFile("../images/container2_specular.png", PNGDecoder.Format.RGBA);

        // Scale, translate, then rotate.
        // Putting into a -1 to 1 space
//        {
//            int numCubesX = 10;
//            int numCubesZ = 10;
//
//            // Cubes!
//            for (int x = 0; x < numCubesX; x ++)
//                for (int z = 0; z < numCubesZ; z ++) {
//                    if (x == 0 && z == 0) continue;
//                    // Cube goes -0.5f to 0.5f
//                    // Want 10 cubes in a -1 to 1 space
//                    // So each cube can be max 0.2 across, spaced every 0.2f
//                    float xPos = x * (2.0f / numCubesX) - 1.0f;
//                    float zPos = z * (2.0f / numCubesZ) - 1.0f;
//                    Vector4 pos = new Vector4(xPos, 0, zPos, 1);
////                    Matrix4x4 scale = Matrix4x4.scale(0.16f); // to 0.05 box, taking up 25% of space
//                    Matrix4x4 scale = Matrix4x4.scale(0.1f); // to 0.05 box, taking up 25% of space
//                    FancyCube cube = new FancyCube(pos, Optional.of(scale), Optional.empty(), materials.get(0), texture, specularMap);
//                    cubeModels.add(cube);
//                }
//        }

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

//        if (action == GLFW_PRESS) {
//            lighting.handleKeyDown(key);
//
////            if (key == GLFW_KEY_KP_9) drawAxisMarkers = !drawAxisMarkers;
////            else if (key == GLFW_KEY_KP_8) renderToDepth = !renderToDepth;
////            else if (key == GLFW_KEY_KP_7) renderDepthFramebuffer = !renderDepthFramebuffer;
////            else if (key == GLFW_KEY_KP_6) drawFloor = !drawFloor;
////            else if (key == GLFW_KEY_KP_5) shadowsEnabled = !shadowsEnabled;
//        }
    }

    private Shader getMainShader() {
        if (debugShader) return shaders.debugShader;
        else return shaders.standardShader;
    }

    @Override public void draw(AppParams params) {
        shaders.reset();

//        glClearColor(1f, 1f, 1f, 1.0f);
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        try (ShaderUse wrap = new ShaderUse(getMainShader())) {
            wrap.shader.setBoolean("shadowsEnabled", shadowsEnabled);
        }

        Matrix4x4 projectionMatrix =  createPerspectiveProjectionMatrix(params);
        Matrix4x4 lightProjection = SceneUtils.createOrthoProjectionMatrix(-1f, 1f, 1f, -1f, 0.001f, 1.5f);

        int textureToRender = lighting.directional.shadowTexture;
        if (lighting.directional.isEnabled()) {
            Vector4 posToRenderFrom = (lighting.directional.direction.$times(-1)).toVector4();
            renderSceneFromPosition(posToRenderFrom, lightProjection, "lightSpaceMatrixDir", lighting.directional.shadowMap);
        }
        else {
            try (ShaderUse wrap = new ShaderUse(getMainShader())) {
                wrap.shader.setMatrix("lightSpaceMatrixDir", Matrix4x4.identity());
            }
        }

        for (int i = 0; i < lighting.points.length; i ++) {
            PointLight light = lighting.points[i];
            if (light.isEnabled()) {
                Vector4 posToRenderFrom = light.pos.toVector4();
                renderSceneFromPosition(posToRenderFrom, lightProjection, "lightSpaceMatrixes[" + i + "]", light.shadowMap);
            }
        }

        // 2. then setup scene as normal with shadow mapping (using depth map)
        glViewport(0, 0, params.widthPixels, params.heightPixels);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        {
            Shader shader = getMainShader();
            Matrix4x4 cameraTranslate = camera.getMatrix();

            try (ShaderUse wrap = new ShaderUse(shaders.basicFlatShader)) {
                wrap.shader.setMatrix("projectionMatrix", projectionMatrix);
                wrap.shader.setMatrix("viewMatrix", cameraTranslate);
            }


            try (ShaderUse wrap = new ShaderUse(shader)) {
                wrap.shader.setMatrix("projectionMatrix", projectionMatrix);
                wrap.shader.setMatrix("viewMatrix", cameraTranslate);

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
        Matrix4x4 projectionMatrix = null;
        Matrix4x4 cameraTranslate = null;
        try (ShaderUse wrap = new ShaderUse(shader)) {
            shader.setVec3("viewPos", camera.getPosition().toVector3());
            lighting.setupShader(wrap.shader);

            if (renderLightsEnabled) {
                lighting.draw(projectionMatrix, cameraTranslate, null, camera);
            }

            if (drawAxisMarkers) {
                axisMarkers.forEach(model -> model.draw(projectionMatrix, cameraTranslate, shaders.basicFlatShader));
            }
            if (drawFloor) {
                quadModels.forEach(model -> model.draw(projectionMatrix, cameraTranslate, wrap.shader));
            }
            if (drawModel) {
                for (int i = 0; i < meshes.length; i++) {
                    meshes[i].draw(projectionMatrix, cameraTranslate, wrap.shader);
                }
            }
        }
    }

    private void renderSceneFromPosition(Vector4 position, Matrix4x4 lightProjection, String shaderPosName, ShadowMap shadowMap) {

        Matrix4x4 lightView = Matrix4x4.lookAt(position, new Vector4(0, 0, 0, 1), new Vector4(0, 1, 0, 1));

        Matrix4x4 lightSpaceMatrix = lightProjection.$times(lightView);

        try (ShaderUse su = new ShaderUse(shaders.shadowGenShader)) {
            shadowMap.setup(su.shader, lightSpaceMatrix);

            renderScene(shaders.shadowGenShader);
        }


        try (ShaderUse wrap = new ShaderUse(getMainShader())) {
            wrap.shader.setMatrix(shaderPosName, lightSpaceMatrix);
        }

        // Back to default framebugger (screen)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


}
