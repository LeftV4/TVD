module TVD {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.almasb.fxgl.scene;
    requires javafx.base;
    requires java.logging;
    requires java.desktop;

    opens app to javafx.fxml;
    exports app;
}