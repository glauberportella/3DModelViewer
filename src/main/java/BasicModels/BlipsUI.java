package BasicModels;

import org.immutables.value.Value;
import scala.Int;

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


class BlipUIAddCheckbox extends BlipUI {
    public String name;
    public boolean initialState;
    public Consumer<Boolean> onChanged;
    Optional<Integer> shortcut;

    static BlipUIAddCheckbox create(String name, boolean initialState, Consumer<Boolean> onChanged, Optional<Integer> shortcut) {
        BlipUIAddCheckbox out = new BlipUIAddCheckbox();
        out.name = name;
        out.initialState = initialState;
        out.onChanged = onChanged;
        out.shortcut = shortcut;
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

class BlipUIAddComboBox extends BlipUI {
    public Optional<String> label;
    public List<ComboBoxItem> items;

    private BlipUIAddComboBox() {}

    static BlipUIAddComboBox create(Optional<String> label, List<ComboBoxItem> items) {
        BlipUIAddComboBox out = new BlipUIAddComboBox();
        out.label = label;
        out.items = items;
        return out;
    }
}

class BlipUIAddTitledSection extends BlipUI {
    public BlipUIContainer content;
    public String name;

    private BlipUIAddTitledSection() { }

    static BlipUIAddTitledSection create(String name, BlipUIContainer content) {
        BlipUIAddTitledSection out = new BlipUIAddTitledSection();
        out.name = name;
        out.content = content;
        return out;
    }

    static BlipUIAddTitledSection create(String name, BlipUI con) {
        BlipUIAddTitledSection out = new BlipUIAddTitledSection();
        BlipUIContainer content = BlipUIAddHStack.create(Arrays.asList(con));
        out.name = name;
        out.content = content;
        return out;
    }
}

class BlipUIAddHStack extends BlipUIContainer {
    public List<BlipUI> elements = new ArrayList<BlipUI>();

    private BlipUIAddHStack() { }

    static BlipUIAddHStack create(List<BlipUI> elements) {
        BlipUIAddHStack out = new BlipUIAddHStack();
        out.elements = elements;
        return out;
    }
}

