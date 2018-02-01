package modelviewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;


public class MainGui extends Application implements BlipHandler {
    private modelviewer.AppWrapper app;
    private Stage primaryStage;
    private GuiController gui;

    public static void main(String[] args) {
        launch(args);
    }

    public void handle(Blip blip) {
        gui.handle(blip);
        app.handle(blip);

        if (blip instanceof BlipInputAnyWindowClosed) {
            Platform.runLater(() -> primaryStage.close());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        app = new AppWrapper(this);
        app.runNonBlocking();

        primaryStage.setTitle("3D Model Viewer");

        Parent root = null;
        try {
            URL resource = AppWrapper.class.getResource("/UI/MainGui.fxml");
            assert (resource != null);
            FXMLLoader loader = new FXMLLoader(resource);
            root = loader.load();
            assert (root != null);
            gui = loader.getController();
            assert (gui != null);
            gui.setApp(this, primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert (root != null);

//        Button changeScene1 = new Button();
//        changeScene1.setText("Scene 1");
//        changeScene1.setOnAction(new BlipHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                app.changeScene(0);
//            }
//        });
//
//        Button changeScene2 = new Button();
//        changeScene2.setText("Scene 2");
//        changeScene2.setOnAction(new BlipHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                app.changeScene(1);
//            }
//        });
//
//
//        StackPane root = new StackPane();
//        root.getChildren().add(changeScene1);
//        root.getChildren().add(changeScene2);
//        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                MainGui.this.handle(new BlipInputOpenGlWindowClosed());
                //app.setShouldClose(false);
            }
        });
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                handle(new BlipInputGuiWindowClosed());
            }
        });



    }
}
