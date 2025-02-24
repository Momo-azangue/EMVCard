package etu.ecole.ensicaen.carteemv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;



public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("simple.fxml"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        Scene scene = new Scene(fxmlLoader.load(), 1020, 640);
        stage.setTitle(bundle.getString("Title"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}