package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Date;


public class AdminController {
    @FXML Label helloLabel;
    @FXML AnchorPane adminButtonPane;
    @FXML StackPane adminStackPane;
    @FXML TextArea logFileArea;
    @FXML AnchorPane userArea;
    @FXML AnchorPane resArea;
    @FXML VBox userList;
    @FXML VBox resList;
    @FXML VBox resRoomList;
    @FXML TextField selUserfName;
    @FXML TextField selUserlName;
    @FXML TextField selUserEmail;
    @FXML TextField selResGEmail;
    @FXML TextField selResID;
    @FXML DatePicker selResCheckIn;
    @FXML DatePicker selResCheckOut;
    @FXML TextField selUserPhone;
    @FXML AnchorPane accountDetails;
    @FXML AnchorPane resDetails;
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
    String userEmail;
    String role;
    String resID;
    String []totalRes;
    String []totalResRooms;
    Date checkIn;
    Date checkOut;

    private static final Logger LOGGER = Logger.getLogger(AdminController.class.getName());

    public void initialize(){
        selResCheckIn.setDisable(true);
        selResCheckIn.setStyle("-fx-opacity: 1");
        selResCheckIn.getEditor().setStyle("-fx-opacity: 1");

        selResCheckOut.setDisable(true);
        selResCheckOut.setStyle("-fx-opacity: 1");
        selResCheckOut.getEditor().setStyle("-fx-opacity: 1");
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

    @FXML
    public void showRes(){
        adminStackPane.getChildren().forEach(node -> node.setVisible(false));
        adminStackPane.getChildren().forEach(node -> node.setDisable(true));
        resArea.setVisible(true);
        resArea.setDisable(false);
        resDetails.setVisible(false);
        resDetails.setDisable(true);
        try (final Connection conn = Database.getConnection()) {
            String ress = SQLProcedures.getRes(conn);
            totalRes = ress.split("\n");
            resList.getChildren().clear();
            for(String res: totalRes){
                // create an HBox for each user row

                HBox row = new HBox();
                row.setSpacing(10);
                row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                // label containing the user email
                Label resField = new Label();
                resField.setPrefWidth(20);
                resField.setStyle("-fx-font-size: 16px;");
                resField.setText(res);

                // edit button
                Button editBtn = new Button("Details");
                editBtn.setOnAction(e -> {
                    resDetails.setVisible(true);
                    resDetails.setDisable(false);
                    try (Connection conn2 = Database.getConnection();) {
                        resID = resField.getText();
                        selResGEmail.setText(SQLProcedures.getGuestEmail(conn2, resID));
                        selResID.setText(resID);
                        selResCheckIn.setValue(SQLProcedures.getCheckIn(conn2, resID).toLocalDate());
                        selResCheckOut.setValue(SQLProcedures.getCheckOut(conn2, resID).toLocalDate());
                        selResCheckOut.setEditable(false);
                        selResCheckIn.setEditable(false);
                        resRoomList.getChildren().clear();
                        String ressRooms = SQLProcedures.getResRooms(conn2, resID);
                        totalResRooms = ressRooms.split("\n");
                        for(String rooms: totalResRooms) {


                            HBox row1 = new HBox();
                            row1.setSpacing(10);
                            row1.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                            // label containing the user email
                            Label roomField = new Label();
                            roomField.setPrefWidth(200);
                            roomField.setStyle("-fx-font-size: 18px;");
                            roomField.setText(rooms);
                            row1.getChildren().addAll(roomField);
                            resRoomList.getChildren().add(row1);
                        }
                    } catch(SQLException ex){
                        LOGGER.log(Level.SEVERE, "Failed to load res info", ex);
                    }
                });

                // delete button
                Button deleteBtn = new Button("Delete");
                deleteBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    try (Connection conn2 = Database.getConnection()) {
                        SQLProcedures.deleteRes(conn2, Integer.valueOf(res));
                        resList.getChildren().remove(row);
                        resDetails.setVisible(false);
                        resDetails.setDisable(true);
                    } catch (SQLException ex) {
                        LOGGER.log(Level.SEVERE, "Failed to delete reservation", ex);
                    }
                });
                row.getChildren().addAll(resField, editBtn, deleteBtn);
                // add row to your UI container
                resList.getChildren().add(row);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
        }

    }


}