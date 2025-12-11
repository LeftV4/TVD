package app;

import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLProcedures {

    private static final Logger LOGGER = Logger.getLogger(SQLProcedures.class.getName());

    public static String getUserInfo(Connection conn, String email, int fieldId) {
        String info = null;
        String sql = "SELECT get_user_info_by_email(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, email);
            stmt.setInt(2, fieldId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    info = rs.getString(1);
                }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user info for ID " + fieldId, e);
        }
        return info;
    }
    public static LocalDate getCheckIn(Connection conn, String resid) {
        String dateString = getFieldById(conn, resid, 1, 2);
        if (dateString != null) {
            try {
                return LocalDate.parse(dateString);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing CheckIn date: " + dateString, e);
                return null;
            }
        }
        return null;
    }

    public static LocalDate getCheckOut(Connection conn, String resid) {
        String dateString = getFieldById(conn, resid, 1, 3);
        if (dateString != null) {
            try {
                return LocalDate.parse(dateString);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error parsing CheckOut date: " + dateString, e);
                return null;
            }
        }
        return null;
    }

    public static String getFieldById(Connection conn, String idValue, int idType, int targetField) {
        LOGGER.info("Fetching field " + targetField + " based on ID Type " + idType);
        String info = null;
        String sql = "SELECT get_field_by_id(?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, idValue);
            stmt.setInt(2, idType);
            stmt.setInt(3, targetField);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    info = rs.getString(1);
                }
            }

            if (info != null && (info.equals("-1") || info.equals("-2"))) {
                LOGGER.warning("DB Function Error: " + info);
                return null;
            }

        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching field by ID: " + targetField, e);
            return null;
        }
        return info;
    }

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



    public static String getRole(Connection conn, String email)
    {
        String r_role = null;
        String sql = "SELECT get_role_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {r_role = rs.getString(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching role", e);
        }
        return r_role;
    }

    public static String getPass(Connection conn, String email){
        String pass = null;
        String sql = "SELECT get_pass_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) { pass = rs.getString(1); }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting password", e);
        }
        return pass;
    }

    public static int updatePass(Connection conn, String email, String newPassword) {
        int status = -1;
        LOGGER.info("Updating User Password...");
        String sql = "SELECT update_pass(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, email);
            stmt.setString(2, newPassword);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) { status = rs.getInt(1); }
            }

            if (status == 0) {
                LOGGER.info("Password updated successfully for: " + email);
            } else if (status == 1) {
                LOGGER.warning("User not found: " + email);
            } else if (status == -1) {
                LOGGER.severe("General error updating password.");
            }

        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password", e);
            return 99;
        }
        return status;
    }


    public static String getEntityList(Connection conn, int entityId) {
        LOGGER.info("Fetching Entity List for ID: " + entityId);
        String listContent = null;
        String sql = "SELECT get_entity_list(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, entityId);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    listContent = rs.getString(1);
                }
            }
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "Error fetching entity list for ID " + entityId, e);
        }
        return listContent;
    }



    public static String getResByEmail(Connection conn, String email){
        LOGGER.info("Fetching Reservations by Email...");
        String ress = null;
        String sql = "SELECT get_reservations_by_email(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);

            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    ress = rs.getString(1);
                }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing reservations file", e);
        }
        return ress;

    }


    public static String getResRooms(Connection conn, String resid){
        LOGGER.info("Fetching Reservation Rooms...");
        StringBuilder rooms = new StringBuilder();
        String sql = "SELECT get_reservation_rooms(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(resid));

            try (ResultSet rs = stmt.executeQuery()){

                if(rs.next()){
                    rooms.append(rs.getString(1));
                }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing reservations file", e);
        }
        return rooms.toString();

    }

    public static int deleteEntity(Connection conn, int entityId, String identifier) {
        String sql = "SELECT delete_entity(?, ?)";
        int state = -1;

        if (entityId == 1) {
            LOGGER.info("Deleting User: " + SQLProcedures.getUserInfo(conn, identifier, 1));
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, entityId);
            stmt.setString(2, identifier);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    state = rs.getInt(1);
                }
            }

            if (state == 0) {
                LOGGER.info("Entity ID " + entityId + " deleted successfully: " + identifier);
            } else if (state == 1) {
                LOGGER.warning("Entity ID " + entityId + " not found or general error during deletion: " + identifier);
            } else if (state == -1) {
                LOGGER.warning("Invalid entity ID provided: " + entityId);
            } else if (state == -2) {
                LOGGER.warning("Invalid ID format for reservation deletion: " + identifier);
            } else {
                LOGGER.severe("Unexpected status code received from DB: " + state);
            }

        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during deletion of entity ID " + entityId, e);
            return 2;
        }
        return state;
    }



    public static int updateInfo(Connection conn, String fname, String lname, String phone, String role, String email, String email2) {
        int status = -1;
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

    public static String getLogFileContent(Connection conn) {
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
                        .append(" | Time: ").append(rs.getTimestamp("timestamp_val"))
                        .append("\n");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing log file", e);
            return "Error fetching logs.";
        }
        return logContent.toString();
    }



    public static int getPriceByType(Connection conn, int typeId){
        LOGGER.info("Fetching Room Price for Type ID: " + typeId);
        String sql = "SELECT get_price_by_type(?)";
        int price = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, typeId);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {price = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Room Price for Type ID " + typeId, e);
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

    public static int getAvailableRooms(Connection conn, LocalDate checkIn, LocalDate checkOut, int typeId){
        LOGGER.info("Fetching Available Rooms for Type ID: " + typeId);
        String sql = "SELECT count_available(?, ?, ?)";
        int rooms = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(checkIn));
            stmt.setDate(2, Date.valueOf(checkOut));
            stmt.setInt(3, typeId);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {rooms = rs.getInt(1);}
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching Available Rooms for Type ID " + typeId, e);
        }
        return rooms;
    }


    public static void update_room_prices(Connection conn, Double single_price, Double double_price, Double suite_price) {
        LOGGER.info("Updating Room Prices...");
        String sql = "SELECT update_room_prices(?, ?, ?)";

        if (single_price == null || double_price == null || suite_price == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Prices");
            alert.setContentText("Cannot have empty room values!");
            alert.showAndWait();
            return;
        }

        if (single_price <= 0 || double_price <= 0 || suite_price <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Prices");
            alert.setContentText("All prices must be greater than 0!");
            alert.showAndWait();
            return;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setBigDecimal(1, BigDecimal.valueOf(single_price));
            stmt.setBigDecimal(2, BigDecimal.valueOf(double_price));
            stmt.setBigDecimal(3, BigDecimal.valueOf(suite_price));
            stmt.execute();
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating room prices", e);
        }
    }
}





