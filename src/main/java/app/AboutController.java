package app;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;


public class AboutController {

    @FXML private AnchorPane aboutRootPane;


    private static final Logger LOGGER = Logger.getLogger(AboutController.class.getName());



    @FXML
    public void logOut(){
        try {
            new SceneSwitch(aboutRootPane, "/signIn.fxml");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Incapability to leave Scene", e);
        }
    }


}