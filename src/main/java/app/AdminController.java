package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AdminController {
    @FXML AnchorPane adminButtonPane;
    @FXML StackPane adminStackPane;
    @FXML TextArea logFileArea;
    @FXML AnchorPane adminContentPane;
    @FXML private AnchorPane rootPane2;
    @FXML private VBox WelcomeBox;
    @FXML private SplitPane rootSplitPane;
    @FXML private Button billing;
    @FXML private Button logfile;
    @FXML private Button accountman;
    @FXML private Button reservationman;
    @FXML private Button roomman;
    @FXML private Button logOutbtn;


    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

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
    public void showLogFile(){
        adminStackPane.getChildren().forEach(node -> node.setVisible(false));
        adminStackPane.getChildren().forEach(node -> node.setDisable(true));
        logFileArea.setVisible(true);
        logFileArea.setDisable(false);
        logFileArea.clear();
        try (Connection conn = Database.getConnection()) {
            String logs = SQLProcedures.getLogFileContent(conn);
            logFileArea.setText(logs);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load logs", e);
        }
    }

    @FXML
    public void logOut(){
        Application.appEmail = null;
        try {
            new SceneSwitch(rootPane2, "/signIn.fxml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Incapability to leave Scene", e);
        }
    }
}
