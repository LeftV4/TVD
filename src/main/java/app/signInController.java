package app;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class signInController {
    @FXML RadioButton adminRadio;
    @FXML RadioButton staffRadio;
    @FXML RadioButton userRadio;
    @FXML TextField regPhone;
    @FXML TextField regLname;
    @FXML TextField regFname;
    @FXML PasswordField regConfirm;
    @FXML PasswordField regPassword;
    @FXML TextField regEmail;
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });
    private static final Logger LOGGER = Logger.getLogger(signInController.class.getName());

    String appRole;

    static AnimationTimer timer;

    public void check2(){
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Database.connectDB(connLabel,executor);
            }
        };
        timer.start();
    }
    boolean i=false;
    public void check(){
        Database.connectDB(connLabel,executor);
        if(!i){
            check2();
            i=true;
        }
    }

    public boolean testConnection(){
        if (connLabel.getText().equals("Not Connected")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Database not Connected!");
            alert.show();
            return false;}
        return connLabel.getText().equals("Connected");
    }

    //public void initialize(){ Database.connectDB(connLabel,executor); check();}

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
        infoBox.setVisible(false);
        infoBox.setDisable(true);
    }
    public void logIn(){
        boolean connTest = testConnection();
        if (!connTest) {return;}
        if(loginEmail.getText().isEmpty() || loginPassword.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Please fill all Fields!");
            alert.show();
            return;
        }
        String email = loginEmail.getText();
        String password = loginPassword.getText();

        try (Connection conn = Database.getConnection()) {
            String role = SQLProcedures.login(conn, email, password);

            if (role !=null){
                if (role.equals("admin")) { timer.stop(); new app.SceneSwitch(rootPane, "/adminPanel.fxml");}
                else if (role.equals("staff")) { timer.stop(); new app.SceneSwitch(rootPane, "/staffPanel.fxml");}
                else {timer.stop(); new app.SceneSwitch(rootPane, "/userPanel.fxml");}
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("Invalid email or password!");
                alert.show();
                return;
            }
        }catch (SQLException | IOException e) {
            LOGGER.log(Level.SEVERE, "Login failed", e);
        }
        Application.appEmail = email;
    }




    public void registerUser(){
        boolean connTest = testConnection();
        if (!connTest) {return;}
        String role = "";
        String passcode = "";
        if(regEmail.getText().isEmpty() || regPassword.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!");
            alert.setContentText("Please fill all Fields!");
            alert.show();
            return;
        }
        if (!regPassword.getText().equals(regConfirm.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!");
            alert.setContentText("Passwords do not match!");
            alert.show();
            return;
        }
        if (roleGroup.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("Please select a role!");
            alert.show();
            return;}

        String email = regEmail.getText();
        String password = regPassword.getText();

        Application.appEmail = email;
        if (userRadio.isSelected()) {role = "guest"; rolePass.setVisible(false); rolePass.setDisable(true);}
        else if (staffRadio.isSelected()) {role = "staff"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "staff";}
        else if (adminRadio.isSelected()) {role = "admin"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "admin";}
        appRole = role;
        if (rolePass.isVisible() && !rolePass.getText().equals(passcode)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Please enter passcode");
            alert.show();
            return;
        }


        try (Connection conn = Database.getConnection()) {
            int result = SQLProcedures.registerUser(conn, email, role, password);
            switch (result) {
                case 0: {
                    infoBox.setVisible(true);
                    infoBox.setDisable(false);
                    registerBox.setVisible(false);
                    registerBox.setDisable(true);
                    break;
                }
                case 1: {Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Notice");
                    alert.setContentText("Email already registered");
                    alert.show();
                    return;}
                default: {Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error");
                    alert.setContentText("Registration failed.");
                    alert.show();
                    return;}
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database Error", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!");
            alert.setContentText("Database connection error!");
            alert.show();
            return;
        }
		infoBox.setVisible(true);
		infoBox.setDisable(false);
		registerBox.setVisible(false);
		registerBox.setDisable(true);
    }
    public void registerInfo(){
        boolean connTest = testConnection();
        if (!connTest) {return;}
        if(regPhone.getText().isEmpty() || regFname.getText().isEmpty() || regLname.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error!");
            alert.setContentText("Please fill all Fields!");
            alert.show();
            return;
		}
        String fname = regFname.getText();
        String lname = regLname.getText();
        String phone = regPhone.getText();

        try (Connection conn = Database.getConnection()) {
            int result = SQLProcedures.registerInfo(conn, fname, lname, phone, appRole, Application.appEmail);
            switch (result) {
                case 0: {
                    if (appRole !=null){
                        if (appRole.equals("admin")) { timer.stop(); new app.SceneSwitch(rootPane, "/adminPanel.fxml"); }
                        else if (appRole.equals("staff")) { timer.stop(); new app.SceneSwitch(rootPane, "/staffPanel.fxml"); }
                        else {timer.stop(); new app.SceneSwitch(rootPane, "/userPanel.fxml"); }
                    }else {Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Error");
                        alert.setContentText("Invalid email or password!");
                        alert.show();
                        return;}
                    break;
                }
                case 1: {Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error");
                    alert.setContentText("Error registering info!");
                    alert.show();
                    return;}
            }
        }catch (SQLException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error registering info", e);
        }

		signInBox.getChildren().remove(loginBox);
        signInBox.getChildren().remove(registerBox);
        signInBox.getChildren().remove(startBox);
        signInBox.getChildren().remove(infoBox);
        try (Connection conn = Database.getConnection()){
            Label welcLabel = new Label("Welcome! " + SQLProcedures.getFirstName(conn, Application.appEmail));
            signInBox.getChildren().add(welcLabel);
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
        }

    }
}