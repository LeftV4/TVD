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

    @FXML Spinner<Integer> singleSpinner;
    @FXML Spinner<Integer> doubleSpinner;
    @FXML Spinner<Integer> suiteSpinner;
    @FXML Label singlePrice;
    @FXML Label doublePrice;
    @FXML Label suitePrice;
    @FXML Label totalCost;
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
        if (singlePrice == null) singlePrice = new Label();
        if (doublePrice == null) doublePrice = new Label();
        if (suitePrice == null) suitePrice = new Label();
        if (totalCost == null) totalCost = new Label();

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


    boolean listenersInitialized = false;
    int cachedSinglePrice, cachedDoublePrice, cachedSuitePrice;
    public void enterReserveRoom(){
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        reserveRoomPane.setVisible(true);
        reserveRoomPane.setDisable(false);

        singleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12));
        doubleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 18));
        suiteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3));

        try (Connection conn = Database.getConnection()) {
            cachedSinglePrice = SQLProcedures.getSinglePrice(conn);
            cachedDoublePrice = SQLProcedures.getDoublePrice(conn);
            cachedSuitePrice = SQLProcedures.getSuitePrice(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching room prices", e);
        }

        if (!listenersInitialized) {
            singleSpinner.valueProperty().addListener((_, _, _) -> {
                singleCart();
                generatePrice();
            });
            doubleSpinner.valueProperty().addListener((_, _, _) -> {
                doubleCart();
                generatePrice();
            });
            suiteSpinner.valueProperty().addListener((_, _, _) -> {
                suiteCart();
                generatePrice();
            });
            listenersInitialized = true;
        }
    }

    private void singleCart() {
        if (singleSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(singlePrice);
        } else {
                singlePrice.setText("Single Room x " + singleSpinner.getValue()+ " =" + (singleSpinner.getValue() * cachedSinglePrice)+ "€");
            if (!cartVbox.getChildren().contains(singlePrice)) {cartVbox.getChildren().add(singlePrice);}
        }
    }

    private void doubleCart() {
        if (doubleSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(doublePrice);
        } else {
                doublePrice.setText("Double Room x " + doubleSpinner.getValue()+ " =" + (doubleSpinner.getValue() * cachedDoublePrice)+ "€");
            if (!cartVbox.getChildren().contains(doublePrice)) {cartVbox.getChildren().add(doublePrice);}
        }
    }

    private void suiteCart() {
        if (suiteSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(suitePrice);
        } else {
                suitePrice.setText("Luxury Suite x " + suiteSpinner.getValue()+ " =" + (suiteSpinner.getValue() * cachedSuitePrice)+ "€");
            if (!cartVbox.getChildren().contains(suitePrice)) {cartVbox.getChildren().add(suitePrice);}
        }
    }
    public int total;
    private void generatePrice() {
            total = (
                    (suiteSpinner.getValue() * cachedSuitePrice)
                            + (singleSpinner.getValue() * cachedSinglePrice)
                            + (doubleSpinner.getValue() * cachedDoublePrice)
            );
            totalCost.setText("Total Cost: " + total + "€");
    }

    public void moveCheckout() {

    }


    public void enterMyReservations(){
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        reservationsPane.setVisible(true);
        reservationsPane.setDisable(false);
    }

    public void enterMyAccount(){
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        myAccountPane.setVisible(true);
        myAccountPane.setDisable(false);
    }






}
