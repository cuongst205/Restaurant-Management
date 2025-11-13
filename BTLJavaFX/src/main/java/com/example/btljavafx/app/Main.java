package com.example.btljavafx.app;
import java.util.Calendar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.Objects;
public class Main extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/btljavafx/view/LoginView.fxml"));
        Rectangle2D vb = Screen.getPrimary().getVisualBounds();
        double initWidth = Math.max(700, vb.getWidth() * 0.65);
        double initHeight = Math.max(600, vb.getHeight() * 0.75);
        Scene scene = new Scene(loader.load(), initWidth, initHeight);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/btljavafx/view/style.css")).toExternalForm());
        stage.setTitle("Quản lý nhà hàng");
        stage.setMinWidth(700);
        stage.setMinHeight(300);
        stage.setScene(scene);
        stage.show();
    }
    public static void switchScene(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        double w = primaryStage.getWidth();
        double h = primaryStage.getHeight();
        Scene scene = new Scene(loader.load(), w, h);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/com/example/btljavafx/view/style.css")).toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }


}
