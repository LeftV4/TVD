package app;

import javafx.animation.*;
import javafx.animation.Timeline;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class Controller{
    @FXML Label messLabel;
    @FXML StackPane signInBox;
    @FXML ToggleGroup roleGroup;
    @FXML Label connLabel;
    @FXML VBox loginBox;
    @FXML VBox registerBox;
    @FXML VBox startBox;
    @FXML VBox infoBox;
    @FXML PasswordField rolePass;
    @FXML AnchorPane rootPane;
    @FXML TextField loginEmail;
    @FXML TextField loginPassword;


    private boolean checking=false;
    private boolean last_state=false;
    @FXML
    private void connectDB() {
        System.out.println("connectDB");
        if (checking) return;
        checking = true;
        if (!last_state) {
            connLabel.setText("Connecting...");
            connLabel.setStyle("-fx-text-fill: orange");
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                try (Connection conn = Database.getConnection()) {
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };

        task.setOnSucceeded(event -> {
            boolean connected = task.getValue();
            if (connected) {
                System.out.println("connected");
                connLabel.setText("Connected!");
                connLabel.setStyle("-fx-text-fill: green");
                last_state=true;
            } else {
                System.out.println("not connected");
                connLabel.setText("Connection Failed!");
                connLabel.setStyle("-fx-text-fill: red");
                last_state=false;
            }
            checking = false;
        });

        new Thread(task).start();
    }

    public void check(){
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> connectDB())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void initialize(){
        connectDB();
        check();
    }

    public void startRegister(){
        registerBox.setVisible(true);
        registerBox.setDisable(false);
        startBox.setVisible(false);
        startBox.setDisable(true);
    }
    public void startLogin(){
        loginBox.setVisible(true);
        loginBox.setDisable(false);
        startBox.setVisible(false);
        startBox.setDisable(true);
    }
    public void backStart(){
        loginBox.setVisible(false);
        loginBox.setDisable(true);
        registerBox.setVisible(false);
        registerBox.setDisable(true);
        startBox.setVisible(true);
        startBox.setDisable(false);
    }
    public void logIn(){
        if(loginEmail.getText().isEmpty() || loginPassword.getText().isEmpty()){
            messLabel.setText("Please fill all Fields!");
            messLabel.setStyle("-fx-text-fill: red");
            return;
        }
        signInBox.getChildren().remove(loginBox);
        signInBox.getChildren().remove(registerBox);
        signInBox.getChildren().remove(startBox);
        signInBox.getChildren().remove(loginBox);
        rootPane.getChildren().remove(messLabel);
        Label welcLabel = new Label("Welcome!");
        signInBox.getChildren().add(welcLabel);

    }
    public void registerUser(){

    }
    public void registerInfo(){

    }
}
