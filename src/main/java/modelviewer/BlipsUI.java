package modelviewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Don't want to create Blips for every single little thing, e.g. turning lights on and off.  Actually has opposite of
 *  desired effect, and spreads parts of the system that could be localised, all over the code.
 *  Instead, allow components to add things to the UI.  Has advantage that UI is now completely decoupled, making unit
 *  testing easier and making it possible to e.g. drop in a completely different UI later.
 */

class BlipUI implements Blip {}
class BlipUIContainer extends BlipUI {}

class BlipUICheckbox extends BlipUI {
    public String name;
    public boolean initialState;
    public Consumer<Boolean> onChanged;
    Optional<Integer> shortcut;

    private BlipUICheckbox() {}

    static BlipUICheckbox create(String name, boolean initialState, Consumer<Boolean> onChanged, Optional<Integer> shortcut) {
        BlipUICheckbox out = new BlipUICheckbox();
        out.name = name;
        out.initialState = initialState;
        out.onChanged = onChanged;
        out.shortcut = shortcut;
        return out;
    }
}

class BlipUIButton extends BlipUI {
    public String label;
    public Runnable onClicked;
    Optional<Integer> shortcut;

    private BlipUIButton() {}

    static BlipUIButton create(String label, Runnable onClicked, Optional<Integer> shortcut) {
        BlipUIButton out = new BlipUIButton();
        out.label = label;
        out.onClicked = onClicked;
        out.shortcut = shortcut;
        return out;
    }
}

class BlipUIFileDialogButton extends BlipUI {
    public String label;
    public Consumer<File> onFileSelected;
    public String dialogTitle;
    Optional<Integer> shortcut;
    Optional<File> initialDir;

    private BlipUIFileDialogButton() {}

    static BlipUIFileDialogButton create(String label, String dialogTitle, Consumer<File> onFileSelected, Optional<Integer> shortcut, Optional<File> initialDir) {
        BlipUIFileDialogButton out = new BlipUIFileDialogButton();
        out.label = label;
        out.dialogTitle = dialogTitle;
        out.onFileSelected = onFileSelected;
        out.shortcut = shortcut;
        out.initialDir = initialDir;
        return out;
    }
}

class BlipUITextField extends BlipUI {
    public Optional<String> label;
    public String initialState;
    public Consumer<String> onChanged;

    private BlipUITextField() {}

    static BlipUITextField create(Optional<String> label, String initialState, Consumer<String> onChanged) {
        BlipUITextField out = new BlipUITextField();
        out.label = label;
        out.initialState = initialState;
        out.onChanged = onChanged;
        return out;
    }
}

class ComboBoxItem {
    public String value;
    public Runnable onSelected;
    public Optional<Integer> shortcut;

    private ComboBoxItem() {}

    static ComboBoxItem create(String value, Runnable onSelected, Optional<Integer> shortcut) {
        ComboBoxItem out = new ComboBoxItem();
        out.value = value;
        out.onSelected = onSelected;
        out.shortcut = shortcut;
        return out;
    }
}

class BlipUIComboBox extends BlipUI {
    public Optional<String> label;
    public List<ComboBoxItem> items;

    private BlipUIComboBox() {}

    static BlipUIComboBox create(Optional<String> label, List<ComboBoxItem> items) {
        BlipUIComboBox out = new BlipUIComboBox();
        out.label = label;
        out.items = items;
        return out;
    }
}

class BlipUITitledSection extends BlipUI {
    public BlipUIContainer content;
    public String name;

    private BlipUITitledSection() { }

    static BlipUITitledSection create(String name, BlipUIContainer content) {
        BlipUITitledSection out = new BlipUITitledSection();
        out.name = name;
        out.content = content;
        return out;
    }

    static BlipUITitledSection create(String name, BlipUI con) {
        BlipUITitledSection out = new BlipUITitledSection();
        BlipUIContainer content = BlipUIHStack.create(Arrays.asList(con));
        out.name = name;
        out.content = content;
        return out;
    }
}

class BlipUIHStack extends BlipUIContainer {
    public List<BlipUI> elements = new ArrayList<BlipUI>();

    private BlipUIHStack() { }

    static BlipUIHStack create(List<BlipUI> elements) {
        BlipUIHStack out = new BlipUIHStack();
        out.elements = elements;
        return out;
    }
}

