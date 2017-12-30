package BasicModels;

import org.immutables.value.Value;
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Don't want to create Blips for every single little thing, e.g. turning lights on and off.  Actually has opposite of
 *  desired effect, and spreads parts of the system that could be localised, all over the code.
 *  Instead, allow components to add things to the UI.
 */

class BlipUI implements Blip {}

class BlipUIClear extends BlipUI {}

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

class BlipUIAddSectionArgs {
    public Optional<String> name;
}

class BlipUIAddSection extends BlipUI {
    private final List<BlipUI> elements = new ArrayList<BlipUI>();

    BlipUIAddSection(BlipUIAddSectionArgs args) {

    }
}
