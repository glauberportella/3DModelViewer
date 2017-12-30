package BasicModels;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class ShortcutHandler implements BlipHandler {
    private final Map<Integer, BlipInputAddKeyboardShortcut> shortcuts = new HashMap<>();

    @Override
    public void handle(Blip blip) {
        if (blip instanceof BlipInputAddKeyboardShortcut) {
            BlipInputAddKeyboardShortcut v = (BlipInputAddKeyboardShortcut) blip;
            shortcuts.put(v.keycode, v);
        }
        else if (blip instanceof BlipSceneReset) {
            shortcuts.clear();
        }
        else if (blip instanceof BlipInputKeyPressed) {
            BlipInputKeyPressed v = (BlipInputKeyPressed) blip;
            if (v.action == GLFW_PRESS) {
                if (shortcuts.containsKey(v.keycode)) {
                    shortcuts.get(v.keycode).onPressed.run();
                }
            }
        }
    }
}
