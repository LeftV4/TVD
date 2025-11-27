package app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;



public class Application extends javafx.application.Application {

    public static Stage primaryStage;
    public static String appEmail;
    public static String appFname;
    public static String appLname;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/signIn.fxml")));
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Hotel Management System");
        primaryStage.show();
    }
}
