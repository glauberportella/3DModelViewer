package BasicModels;

import Useful.AppParams;
import de.matthiasmann.twl.utils.PNGDecoder;
import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import enterthematrix.Vector4;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE5;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


class FlockingScene extends Scene {
    private final BlipHandler app;
    private final ArrayList<BirdController> birds = new ArrayList<>();
    //    private final ArrayList<Model> models = new ArrayList<>();
    private final ArrayList<FancyQuad> quadModels = new ArrayList<>();
    private final ArrayList<FancyCube> axisMarkers = new ArrayList<>();
    //    private final Shader standardShader;
    private CameraRotatingAroundOrigin camera;
    private final FlockingLighting lighting;
    //    private final Shader shadowGenShader;
    private final ShaderStoreFlocking shaders = new ShaderStoreFlocking();
    private Mesh[] meshes;

    private Material birdMaterial, birdLeaderMaterial;
    private boolean drawAxisMarkers = true;
    private boolean renderToDepth = true;
    private boolean renderDepthFramebuffer = false;
    private boolean drawFloor = false;
    private boolean drawModel = true;
    private boolean renderLightsEnabled = true;
    private boolean shadowsEnabled = true;
    private boolean debugShader = false;

    private List<BlipUI> modelUI = new ArrayList<BlipUI>();
    private final List<BlipUI> basicUi = new ArrayList<BlipUI>();
    private final List<BlipUI> birdUi = new ArrayList<BlipUI>();
    private final Queue<BlipBasicModelScene> queued = new ArrayDeque<>();


    @Override
    public void handle(Blip blip) {
        lighting.handle(blip);

        if (blip instanceof BlipSceneStart) {
            List<BlipUI> uiComplete = new ArrayList<>(basicUi);
            uiComplete.addAll(modelUI);
            app.handle(BlipUITitledSection.create("Scene", BlipUIHStack.create(uiComplete)));
            app.handle(BlipUITitledSection.create("Bird", BlipUIHStack.create(birdUi)));
        }
    }

    FlockingScene(BlipHandler app) throws URISyntaxException {
        this.app = app;
        lighting = new FlockingLighting(app, shaders);

        File initialDir = new File(System.getProperty("user.dir"));
        basicUi.add(BlipUIFileDialogButton.create("Load", "Choose Model File", (file) -> {
            // Cause this will fire in its own thread, and we don't want to trash the meshdata mid-scene
            queued.add(new BlipBasicModelSceneLoadModel(file));
        }, Optional.of(GLFW_KEY_O), Optional.of(initialDir)));

        basicUi.add(BlipUICheckbox.create("Model", drawModel, (v) -> drawModel = v, Optional.empty()));
        basicUi.add(BlipUICheckbox.create("Axis markers", drawAxisMarkers, (v) -> drawAxisMarkers = v, Optional.empty()));
        basicUi.add(BlipUICheckbox.create("Floor", drawFloor, (v) -> drawFloor = v, Optional.of(GLFW_KEY_KP_6)));
        basicUi.add(BlipUICheckbox.create("Shadows", shadowsEnabled, (v) -> shadowsEnabled = v, Optional.of(GLFW_KEY_KP_5)));
        basicUi.add(BlipUICheckbox.create("Lights", renderLightsEnabled, (v) -> renderLightsEnabled = v, Optional.empty()));
        basicUi.add(BlipUIButton.create("Tick", () -> doOneTick(), Optional.of(GLFW_KEY_SPACE)));

        birdLeaderMaterial = new Material("Bird", new Vector3(1.0f, 0, 0), Vector3.fill(1f), Vector3.fill(1f), 32.0f);
        birdMaterial = new Material("Bird", Vector3.fill(1f), Vector3.fill(1f), Vector3.fill(1.0f), 32.0f);

        birdUi.add(BlipUITextField.create(Optional.of("Bird Ambient"), String.valueOf(birdMaterial.getAmbient().x()), (v) -> {
            float value = birdMaterial.getAmbient().x();
            try {
                value = Float.parseFloat(v);
            }
            catch (Exception e) {}
            birdMaterial = new Material(birdMaterial.getName(), Vector3.fill(value), birdMaterial.getDiffuse(), birdMaterial.getSpecular(), birdMaterial.getShininess());
        }));

        birdUi.add(BlipUITextField.create(Optional.of("Bird Diffuse"), String.valueOf(birdMaterial.getAmbient().x()), (v) -> {
            float value = birdMaterial.getDiffuse().x();
            try {
                value = Float.parseFloat(v);
            }
            catch (Exception e) {}
            birdMaterial = new Material(birdMaterial.getName(), birdMaterial.getAmbient(), Vector3.fill(value), birdMaterial.getSpecular(), birdMaterial.getShininess());
        }));

        birdUi.add(BlipUITextField.create(Optional.of("Specular"), String.valueOf(birdMaterial.getAmbient().x()), (v) -> {
            float value = birdMaterial.getSpecular().x();
            try {
                value = Float.parseFloat(v);
            }
            catch (Exception e) {}
            birdMaterial = new Material(birdMaterial.getName(), birdMaterial.getAmbient(), birdMaterial.getDiffuse(), Vector3.fill(value), birdMaterial.getShininess());
        }));

        birdUi.add(BlipUITextField.create(Optional.of("Shininess"), String.valueOf(birdMaterial.getAmbient().x()), (v) -> {
            float value = birdMaterial.getShininess();
            try {
                value = Float.parseFloat(v);
            }
            catch (Exception e) {}
            birdMaterial = new Material(birdMaterial.getName(), Vector3.fill(value), birdMaterial.getDiffuse(), birdMaterial.getSpecular(), value);
        }));

        Materials materials = new Materials();
        TextureFromFile texture = new TextureFromFile("../images/container2.png", PNGDecoder.Format.RGBA);
        TextureFromFile specularMap = new TextureFromFile("../images/container2_specular.png", PNGDecoder.Format.RGBA);

        // Scale, translate, then rotate.
        // Putting into a -1 to 1 space
        {
            int numBirdsX = 1;
            int numBirdsY = 1;
            int numBirdsZ = 1;
            boolean leader = true;

            for (int x = 0; x < numBirdsX; x ++) {
                for (int y = 0; y < numBirdsY; y++)
                    for (int z = 0; z < numBirdsZ; z++) {
                        // Cube goes -0.5f to 0.5f
                        // Want 10 cubes in a -1 to 1 space
                        // So each cube can be max 0.2 across, spaced every 0.2f
                        float xPos = x * (2.0f / numBirdsX) - 1.0f;
                        float yPos = y * (2.0f / numBirdsY) - 1.0f;
                        float zPos = z * (2.0f / numBirdsZ) - 1.0f;
                        Vector4 pos = new Vector4(xPos, yPos, zPos, 1);
                        Matrix4x4 scale = Matrix4x4.scale(0.1f); // to 0.05 box, taking up 25% of space
                        Material material = birdMaterial;
                        if (leader) {
                            material = birdLeaderMaterial;
                        }
                        Vector3 dir = new Vector3(0,0, -1);
                        BirdView birdView = new BirdView(pos, Optional.of(scale), Optional.empty(), material, leader);
                        BirdController bird = new BirdController(birdView, pos.toVector3(), dir);

                        leader = false;

                        birds.add(bird);
                    }
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

    }

    private Shader getMainShader() {
//        if (debugShader) return shaders.debugShader;
        return shaders.standardShader;
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
                birds.forEach(model -> model.draw(projectionMatrix, cameraTranslate, wrap.shader));
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


    public void changeData(Mesh[] meshes, List<BlipUI> modelUi) {
        this.meshes = meshes;
        this.modelUI = modelUi;
    }

    public void doOneTick() {
        float constraint = 1.0f;
        BoundingBox constraints = new BoundingBox(-constraint, constraint, -constraint, constraint, -constraint, constraint);
        List<Bird> b = birds.stream().map(bird -> bird.getModel()).collect(Collectors.toList());
        birds.forEach(bird -> bird.doOneTick(b, constraints));
    }
}
