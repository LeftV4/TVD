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
        if (connLabel.getText().equals("Not Connected")) {messLabel.setText("Database not connected");return false;}
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

        try (Connection conn = Database.getConnection()) {
            String role = SQLProcedures.login(conn, email, password);
            System.out.println(role);

            if (role !=null){
                //noinspection StatementWithEmptyBody
                if (role.equals("admin")) { timer.stop(); new app.SceneSwitch(rootPane, "/adminPanel.fxml");}
                else //noinspection StatementWithEmptyBody
                    if (role.equals("staff")) { /*STAFF PANEL*/}
                else {/*USER PANEL*/}
            }else {messLabel.setText("Invalid Email or Password!"); return;}
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

        Application.appEmail = email;
        if (userRadio.isSelected()) {role = "user"; rolePass.setVisible(false); rolePass.setDisable(true);}
        else if (staffRadio.isSelected()) {role = "staff"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "staff";}
        else if (adminRadio.isSelected()) {role = "admin"; rolePass.setVisible(true); rolePass.setDisable(false); passcode = "admin";}
        appRole = role;
        if (rolePass.isVisible() && !rolePass.getText().equals(passcode)){messLabel.setText("Please enter passcode"); return;}


        try (Connection conn = Database.getConnection()) {
            int result = SQLProcedures.registerUser(conn, email, role, password);
            switch (result) {
                case 0: {
                    messLabel.setText("User successfully registered");
                    // Only advance UI if registration was successful
                    infoBox.setVisible(true);
                    infoBox.setDisable(false);
                    registerBox.setVisible(false);
                    registerBox.setDisable(true);
                    break;
                }
                case 1: {messLabel.setText("Email already registered!"); return;}
                default: {messLabel.setText("Registration failed."); return;}
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database Error", e);
            messLabel.setText("Database connection error");
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
            int result = SQLProcedures.registerInfo(conn, fname, lname, phone, appRole, Application.appEmail);
            System.out.println(appRole);
            System.out.println(Application.appEmail);
            switch (result) {
                case 0: {
                    if (appRole !=null){
                        //noinspection StatementWithEmptyBody
                        if (appRole.equals("admin")) { /*ADMIN PANEL*/}
                        else //noinspection StatementWithEmptyBody
                            if (appRole.equals("staff")) { /*STAFF PANEL*/}
                        else {/*USER PANEL*/}
                    }else {messLabel.setText("Invalid Email or Password!"); return;}
                    break;
                }
                case 1: {messLabel.setText("Error registering info!"); return;}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering info", e);
        }

		signInBox.getChildren().remove(loginBox);
        signInBox.getChildren().remove(registerBox);
        signInBox.getChildren().remove(startBox);
        signInBox.getChildren().remove(infoBox);
        rootPane.getChildren().remove(messLabel);
        try (Connection conn = Database.getConnection()){
            Label welcLabel = new Label("Welcome! " + SQLProcedures.getFirstName(conn, Application.appEmail));
            signInBox.getChildren().add(welcLabel);
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
        }

    }
}