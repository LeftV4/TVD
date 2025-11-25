package app;

import javafx.animation.*;
import javafx.animation.Timeline;

import javafx.application.Platform;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller{
    @FXML RadioButton adminRadio;
    @FXML RadioButton staffRadio;
    @FXML RadioButton userRadio;
    @FXML TextField regPhone;
    @FXML TextField regLname;
    @FXML TextField regFname;
    @FXML PasswordField regConfirm;
    @FXML PasswordField regPassword;
    @FXML TextField regEmail;
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean checking=false;

    String appEmail;
    String appFname;
    String appLname;
    String appRole;

    @FXML private void connectDB() {
        if (checking) return;
        checking = true;

        executor.submit(() -> {
            boolean connected;

            try (Connection conn = Database.getConnection()) {
                connected = true;
            } catch (Exception e) {
                connected = false;
            }

            boolean finalConnected = connected;

            Platform.runLater(() -> {
                if (finalConnected) {
                    connLabel.setText("Connected");
                    connLabel.setStyle("-fx-text-fill: green");
                } else {
                    connLabel.setText("Not Connected");
                    connLabel.setStyle("-fx-text-fill: red");
                }

                checking = false;
            });
        });
    }


    public void check(){
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                connectDB();
            }
        };
        timer.start();
    }

    public boolean testConnection(){
        if (connLabel.getText().equals("Not Connected")) {messLabel.setText("Database not connected");return false;}
        if (connLabel.getText().equals("Connected")) {return true;}
        return false;
    }

    public void initialize(){ connectDB(); check();}

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
        messLabel.setText("");
    }
    public void logIn(){
        boolean connTest = testConnection();
        if (!connTest) {return;}
        if(loginEmail.getText().isEmpty() || loginPassword.getText().isEmpty()){
            messLabel.setText("Please fill all Fields!");
            return;
        }
        String email = loginEmail.getText();
        String password = loginPassword.getText();

        System.out.println("email: " + email);
        System.out.println("password: " + password);
        try (Connection conn = Database.getConnection()) {
            String role = SQLProcedures.login(conn, email, password);
            System.out.println(role);

            if (role !=null){
                if (role.equals("admin")) { /*ADMIN PANEL*/}
                else if (role.equals("staff")) { /*STAFF PANEL*/}
                else {/*USER PANEL*/}
            }else {messLabel.setText("Invalid Email or Password!"); return;}
        }catch (SQLException e) {e.printStackTrace();}

        signInBox.getChildren().remove(loginBox);
        signInBox.getChildren().remove(registerBox);
        signInBox.getChildren().remove(startBox);
        signInBox.getChildren().remove(infoBox);
        rootPane.getChildren().remove(messLabel);
        appEmail = email;
        try (Connection conn = Database.getConnection()){
            Label welcLabel = new Label("Welcome! " + SQLProcedures.getFirstName(conn, appEmail));
            signInBox.getChildren().add(welcLabel);
        }catch (SQLException e) {e.printStackTrace();}

    }
    public void registerUser(){
        boolean connTest = testConnection();
        if (!connTest) {return;}
        String role = "";
        String passcode = "";
        if(regEmail.getText().isEmpty() || regPassword.getText().isEmpty()){
            messLabel.setText("Please fill all Fields!");
            return;
        }
        if (!regPassword.getText().equals(regConfirm.getText())){
            messLabel.setText("Passwords do not match!");
            return;
        }
        if (roleGroup.getSelectedToggle() == null) { messLabel.setText("Please select a role!"); return;}

        String email = regEmail.getText();
        String password = regPassword.getText();

        appEmail = email;
        if (userRadio.isSelected()) {role = "user"; rolePass.setVisible(false); rolePass.setDisable(true);}
        else if (staffRadio.isSelected()) {role = "staff"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "staff";}
        else if (adminRadio.isSelected()) {role = "admin"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "admin";}
        appRole = role;
        if (rolePass.isVisible() && !rolePass.getText().equals(passcode)){messLabel.setText("Please enter passcode"); return;}
        Connection conn = null;
        try {
            conn = Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (conn != null) {
            int result = SQLProcedures.registerUser(conn, email, role, password);
            switch (result) {
                case 0: {messLabel.setText("User successfully registered"); break;}
                case 1: {messLabel.setText("Email already registered!"); return;}
            }
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
            messLabel.setText("Please fill all Fields!");
            return;
		}
        String fname = regFname.getText();
        String lname = regLname.getText();
        String phone = regPhone.getText();

        try (Connection conn = Database.getConnection()) {
            int result = SQLProcedures.registerInfo(conn, fname, lname, phone, appRole, appEmail);
            System.out.println(appRole);
            System.out.println(appEmail);
            switch (result) {
                case 0: {
                    if (appRole !=null){
                        if (appRole.equals("admin")) { /*ADMIN PANEL*/}
                        else if (appRole.equals("staff")) { /*STAFF PANEL*/}
                        else {/*USER PANEL*/}
                    }else {messLabel.setText("Invalid Email or Password!"); return;}
                    break;
                }
                case 1: {messLabel.setText("Error registering info!"); return;}
            }
        }catch (SQLException e) {e.printStackTrace();}

		signInBox.getChildren().remove(loginBox);
        signInBox.getChildren().remove(registerBox);
        signInBox.getChildren().remove(startBox);
        signInBox.getChildren().remove(infoBox);
        rootPane.getChildren().remove(messLabel);
        try (Connection conn = Database.getConnection()){
            Label welcLabel = new Label("Welcome! " + SQLProcedures.getFirstName(conn, appEmail));
            signInBox.getChildren().add(welcLabel);
        }catch (SQLException e) {e.printStackTrace();}

    }
}