package BasicModels;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

public class GuiController {
    @FXML
    public ChoiceBox scene;
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
}
