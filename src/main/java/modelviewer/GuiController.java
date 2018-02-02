package modelviewer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;


// Controls the JavaFX mini GUI, and handles requests to dynamically build the UI
public class GuiController implements BlipHandler {
    @FXML public VBox others;

    private final double padding = 5.0;
    private final double inner = 3.0;
    private MainGui app;
    private Stage stage;
    Consumer<Bounds> onBoundsChanged;

    public GuiController() {
    }

    @FXML public void initialize() {
    }


    public void setApp(MainGui app, Stage stage) {
        this.app = app;
        this.stage = stage;
    }

    private void addNode(Node control) {
        Platform.runLater(() -> {
            others.getChildren().add(control);
            Bounds bounds = stage.getScene().getRoot().layoutBoundsProperty().get();
            onBoundsChanged.accept(bounds);
        });
    }

    @Override
    public void handle(Blip blip) {
        if (blip instanceof BlipSceneReset) {
            Platform.runLater(() -> others.getChildren().clear());
        }
        else if (blip instanceof BlipUI) {
            handleBuildUI((BlipUI) blip);
        }
    }

    private void handleBuildUI(BlipUI blip) {
        addNode(createNode(blip));

    }

    private Node createNode(BlipUI blip) {
        if (blip instanceof BlipUICheckbox) {
            BlipUICheckbox v = (BlipUICheckbox) blip;
            return createCheckbox(v);
        }
        else if (blip instanceof BlipUIComboBox) {
            return createComboBox((BlipUIComboBox) blip);
        }
        else if (blip instanceof BlipUITextField) {
            return createTextField((BlipUITextField) blip);
        }
        else if (blip instanceof BlipUITitledSection) {
            return createTitledSection((BlipUITitledSection) blip);
        }
        else if (blip instanceof BlipUIHStack) {
            return createHBox((BlipUIHStack) blip);
        }
        else if (blip instanceof BlipUIVStack) {
            return createVBox((BlipUIVStack) blip);
        }
        else if (blip instanceof BlipUIFileDialogButton) {
            return createFileDialogButton((BlipUIFileDialogButton) blip);
        }
        else if (blip instanceof BlipUIButton) {
            return createButton((BlipUIButton) blip);
        }
        else {
            assert (false);
            return null;
        }
    }

    private Node createButton(BlipUIButton v) {
        Button control = new Button();
        control.setText(v.label);
        control.setOnMouseClicked((event) -> {
            v.onClicked.run();
        });

        if (v.shortcut.isPresent()) {
            BlipInputAddKeyboardShortcut shortcut = BlipInputAddKeyboardShortcut.create(v.shortcut.get(), v.onClicked::run);
            app.handle(shortcut);
        }

        return control;
    }
    private Node createFileDialogButton(BlipUIFileDialogButton v) {
        Runnable onButtonClicked = () -> {
            FileChooser fileChooser = new FileChooser();
            v.initialDir.ifPresent(file -> fileChooser.setInitialDirectory(file));
            fileChooser.setTitle(v.dialogTitle);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                v.onFileSelected.accept(file);
            }
        };

        Button control = new Button();
        control.setText(v.label);
        control.setOnMouseClicked((event) -> {
            onButtonClicked.run();
                });

        if (v.shortcut.isPresent()) {
            BlipInputAddKeyboardShortcut shortcut = BlipInputAddKeyboardShortcut.create(v.shortcut.get(), onButtonClicked::run);
            app.handle(shortcut);
        }

        return control;
    }


    private Node createCheckbox(BlipUICheckbox v) {
        CheckBox control = new CheckBox();
        control.setSelected(v.initialState);
        control.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                v.onChanged.accept(t1);
            }
        });

        Label label = new Label();
        label.setLabelFor(control);
        label.setText(v.name);
        label.setOnMouseClicked(mouseEvent -> {
            boolean checked = control.selectedProperty().get();
            control.selectedProperty().set(!checked);
            v.onChanged.accept(!checked);
        });

        if (v.shortcut.isPresent()) {
            BlipInputAddKeyboardShortcut shortcut = BlipInputAddKeyboardShortcut.create(v.shortcut.get(), () -> {
                boolean checked = control.selectedProperty().get();
                control.selectedProperty().set(!checked);
                v.onChanged.accept(!checked);
            });
            app.handle(shortcut);
        }

        HBox box = new HBox();
        box.setSpacing(inner);
        box.getChildren().add(label);
        box.getChildren().add(control);
        return box;

    }

    private Node createTextField(BlipUITextField v) {
        TextField control = new TextField();
            control.setText(v.initialState);
        control.setMaxWidth(50f);
        control.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                v.onChanged.accept(t1);
            }
        });

        if (v.label.isPresent()) {
            Label label = new Label();
            label.setLabelFor(control);
            label.setText(v.label.get());

            HBox box = new HBox();
            box.setSpacing(inner);
            box.getChildren().add(label);
            box.getChildren().add(control);
            return box;
        }
        else {
            return control;
        }

    }
    private Node createComboBox(BlipUIComboBox v) {
        ComboBox control = new ComboBox();
        List<String> itemssAsStrings = v.items.stream().map(it -> it.value).collect(Collectors.toList());
        control.setItems(FXCollections.observableArrayList(itemssAsStrings));
        control.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue value, String old, String n) {
                v.items.forEach(item -> {
                    if (item.value.equals(n)) {
                        item.onSelected.run();
                    }
                });
            }
        });

        v.items.forEach(item -> {
            if (item.shortcut.isPresent()) {
                BlipInputAddKeyboardShortcut shortcut = BlipInputAddKeyboardShortcut.create(item.shortcut.get(), () -> {
                    control.valueProperty().setValue(item.value);
                    item.onSelected.run();
                });
                app.handle(shortcut);
            }

        });

        if (v.label.isPresent()) {
            Label label = new Label();
            label.setLabelFor(control);
            label.setText(v.label.get());

            HBox box = new HBox();
            box.setSpacing(inner);
            box.getChildren().add(label);
            box.getChildren().add(control);
            return box;
        }
        else {
            return control;
        }
    }

    private Node createHBox(BlipUIHStack v) {
        HBox control = new HBox();
        control.setPadding(new Insets(padding, padding, padding, padding));
        control.setSpacing(padding);
        v.elements.forEach(item -> {
            control.getChildren().add(createNode(item));
        });
        return control;
    }

    private Node createVBox(BlipUIVStack v) {
        VBox control = new VBox();
        control.setPadding(new Insets(padding, padding, padding, padding));
        control.setSpacing(padding);
        v.elements.forEach(item -> {
            control.getChildren().add(createNode(item));
        });
        return control;
    }


    private Node createTitledSection(BlipUITitledSection v) {
        TitledPane control = new TitledPane();
        control.setPadding(new Insets(padding, padding, padding, padding));
        control.setText(v.name);
        control.setContent(createNode(v.content));
        control.setCollapsible(false);
        return control;
    }
}
