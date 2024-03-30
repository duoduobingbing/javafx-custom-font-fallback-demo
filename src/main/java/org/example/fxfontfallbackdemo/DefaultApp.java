package org.example.fxfontfallbackdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class DefaultApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        final URL fxmlResource = DefaultApp.class.getResource("default-view.fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        final Parent root = fxmlLoader.load();
        final DefaultController defaultController = fxmlLoader.getController();
        final Scene scene = new Scene(root, 700, 440);
        stage.setTitle("FontFallbackDemo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}