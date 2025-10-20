package com.example.btljavafx.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btljavafx/view/LoginView.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/btljavafx/view/style.css")).toExternalForm());
        stage.setTitle("Quản lý nhà hàng");
        stage.setScene(scene);
        stage.show();
    }

    public static void switchScene(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 1000, 700);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/example/btljavafx/view/style.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}
