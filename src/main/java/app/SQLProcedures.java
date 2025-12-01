package app;

import java.sql.*;
import java.time.LocalDate;
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

    public static String getRole(Connection conn, String email)
    {
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
        String sql = "SELECT * FROM get_logs()";

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

    public static int getSinglePrice(Connection conn){
        LOGGER.info("Fetching Single Room Price...");
        String sql = "SELECT get_single_price()";
        int price = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {price = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Single Room Price", e);
        }
        return price;
    }

    public static int getDoublePrice(Connection conn){
        LOGGER.info("Fetching Double Room Price...");
        String sql = "SELECT get_double_price()";
        int price = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {price = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Double Room Price", e);
        }
        return price;
    }

    public static int getSuitePrice(Connection conn){
        LOGGER.info("Fetching Suite Room Price...");
        String sql = "SELECT get_suite_price()";
        int price = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {price = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Suite Room Price", e);
        }
        return price;
    }


    public static int makeReservation(Connection conn, String email, Date checkIn, Date checkOut, int singles, int doubles, int suites) {
        LOGGER.info("Making Reservation...");
        String sql = "SELECT make_reservation(?, ?, ?, ?, ?, ?)";
        int id = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, email);
            stmt.setDate(2, checkIn);
            stmt.setDate(3, checkOut);
            stmt.setInt(4, singles);
            stmt.setInt(5, doubles);
            stmt.setInt(6, suites);

            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error making reservation", e);
        }
        return id;
    }

    public static void registerPayment (Connection conn, int res_id, double amount, String method) {
        LOGGER.info("Registering Payment...");
        String sql = "SELECT register_payment(?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, res_id);
                stmt.setDouble(2, amount);
                stmt.setString(3, method);
                stmt.execute();
            } catch (SQLException e) {LOGGER.log(Level.SEVERE, "Error registering payment", e);}
    }


    public static int getAvailableSingle(Connection conn, LocalDate checkIn, LocalDate checkOut){
        LOGGER.info("Fetching Available Single Rooms...");
        String sql = "SELECT count_available_single(?,?)";
        int rooms = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(checkIn));
            stmt.setDate(2, Date.valueOf(checkOut));
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {rooms = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Available Single Rooms", e);
        }
        return rooms;
    }

    public static  int getAvailableDouble(Connection conn, LocalDate checkIn, LocalDate checkOut){
        LOGGER.info("Fetching Available Double Rooms...");
        String sql = "SELECT count_available_double(?,?)";
        int rooms = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(checkIn));
            stmt.setDate(2, Date.valueOf(checkOut));
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {rooms =rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Available Double Rooms", e);
        }
        return rooms;
    }

    public static int getAvailableSuite(Connection conn, LocalDate checkIn, LocalDate checkOut){
        LOGGER.info("Fetching Available Suite Rooms...");
        String sql = "SELECT count_available_suite(?,?)";
        int rooms = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(checkIn));
            stmt.setDate(2, Date.valueOf(checkOut));
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {rooms =rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Available Suite Rooms", e);
        }
        return rooms;
    }
}





