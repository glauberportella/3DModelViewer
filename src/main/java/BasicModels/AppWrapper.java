
package BasicModels;

import Useful.AppParams;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.net.URISyntaxException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AppWrapper extends GLFWKeyCallback implements BlipHandler {
    private Scene currentScene;
    // Keep these around so they can save their lighting etc.
    private ArrayList<Scene> scenes = new ArrayList<>();
    private long window;
    private final BlipHandler app;
    private final ShortcutHandler shortcuts = new ShortcutHandler();

    @Override
    public void handle(Blip blip) {
        if (blip instanceof BlipInputChangeScene) {
            changeScene(((BlipInputChangeScene) blip).getScene());
        }
        else if (blip instanceof BlipInputAnyWindowClosed) {
            glfwSetWindowShouldClose(window, true);
        }
        else if (blip instanceof BlipInputKeyPressed) {
            handleKeyboardInput((BlipInputKeyPressed) blip);
        }

        if (currentScene != null) {
            currentScene.handle(blip);
        }
        shortcuts.handle(blip);
    }

    private void handleKeyboardInput(BlipInputKeyPressed blip) {
        if (blip.action == GLFW_PRESS) {
//            if (blip.keycode == GLFW_KEY_KP_1) app.handle(new BlipInputChangeScene(0));
//            else if (blip.keycode == GLFW_KEY_KP_2) app.handle(new BlipInputChangeScene(1));
//            else if (blip.keycode == GLFW_KEY_KP_3) app.handle(new BlipInputChangeScene(2));
            if (blip.keycode == GLFW_KEY_ESCAPE) app.handle(new BlipInputOpenGlWindowClosed());
        }
    }

    public AppWrapper(BlipHandler app) {
        this.app = app;
    }

    public void runNonBlocking() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                AppWrapper.this.run();
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    private AppParams runPre() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        AppParams params = new AppParams();
        params.heightPixels = 600;
        params.widthPixels = 600;
        params.fovDegrees = 90;

        init(params);
        return params;
    }

    private void runPost() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() {
        AppParams params = runPre();
        try {
            loop(params);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        runPost();
    }

    public void setShouldClose(boolean fromOpenGlWindows) {
        glfwSetWindowShouldClose(window, true);
//        if (fromOpenGlWindows) {
//            onClose.run();
//        }
    }

    @Override public void invoke(long window, int key, int scancode, int action, int mods) {
        currentScene.keyPressedImpl(window, key, scancode, action, mods);
        app.handle(BlipInputKeyPressed.create(key, action));
    }


    private void init(AppParams params) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

//        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Enable MSAA
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(params.widthPixels, params.heightPixels, "Hello GloomyCubeScene!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        glfwSetKeyCallback(window, this);
        glfwSetWindowCloseCallback(window, (i) -> {
            app.handle(new BlipInputOpenGlWindowClosed());
        });

//		glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
//		glfwSetInputMode(id, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

    }

    public void changeScene(int index) {
        app.handle(new BlipSceneReset());

        Scene scene = scenes.get(index);
        currentScene = scene;

        app.handle(BlipUITitledSection.create("Scenes",
                BlipUIComboBox.create(Optional.of("Scene"), new ArrayList<>(Arrays.asList(
                        ComboBoxItem.create("Scene 1", () -> app.handle(new BlipInputChangeScene(0)), Optional.of(GLFW_KEY_KP_1)),
                        ComboBoxItem.create("Scene 2", () -> app.handle(new BlipInputChangeScene(1)), Optional.of(GLFW_KEY_KP_2)),
                        ComboBoxItem.create("Scene 3", () -> app.handle(new BlipInputChangeScene(2)), Optional.of(GLFW_KEY_KP_3))
                )))));

        app.handle(new BlipSceneStart());


    }

    private void loop(AppParams params) throws URISyntaxException {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);

        Scene models = new BasicModelScene(app);
        Scene shadows = new ShinyCubeShadowsScene(app);
        Scene gloomy = new GloomyCubeScene();

        scenes.add(models);
        scenes.add(shadows);
        scenes.add(gloomy);

        changeScene(0);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            currentScene.draw(params);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }
    }

}