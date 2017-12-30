package BasicModels;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GuiController implements BlipHandler {
    @FXML public ChoiceBox scene;
    @FXML public HBox others;

    private MainGui app;

    public GuiController() {

    }

    @FXML public void initialize() {
        scene.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue value, String old, String n) {
                System.out.println("Changed to " + n);
                int sceneIdx = 0;
                switch (n) {
                    case "Scene 1":
                        sceneIdx = 0;
                        break;
                    case "Scene 2":
                        sceneIdx = 1;
                        break;
                    case "Scene 3":
                        sceneIdx = 2;
                        break;
                }
                app.handle(new BlipInputChangeScene(sceneIdx));

            }
        });
    }


    @FXML private void test(ActionEvent event) {
        System.out.println("Fired!");
    }

    public void setApp(MainGui app) {
        this.app = app;
    }

    private void addControl(Control control) {
        Platform.runLater(() -> others.getChildren().add(control));
    }

    @Override
    public void handle(Blip blip) {
        if (blip instanceof BlipUIClear) {
            Platform.runLater(() -> others.getChildren().clear());
        }
        else if (blip instanceof BlipUI) {
            if (blip instanceof BlipUIAddCheckbox) {
                BlipUIAddCheckbox v = (BlipUIAddCheckbox) blip;
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

                Platform.runLater(() -> {
                    others.getChildren().add(control);
                    others.getChildren().add(label);
                });

            }
        }
    }
}
