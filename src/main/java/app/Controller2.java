package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Controller2{
    @FXML
    private AnchorPane rootPane2;
    @FXML
    private VBox WelcomeBox;
    @FXML
    private SplitPane rootSplitPane;
    @FXML
    private Button billing;
    @FXML
    private Button logfile;
    @FXML
    private Button accountman;
    @FXML
    private Button reservationman;
    @FXML
    private Button roomman;
    @FXML
    private Button logOutbtn;


    private static final Logger LOGGER = Logger.getLogger(Controller2.class.getName());

    public void initialize(){

        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
            Label welcLabel = new Label("Hi, "+ SQLProcedures.getFirstName(conn, Application.appEmail));
            WelcomeBox.getChildren().add(welcLabel);
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
        }});
    }

    @FXML
    public void logOut(){
        Application.appEmail = null;
        try {
            new SceneSwitch(rootPane2, "/layout.fxml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Incapability to leave Scene", e);
        }
    }
}
