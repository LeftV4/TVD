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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaffController {

    @FXML private AnchorPane StaffRootPane;
    @FXML private Label helloLabel;
    @FXML private VBox WelcomeBox;
    @FXML private AnchorPane adminButtonPane;
    @FXML private AnchorPane resArea;
    @FXML private VBox resList;
    @FXML private AnchorPane resDetails;
    @FXML private VBox resRoomList;
    @FXML private StackPane adminStackPane;
    @FXML private TextField selResID;
    @FXML private TextField selResGEmail;
    @FXML private DatePicker selResCheckIn;
    @FXML private DatePicker selResCheckOut;
    @FXML private Label availSingle;
    @FXML private Label availDouble;
    @FXML private Label availSuite;
    @FXML private AnchorPane roomAvailArea;
    @FXML private DatePicker roomAvailCheckIn;
    @FXML private DatePicker roomAvailCheckOut;
    @FXML private AnchorPane guestArea;
    @FXML private AnchorPane guestDetails;
    @FXML private VBox guestList;
    @FXML private TextField selGEmail;
    @FXML private TextField selGFName;
    @FXML private TextField selGLName;
    @FXML private TextField selGPhone;





    private static final Logger LOGGER = Logger.getLogger(StaffController.class.getName());

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

        Platform.runLater(()->{
            try (Connection conn = Database.getConnection()){
                helloLabel.setText("Welcome "+ SQLProcedures.getFirstName(conn, Application.appEmail) + "!");
                Label welcLabel = new Label("Staff Privileges");
                WelcomeBox.getChildren().add(welcLabel);
            }catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving First Name", e);
            }});
    }
    @FXML
    public void logOut(){
        Application.appEmail = null;
        try {
            new SceneSwitch(StaffRootPane, "/signIn.fxml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Incapability to leave Scene", e);
        }
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
            resList.getChildren().clear();
            if(ress != null){
                totalRes = ress.split("\n");
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

                                // label containing the roomfield
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

                    row.getChildren().addAll(resField, editBtn);
                    // add row to your UI container
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

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
        }


    }

    private boolean listenersInitialized=false;

    @FXML
    public void showAvailable(){
        adminStackPane.getChildren().forEach(node -> node.setVisible(false));
        adminStackPane.getChildren().forEach(node -> node.setDisable(true));
        roomAvailArea.setVisible(true);
        roomAvailArea.setDisable(false);

        roomAvailCheckIn.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        roomAvailCheckOut.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                LocalDate checkIn = roomAvailCheckIn.getValue();
                setDisable(empty || (checkIn != null && !date.isAfter(checkIn)));
            }


        });

        if (!listenersInitialized) {
            roomAvailCheckIn.valueProperty().addListener((_, _, newV) -> {
                roomAvailCheckOut.setDisable(false);
                roomAvailCheckOut.setValue(null);
                roomAvailCheckOut.setDayCellFactory(_ -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);

                        if (!date.isAfter(newV)){
                            setDisable(true);
                        }
                    }
                });
            });

            roomAvailCheckOut.valueProperty().addListener((_, _, _) -> {
                try (Connection conn = Database.getConnection()){
                    availSingle.setText("Single Room Availability: " + SQLProcedures.getAvailableSingle(conn, roomAvailCheckIn.getValue(), roomAvailCheckOut.getValue()));
                    availDouble.setText("Double Room Availability: " + SQLProcedures.getAvailableDouble(conn, roomAvailCheckIn.getValue(), roomAvailCheckOut.getValue()));
                    availSuite.setText("Luxury Suite Availability: " + SQLProcedures.getAvailableSuite(conn, roomAvailCheckIn.getValue(), roomAvailCheckOut.getValue()));
                }catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error fetching room availability", e);}
            });


            listenersInitialized = true;
        }


    }

    String []totalGuests;
    String userEmail;

    public void showGuests(){
        adminStackPane.getChildren().forEach(node -> node.setVisible(false));
        adminStackPane.getChildren().forEach(node -> node.setDisable(true));
        guestArea.setVisible(true);
        guestArea.setDisable(false);
        guestDetails.setVisible(false);
        guestDetails.setDisable(true);
        try (final Connection conn = Database.getConnection()) {
            String guests = SQLProcedures.getGuests(conn);
            guestList.getChildren().clear();
            if(guests != null){
                totalGuests = guests.split("\n");
                for(String email: totalGuests){
                    // create an HBox for each user row

                    HBox row = new HBox();
                    row.setSpacing(10);
                    row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                    // label containing the user email
                    Label emailField = new Label();
                    emailField.setPrefWidth(200);
                    emailField.setStyle("-fx-font-size: 16px;");
                    emailField.setText(email);

                    // details button
                    Button editBtn = new Button("Details");
                    editBtn.setOnAction(e -> {
                        guestDetails.setVisible(true);
                        guestDetails.setDisable(false);
                        try (Connection conn2 = Database.getConnection();) {
                            userEmail = emailField.getText();
                            selGFName.setText(SQLProcedures.getFirstName(conn2, userEmail));
                            selGLName.setText(SQLProcedures.getLastName(conn2, userEmail));
                            selGEmail.setText(userEmail);
                            selGPhone.setText(SQLProcedures.getPhone(conn2, userEmail));
                        } catch(SQLException ex){
                            LOGGER.log(Level.SEVERE, "Failed to load guests info", ex);
                        }
                    });


                    row.getChildren().addAll(emailField, editBtn);
                    // add row to your UI container
                    guestList.getChildren().add(row);
                }
            }else{
                HBox row = new HBox();
                row.setSpacing(10);
                row.setStyle("-fx-padding: 5; -fx-alignment: CENTER_LEFT;");

                Label noUsers = new Label("No users yet!");
                noUsers.setStyle("-fx-font-size: 12px;");
                noUsers.setPrefWidth(200);

                row.getChildren().addAll(noUsers);
                guestList.getChildren().add(row);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to load users", e);
        }


    }




}
