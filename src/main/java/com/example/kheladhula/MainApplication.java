package com.example.kheladhula;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    //Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login-screen.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login Screen");
            stage.show();
            //System.out.println("JavaFX Runtime Version: " + System.getProperty("javafx.runtime.version"));

        }catch (IOException e){
            System.out.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }

    }



    public static void main(String[] args) {
        launch();
    }
}