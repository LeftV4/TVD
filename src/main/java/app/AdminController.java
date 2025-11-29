package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AdminController {
    @FXML Label helloLabel;
    @FXML AnchorPane adminButtonPane;
    @FXML StackPane adminStackPane;
    @FXML TextArea logFileArea;
    @FXML AnchorPane userArea;
    @FXML VBox userList;
    @FXML TextField selUserfName;
    @FXML TextField selUserlName;
    @FXML TextField selUserEmail;
    @FXML TextField selUserPhone;
    @FXML AnchorPane accountDetails;
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

    String []totalUsers;
    int userID;
    String userEmail;
    String role;

    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

    public void initialize(){
        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
            helloLabel.setText("Welcome "+ SQLProcedures.getFirstName(conn, Application.appEmail) + "!");
            Label welcLabel = new Label("Admin Privileges");
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
    public void showUsers(){
        adminStackPane.getChildren().forEach(node -> node.setVisible(false));
        adminStackPane.getChildren().forEach(node -> node.setDisable(true));
        userArea.setVisible(true);
        userArea.setDisable(false);
        accountDetails.setVisible(false);
        accountDetails.setDisable(true);
        try (final Connection conn = Database.getConnection()) {
            String users = SQLProcedures.getUsers(conn);
            totalUsers = users.split("\n");
            userList.getChildren().clear();
            for(String email: totalUsers){
                // create an HBox for each user row

                HBox row = new HBox();
                row.setSpacing(10);
                row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                // label containing the user email
                Label emailField = new Label();
                emailField.setPrefWidth(200);
                emailField.setStyle("-fx-font-size: 16px;");
                emailField.setText(email);

                // edit button
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(e -> {
                    accountDetails.setVisible(true);
                    accountDetails.setDisable(false);
                    selUserfName.setDisable(false);
                    selUserfName.setVisible(true);
                    try (Connection conn2 = Database.getConnection();) {
                        userEmail = emailField.getText();
                        selUserfName.setText(SQLProcedures.getFirstName(conn2, userEmail));
                        selUserlName.setText(SQLProcedures.getLastName(conn2, userEmail));
                        selUserEmail.setText(userEmail);
                        selUserPhone.setText(SQLProcedures.getPhone(conn2, userEmail));
                    } catch(SQLException ex){
                        LOGGER.log(Level.SEVERE, "Failed to load users info", ex);
                    }
                });

                // delete button
                Button deleteBtn = new Button("Delete");
                deleteBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    try (Connection conn2 = Database.getConnection()) {
                        SQLProcedures.deleteUser(conn2, email); // implementation to delete a user? i shall try it soon
                        userList.getChildren().remove(row);
                        accountDetails.setVisible(false);
                        accountDetails.setDisable(true);
                    } catch (SQLException ex) {
                        LOGGER.log(Level.SEVERE, "Failed to delete user", ex);
                    }
                });
                row.getChildren().addAll(emailField, editBtn, deleteBtn);
                // add row to your UI container
                userList.getChildren().add(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
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
    int status;
    @FXML
    public void updateUser(){
        try(Connection conn = Database.getConnection()){
            role = SQLProcedures.getRole(conn, userEmail);
            status = SQLProcedures.updateInfo(conn, selUserfName.getText(), selUserlName.getText(), selUserPhone.getText(), role, userEmail, selUserEmail.getText());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user", e);
        }
        showUsers();
    }


}




