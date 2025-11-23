package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private static final String URL = "jdbc:postgresql://dblabs.iee.ihu.gr:5432/elevvour";
    private static final String USER = "elevvour";
    private static final String PASSWORD = "smth2025";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


