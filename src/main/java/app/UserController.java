package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {

    @FXML AnchorPane userRootPane;
    @FXML Label singleAvailability;
    @FXML Label doubleAvailability;
    @FXML Label suiteAvailability;
    @FXML VBox cartVbox;
    @FXML DatePicker checkInDate;
    @FXML DatePicker checkOutDate;
    @FXML AnchorPane userButtonPane;
    @FXML AnchorPane reserveRoomPane;
    @FXML AnchorPane reservationsPane;
    @FXML AnchorPane myAccountPane;
    @FXML TextField editEmail;
    @FXML TextField editFname;
    @FXML TextField editLname;
    @FXML TextField editPhone;
    @FXML PasswordField editOldPass;
    @FXML PasswordField editNewPass;
    @FXML PasswordField editConfirmPass;
    @FXML AnchorPane userContentPane;
    @FXML StackPane userStackPane;
    @FXML SplitPane rootSplitPane;
    @FXML Label helloLabel;
    @FXML VBox WelcomeBox;

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());


    public void initialize(){
        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
                helloLabel.setText("Welcome "+ SQLProcedures.getFirstName(conn, Application.appEmail) + "!");
            }catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
            }});
    }
    @FXML
    public void logOut(){
        Application.appEmail = null;
        try {
            new SceneSwitch(userRootPane, "/signIn.fxml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to switch to sign-in scene", e);
        }
    }

    public void enterReserveRoom(){

    }

    public void enterMyReservations(){

    }

    public void enterMyAccount(){
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        myAccountPane.setVisible(true);
        myAccountPane.setDisable(false);
    }

    //Checkout button, takes u to the payment scene
    public void moveCheckout() {
    }
}
