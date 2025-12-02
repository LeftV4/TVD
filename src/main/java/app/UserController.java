package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserController {

    @FXML Label suiteAv;
    @FXML Label doubleAv;
    @FXML Label singleAv;
    @FXML Label resNumber;
    @FXML AnchorPane thankPanel;
    @FXML VBox receiptVbox;
    @FXML Label receiptTotal;
    @FXML AnchorPane checkoutPane;
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
        checkOutDate.setDisable(true);

        singleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12));
        doubleSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15));
        suiteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 3));

        //Set Check In Limits
        checkInDate.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        //Set Check-Out Limits
        checkOutDate.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate checkIn = checkInDate.getValue();
                setDisable(empty || (checkIn != null && !date.isAfter(checkIn)));
            }


        });

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

            checkInDate.valueProperty().addListener((_, _, newV) -> {
                checkOutDate.setDisable(false);
                checkOutDate.setValue(null);
                checkOutDate.setDayCellFactory(_ -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        if (!date.isAfter(newV)){
                            setDisable(true);
                        }
                    }
                });
                generatePrice();
            });

            checkOutDate.valueProperty().addListener((_, _, _) -> {
                generatePrice();
                try (Connection conn = Database.getConnection()){
                    singleAv.setText("Current Availability: " + SQLProcedures.getAvailableSingle(conn, checkInDate.getValue(), checkOutDate.getValue()));
                    doubleAv.setText("Current Availability: " + SQLProcedures.getAvailableDouble(conn, checkInDate.getValue(), checkOutDate.getValue()));
                    suiteAv.setText("Current Availability: " + SQLProcedures.getAvailableSuite(conn, checkInDate.getValue(), checkOutDate.getValue()));
                }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error fetching room availability", e);}
            });


            listenersInitialized = true;
        }
    }

    private void singleCart() {
        if (singleSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(singlePrice);
        } else {
                singlePrice.setText("Single Room x " + singleSpinner.getValue()+ " =" + (singleSpinner.getValue() * cachedSinglePrice)+ "€ per day");
            if (!cartVbox.getChildren().contains(singlePrice)) {cartVbox.getChildren().add(singlePrice);}
        }
    }

    private void doubleCart() {
        if (doubleSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(doublePrice);
        } else {
                doublePrice.setText("Double Room x " + doubleSpinner.getValue()+ " =" + (doubleSpinner.getValue() * cachedDoublePrice)+ "€ per day");
            if (!cartVbox.getChildren().contains(doublePrice)) {cartVbox.getChildren().add(doublePrice);}
        }
    }

    private void suiteCart() {
        if (suiteSpinner.getValue() == 0) {
            cartVbox.getChildren().remove(suitePrice);
        } else {
                suitePrice.setText("Luxury Suite x " + suiteSpinner.getValue()+ " =" + (suiteSpinner.getValue() * cachedSuitePrice)+ "€ per day");
            if (!cartVbox.getChildren().contains(suitePrice)) {cartVbox.getChildren().add(suitePrice);}
        }
    }

    public int total;
    private void generatePrice() {
            total = (int) (( (suiteSpinner.getValue() * cachedSuitePrice)
                                        + (singleSpinner.getValue() * cachedSinglePrice)
                                        + (doubleSpinner.getValue() * cachedDoublePrice) ) * ChronoUnit.DAYS.between(checkInDate.getValue(), checkOutDate.getValue() ));
            totalCost.setText("Total Cost: " + total + "€");
    }

    public void moveCheckout() {
        if (checkInDate.getValue() == null || checkOutDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select check-in and check-out dates!");
            alert.showAndWait();
            return;
        }
        else if (total == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Rooms Selected!");
            alert.showAndWait();
            return;}

        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        checkoutPane.setVisible(true);
        checkoutPane.setDisable(false);

        receiptVbox.getChildren().add(singlePrice);
        receiptVbox.getChildren().add(doublePrice);
        receiptVbox.getChildren().add(suitePrice);
        receiptTotal.setText(totalCost.getText());
    }

    public void cancelReservation() {
        receiptVbox.getChildren().clear();
        enterReserveRoom();
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


    public void payByCash() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Payment Confirmation");
        alert.setHeaderText("Make reservation and pay " + total + "€ with cash?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try (Connection conn = Database.getConnection() ){
                int id = SQLProcedures.makeReservation(conn, Application.appEmail, Date.valueOf(checkInDate.getValue()), Date.valueOf(checkOutDate.getValue()), singleSpinner.getValue(), doubleSpinner.getValue(), suiteSpinner.getValue());
                SQLProcedures.registerPayment(conn,id,total, "card");
            }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error registering reservation", e);}

            userStackPane.getChildren().forEach(node -> node.setVisible(false));
            userStackPane.getChildren().forEach(node -> node.setDisable(true));
            thankPanel.setVisible(true);
            thankPanel.setDisable(false);
        }
    }

    public void payByCard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Payment Confirmation");
        alert.setHeaderText("Make reservation and " + total + "€ with card?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try (Connection conn = Database.getConnection() ){
                 int id = SQLProcedures.makeReservation(conn, Application.appEmail, Date.valueOf(checkInDate.getValue()), Date.valueOf(checkOutDate.getValue()), singleSpinner.getValue(), doubleSpinner.getValue(), suiteSpinner.getValue());
                 SQLProcedures.registerPayment(conn,id,total, "card");
            }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error registering reservation", e);}
            userStackPane.getChildren().forEach(node -> node.setVisible(false));
            userStackPane.getChildren().forEach(node -> node.setDisable(true));
            thankPanel.setVisible(true);
            thankPanel.setDisable(false);
        }
    }

    public void showReservations(){

    }
}
