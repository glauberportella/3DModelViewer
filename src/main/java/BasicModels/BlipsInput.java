package BasicModels;

class BlipInputChangeScene implements Blip {
    public int getScene() {
        return scene;
    }

    private int scene;

    BlipInputChangeScene(int scene) {
        this.scene = scene;
    }
}


class BlipInputAnyWindowClosed implements Blip {}
class BlipInputOpenGlWindowClosed extends BlipInputAnyWindowClosed {}
class BlipInputGuiWindowClosed extends BlipInputAnyWindowClosed {}