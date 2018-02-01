package modelviewer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;


// This it the JavaFX mini UI, in its separate window
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

        primaryStage.setTitle("Model Viewer");

        Parent root = null;
        try {
            URL resource = AppWrapper.class.getResource("/UI/MainGui.fxml");
            assert (resource != null);
            FXMLLoader loader = new FXMLLoader(resource);
            root = loader.load();
            assert (root != null);
            gui = loader.getController();
            assert (gui != null);
            gui.onBoundsChanged = new Consumer<Bounds>() {
                @Override
                public void accept(Bounds bounds) {
                    app.boundsChanged(bounds);
                }
            };
            gui.setApp(this, primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert (root != null);

        Scene scene = new Scene(root, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
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
