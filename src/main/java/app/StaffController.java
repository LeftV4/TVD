package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaffController {

    @FXML private Label helloLabel;
    @FXML private VBox WelcomeBox;

    private static final Logger LOGGER = Logger.getLogger(StaffController.class.getName());

    public void initialize(){
        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
                helloLabel.setText("Welcome "+ SQLProcedures.getFirstName(conn, Application.appEmail) + "!");
                Label welcLabel = new Label("Staff Privileges");
                WelcomeBox.getChildren().add(welcLabel);
            }catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
            }});
    }
}
