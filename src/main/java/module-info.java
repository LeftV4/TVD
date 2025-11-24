module TVD {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens app to javafx.fxml;

    exports app;
}