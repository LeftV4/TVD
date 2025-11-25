package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;



public class Application extends javafx.application.Application {

    public static Stage primaryStage;


    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("layout.fxml")));
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Hotel Management System");
        primaryStage.show();
    }
}
