package app;

import javafx.animation.*;
import javafx.animation.Timeline;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.sql.*;

public class Controller{
    @FXML
    Label connLabel;

    private boolean checking=false;
    private boolean last_state=false;
    @FXML
    private void connectDB() {
        if (checking) return;
        checking = true;
        if (last_state==false) {
            connLabel.setText("Connecting...");
            connLabel.setStyle("-fx-text-fill: orange");
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                try (Connection conn = Database.getConnection()) {
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };

        task.setOnSucceeded(event -> {
            boolean connected = task.getValue();
            if (connected) {
                connLabel.setText("Connected!");
                connLabel.setStyle("-fx-text-fill: green");
                last_state=true;
            } else {
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
                new KeyFrame(Duration.seconds(5), event -> {
                    connectDB();
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void initialize(){
        connectDB();
        check();
    }

}
