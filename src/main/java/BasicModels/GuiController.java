package BasicModels;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.stream.Collectors;

public class GuiController implements BlipHandler {
    //    @FXML public ChoiceBox scene;
    @FXML public HBox others;

    private double padding = 5.0;
    private MainGui app;

    public GuiController() {

    }

    @FXML public void initialize() {
    }


    @FXML private void test(ActionEvent event) {
        System.out.println("Fired!");
    }

    public void setApp(MainGui app) {
        this.app = app;
    }

    private void addNode(Node control) {
        Platform.runLater(() -> others.getChildren().add(control));
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
        if (blip instanceof BlipUIAddCheckbox) {
            BlipUIAddCheckbox v = (BlipUIAddCheckbox) blip;
            return createCheckbox(v);
        }
        else if (blip instanceof BlipUIAddComboBox) {
            return createComboBox((BlipUIAddComboBox) blip);
        }
        else if (blip instanceof BlipUIAddTitledSection) {
            return createTitledSection((BlipUIAddTitledSection) blip);
        }
        else if (blip instanceof BlipUIAddHStack) {
            return createHBox((BlipUIAddHStack) blip);
        }
        else {
            assert (false);
            return null;
        }
    }

    private Node createCheckbox(BlipUIAddCheckbox v) {
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

//        Platform.runLater(() -> {
//            others.getChildren().add(control);
//            others.getChildren().add(label);
//        });

        if (v.shortcut.isPresent()) {
            BlipInputAddKeyboardShortcut shortcut = BlipInputAddKeyboardShortcut.create(v.shortcut.get(), () -> {
                boolean checked = control.selectedProperty().get();
                control.selectedProperty().set(!checked);
                v.onChanged.accept(!checked);
            });
            app.handle(shortcut);
        }

        HBox box = new HBox();
        box.getChildren().add(label);
        box.getChildren().add(control);
        return box;

    }

    private Node createComboBox(BlipUIAddComboBox v) {
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
//        control.valueProperty().setValue(itemssAsStrings.get(0));

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

//            Platform.runLater(() -> {
//                others.getChildren().add(label);
//                others.getChildren().add(control);
//            });

            HBox box = new HBox();
            box.getChildren().add(label);
            box.getChildren().add(control);
            return box;
        }
        else {
//            Platform.runLater(() -> {
//                others.getChildren().add(control);
//            });
            return control;
        }
    }

    private Node createHBox(BlipUIAddHStack v) {

        HBox control = new HBox();
        control.setPadding(new Insets(padding, padding, padding, padding));
        v.elements.forEach(item -> {
            control.getChildren().add(createNode(item));
        });
        return control;
    }

    private Node createTitledSection(BlipUIAddTitledSection v) {
        TitledPane control = new TitledPane();
        control.setPadding(new Insets(padding, padding, padding, padding));
        control.setText(v.name);
        control.setContent(createNode(v.content));
        control.setCollapsible(false);
        return control;
    }
}
