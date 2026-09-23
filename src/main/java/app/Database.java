package app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private static final String URL = requireEnv("DB_URL");
    private static final String USER = requireEnv("DB_USER");
    private static final String PASSWORD = requireEnv("DB_PASSWORD");
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean checking = false;

    @FXML
    public static void connectDB(Label connLabel, ExecutorService executor) {
        if (Database.checking) return;
        Database.checking = true;

        executor.submit(() -> {
            boolean connected;

            try (Connection conn = Database.getConnection()) {
                connected = conn.isValid(2);
            } catch (SQLException | IllegalStateException e) {
                LOGGER.log(Level.SEVERE, "Unable to establish database connection", e);
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

                Database.checking = false;
            });
        });
    }
}
