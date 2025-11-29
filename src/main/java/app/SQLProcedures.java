package app;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLProcedures {

    private static final Logger LOGGER = Logger.getLogger(SQLProcedures.class.getName());

    public static int registerUser(Connection conn, String email, String role, String password) {
        int status = -1;
        LOGGER.info("Registering User...");
        String sql = "SELECT register_user(?, ?, ?)"; // procedure call

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set input parameters
            stmt.setString(1, email);
            stmt.setString(2, role);
            stmt.setString(3, password);

            // Execute a query and get the result
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getInt(1); // function return value
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user", e);
            status = 2;
        }

        return status;
    }

    public static int registerInfo(Connection conn, String fname, String lname, String phone, String role, String email) {
        int status = -1;
        LOGGER.info("Registering Info...");
        String sql = "SELECT register_info(?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, phone);
            stmt.setString(4, role);
            stmt.setString(5, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user info", e);
            status = 1;
        }
        return status;
    }

    public static String login(Connection conn, String email, String password) {
        LOGGER.info("Logging in...");
        String role = null;
        String sql = "SELECT login(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging in", e);
        }
        return role;
    }

    public static String getFirstName(Connection conn, String email) {
        String fname = null;
        String sql = "SELECT get_fname_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {fname = rs.getString(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting first name", e);
        }
        return fname;
    }

    public static String getLastName(Connection conn, String email) {
        String fname = null;
        String sql = "SELECT get_lname_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {fname = rs.getString(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting last name", e);
        }
        return fname;
    }

    public static String getPhone(Connection conn, String email) {
        String phone = null;
        String sql = "SELECT get_phone_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {phone = rs.getString(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting phone", e);
        }
        return phone;
    }

    public static String getRole(Connection conn, String email) {
        String role = null;
        String sql = "SELECT get_role_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {role = rs.getString(1);}
                return role;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching role", e);
        }
        return role;
    }
    public static String getLogFileContent(Connection conn) {
        LOGGER.info("Fetching Log File content...");
        StringBuilder logContent = new StringBuilder();
        String sql = "SELECT * FROM logfile ORDER BY timestamp DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logContent.append("ID: ").append(rs.getInt("log_id"))
                        .append(" | Table: ").append(rs.getString("table_name"))
                        .append(" | Op: ").append(rs.getString("operation"))
                        .append(" | Old: ").append(rs.getString("old_data"))
                        .append(" | New: ").append(rs.getString("new_data"))
                        .append(" | By: ").append(rs.getString("modified_by"))
                        .append(" | Time: ").append(rs.getTimestamp("timestamp"))
                        .append("\n");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing log file", e);
            return "Error fetching logs.";
        }
        return logContent.toString();
    }
    public static String getUsers(Connection conn) {
        LOGGER.info("Fetching Users...");
        StringBuilder users = new StringBuilder();
        String sql = "SELECT * FROM users";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.append(rs.getString("email"))
                        .append("\n");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing users file", e);
            return "Error fetching logs.";
        }
        return users.toString();
    }

    public static int deleteUser(Connection conn, String email) {
        LOGGER.info("Deleting User " + SQLProcedures.getFirstName(conn, email) + "..." );
        String sql = "SELECT * from deleteUser_by_email(?)";
        int state = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {state = rs.getInt(1);}

            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
        }
        return state;
    }

    public static int updateInfo(Connection conn, String fname, String lname, String phone, String role, String email, String email2) {
        int status = -1;
        LOGGER.info("Updating Info...");
        String sql = "SELECT update_info(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, phone);
            stmt.setString(4, role);
            stmt.setString(5, email);
            stmt.setString(6, email2);


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user info", e);
            status = 1;
        }
        return status;
    }



}



