import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class Controller {
    @FXML
    Label connLabel;
    @FXML
    Button connButton;

    @FXML
    public void connectDB(){
        try (Connection conn = Database.getConnection();) {
            connLabel.setText("Connected!");
            connLabel.setStyle("-fx-text-fill: green");
        }catch (Exception e){
            connLabel.setText("Connection Failed!");
            connLabel.setStyle("-fx-text-fill: red");
            e.printStackTrace();
    }




    }
}
