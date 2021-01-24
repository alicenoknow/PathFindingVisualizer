package agh.oop.pathfindingVisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    public static Stage stage;
    private Parent root;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        String FXMLPath = "resources/window.fxml";
        this.loadFXML(FXMLPath);
        stage.setScene(new Scene(root, 735, 590));
        stage.setResizable(false);
        stage.show();

    }

    public void loadFXML(String path) {
        try {
            URL filePath = this.getClass().getResource(path);
            FXMLLoader fxmlLoader = new FXMLLoader(filePath);
            this.root = fxmlLoader.load();
            root.getStylesheets().add(getClass().getResource("resources/style.css").toExternalForm());
        } catch (NullPointerException | IllegalStateException | IOException e) {
            log.log(Level.SEVERE, "Cannot load fxml file! " + e.getMessage());
            System.exit(0);
        }
    }

}
