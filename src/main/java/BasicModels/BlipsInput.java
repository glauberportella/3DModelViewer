package BasicModels;

class BlipInput implements  Blip {}

class BlipInputChangeScene extends BlipInput {
    public int getScene() {
        return scene;
    }

    private int scene;

    BlipInputChangeScene(int scene) {
        this.scene = scene;
    }
}

class BlipInputKeyPressed extends BlipInput {
    public int keycode, action;

    private BlipInputKeyPressed() {}

    static BlipInputKeyPressed create(int keycode, int action) {
        BlipInputKeyPressed out = new BlipInputKeyPressed();
        out.keycode = keycode;
        out.action = action;
        return out;
    }
}

class BlipInputAnyWindowClosed extends BlipInput {}
class BlipInputOpenGlWindowClosed extends BlipInputAnyWindowClosed {}
class BlipInputGuiWindowClosed extends BlipInputAnyWindowClosed {}

class BlipInputAddKeyboardShortcut extends BlipInput {
    public int keycode;
    Runnable onPressed;

    private BlipInputAddKeyboardShortcut() {}

    static BlipInputAddKeyboardShortcut create(int keycode, Runnable onPressed) {
        BlipInputAddKeyboardShortcut out = new BlipInputAddKeyboardShortcut();
        out.keycode = keycode;
        out.onPressed = onPressed;
        return out;
    }
}