package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
    @FXML AnchorPane resDetails;
    @FXML Label singleAvailability;
    @FXML Label doubleAvailability;
    @FXML Label suiteAvailability;
    @FXML Label messLabel;
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
    @FXML VBox resList;
    @FXML VBox resRoomList;
    @FXML TextField selResEmail;
    @FXML DatePicker selResCheckIn;
    @FXML DatePicker selResCheckOut;
    @FXML TextField selResAmount;

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    String []totalRes;
    String []totalResRooms;
    String resID;


    public void initialize(){
        selResCheckIn.setDisable(true);
        selResCheckIn.setStyle("-fx-opacity: 1");
        selResCheckIn.getEditor().setStyle("-fx-opacity: 1");

        selResCheckOut.setDisable(true);
        selResCheckOut.setStyle("-fx-opacity: 1");
        selResCheckOut.getEditor().setStyle("-fx-opacity: 1");

        if (singlePrice == null) singlePrice = new Label();
        if (doublePrice == null) doublePrice = new Label();
        if (suitePrice == null) suitePrice = new Label();
        if (totalCost == null) totalCost = new Label();

        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
                helloLabel.setText("Welcome "+ SQLProcedures.getUserInfo(conn, Application.appEmail, 1) + "!");
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
            cachedSinglePrice = SQLProcedures.getPriceByType(conn,1);
            cachedDoublePrice = SQLProcedures.getPriceByType(conn,2);
            cachedSuitePrice = SQLProcedures.getPriceByType(conn,3);

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
                    singleAv.setText("Current Availability: " + SQLProcedures.getAvailableRooms(conn, checkInDate.getValue(), checkOutDate.getValue(), 1));
                    doubleAv.setText("Current Availability: " + SQLProcedures.getAvailableRooms(conn, checkInDate.getValue(), checkOutDate.getValue(), 2));
                    suiteAv.setText("Current Availability: " + SQLProcedures.getAvailableRooms(conn, checkInDate.getValue(), checkOutDate.getValue(), 3));
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


    public void enterMyAccount(){
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        myAccountPane.setVisible(true);
        myAccountPane.setDisable(false);
        editEmail.setText(Application.appEmail);
        try (Connection conn = Database.getConnection()){
            editFname.setText(SQLProcedures.getUserInfo(conn, Application.appEmail, 1));
            editLname.setText(SQLProcedures.getUserInfo(conn, Application.appEmail, 2));
            editPhone.setText(SQLProcedures.getUserInfo(conn, Application.appEmail, 3));
        }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error retrieving user info", e);}

    }

    public void deleteUser(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = Database.getConnection()){
                SQLProcedures.deleteEntity(conn, 1, Application.appEmail);
                logOut();
            }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error deleting user", e);}
        }
    }

    String role;
    int status;
    public void updateMyUser(){
        try(Connection conn = Database.getConnection()){
            role = SQLProcedures.getRole(conn, Application.appEmail);
            status = SQLProcedures.updateInfo(conn, editFname.getText(), editLname.getText(), editPhone.getText(), role, Application.appEmail, editEmail.getText());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update user", e);
        }
        enterMyAccount();
    }

    String pass;
    public void changePassword(){

        messLabel.setText("");
        if(editOldPass.getText().isEmpty() || editNewPass.getText().isEmpty() || editConfirmPass.getText().isEmpty()){
            messLabel.setText("Please fill in all fields!");
        }
        try (Connection conn = Database.getConnection()){
            pass = SQLProcedures.getPass(conn, Application.appEmail);

            if (editOldPass.getText().equals(pass)){
                if(editNewPass.getText().equals(editConfirmPass.getText())){
                        SQLProcedures.updatePass(conn, Application.appEmail, editNewPass.getText());
                }else{
                    messLabel.setText("Confirmation of new password failed!");
                }
            }else{
                messLabel.setText("Incorrect old password!");
            }

        }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error proceeding with update of password", e);}
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
        alert.setHeaderText("Make reservation and pay " + total + "€ with card?");
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
        userStackPane.getChildren().forEach(node -> node.setVisible(false));
        userStackPane.getChildren().forEach(node -> node.setDisable(true));
        reservationsPane.setVisible(true);
        reservationsPane.setDisable(false);
        resDetails.setVisible(false);
        resDetails.setDisable(true);
        try(final Connection conn = Database.getConnection()){
            String ress = SQLProcedures.getResByEmail(conn, Application.appEmail);
            resList.getChildren().clear();
            if(ress != null){
                totalRes = ress.split("\n");
                for(String res: totalRes){
                    HBox row = new HBox();
                    row.setSpacing(10);
                    row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                    Label resField = new Label();
                    resField.setPrefWidth(20);
                    resField.setStyle("-fx-font-size: 16px;");
                    resField.setText(res);

                    //details button
                    Button detailsBtn = new Button("Details");
                    detailsBtn.setOnAction(_ -> {
                        resDetails.setVisible(true);
                        resDetails.setDisable(false);
                        try (Connection conn2 = Database.getConnection()){
                            resID = resField.getText();
                            selResEmail.setText(SQLProcedures.getFieldById(conn2, resID, 1, 1));
                            selResCheckIn.setValue(SQLProcedures.getCheckIn(conn2, resID));
                            selResCheckOut.setValue(SQLProcedures.getCheckOut(conn2, resID));
                            selResCheckOut.setEditable(false);
                            selResCheckIn.setEditable(false);
                            selResAmount.setText(String.valueOf(SQLProcedures.getFieldById(conn2, resID, 1 ,4))+"€");
                            resRoomList.getChildren().clear();
                            String ressRooms = SQLProcedures.getResRooms(conn2, resID);
                            totalResRooms = ressRooms.split("\n");
                            for(String rooms: totalResRooms){

                                HBox row1 = new HBox();
                                row1.setSpacing(10);
                                row1.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                                //label for containing roomfield
                                Label roomField = new Label();
                                roomField.setPrefWidth(200);
                                roomField.setStyle("-fx-font-size: 18px;");
                                roomField.setText(rooms);
                                row1.getChildren().addAll(roomField);
                                resRoomList.getChildren().add(row1);
                            }
                        }catch (SQLException ex){
                            LOGGER.log(Level.SEVERE, "Failed to load res info", ex);
                        }
                    });

                    //delete /cancel button
                    Button deleteBtn = new Button("Cancel");
                    deleteBtn.setOnAction(_ -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Cancel Reservation?");
                        alert.setHeaderText("Are you sure you want to cancel your reservation?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            try(Connection conn2 = Database.getConnection()){
                                SQLProcedures.deleteEntity(conn2, 2, String.valueOf(Integer.parseInt(res)));
                                resList.getChildren().remove(row);
                                resDetails.setVisible(false);
                                resDetails.setDisable(true);
                            }catch (SQLException ex){
                                LOGGER.log(Level.SEVERE, "Failed to delete reservation", ex);
                            }
                        }

                    });
                    row.getChildren().addAll(resField, detailsBtn, deleteBtn);
                    resList.getChildren().add(row);
                }
            }else{
                HBox row = new HBox();
                row.setSpacing(10);
                row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                Label noRes = new Label("No reservations yet!");
                noRes.setStyle("-fx-font-size: 12px;");
                noRes.setPrefWidth(200);

                row.getChildren().addAll(noRes);
                resList.getChildren().add(row);
            }
        }catch (SQLException e){
                LOGGER.log(Level.SEVERE, "Failed to load reservations", e);
        }
    }





}
