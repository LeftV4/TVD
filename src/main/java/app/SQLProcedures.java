package app;

import javafx.scene.control.Alert;

import java.math.BigDecimal;
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

    public static int updatePass(Connection conn, String email, String pass) {
        int status = -1;
        LOGGER.info("Updating Password...");
        String sql = "SELECT update_pass(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,email);
            stmt.setString(2,pass);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) { status = rs.getInt(1); }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password", e);
        }
        return status;
    }

    public static String getUsers(Connection conn) {
        LOGGER.info("Fetching Users...");
        String users = null;
        String sql = "SELECT get_users()";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    users = rs.getString(1);
                }
            }
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "Error viewing users file", e);
        }
        return users;
    }

    public static String getGuests(Connection conn) {
        LOGGER.info("Fetching Guests...");
        String guests = null;
        String sql = "SELECT get_guests()";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    guests = rs.getString(1);
                }
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error viewing guests file", e);
        }
        return guests;
    }

    public static String getBills(Connection conn) {
        LOGGER.info("Fetching bill/payments...");
        String bills = null;
        String sql = "SELECT get_bills()";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    bills = rs.getString(1);
                }
            }
        }catch(SQLException e){
            LOGGER.log(Level.SEVERE, "Error viewing users file", e);
        }
        return bills;
    }

    public static String getRes(Connection conn) {
        LOGGER.info("Fetching Reservations...");
        String ress = null;
        String sql = "SELECT get_reservations()";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
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



    public static String getGuestEmail(Connection conn, String resid)
    {
        String guest_email = null;
        String sql = "SELECT get_guest_email_by_resid(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(resid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {guest_email = rs.getString(1);}
                return guest_email;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching guest_email_by_resid", e);
        }
        return guest_email;
    }

    public static Date getCheckIn(Connection conn, String resid){
        Date checkIn = null;
        String sql = "SELECT get_checkIn_by_resid(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(resid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {checkIn = rs.getDate(1);}
                return checkIn;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching CheckIn_by_resid", e);
        }
        return checkIn;
    }

    public static String getRIDbyBILLID(Connection conn, String pid){
        String rid = null;
        String sql = "SELECT get_resid_by_paymentid(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(pid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {rid = rs.getString(1);}
                return rid;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching CheckIn_by_resid", e);
        }
        return rid;
    }

    public static Timestamp getPayDate(Connection conn, String pid){
        Timestamp payDate = null;
        String sql = "SELECT get_paydate_by_paymentid(?)";

        try (PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(pid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {payDate = rs.getTimestamp(1);}
                return payDate;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching paydate_by_paymentid", e);
        }
        return payDate;
    }

    public static String getMethod(Connection conn, String pid){
        String method = null;
        String sql = "SELECT get_method_by_paymentid(?)";

        try (PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(pid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {method = rs.getString(1);}
                return method;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching paydate_by_paymentid", e);
        }
        return method;
    }

    public static Date getCheckOut(Connection conn, String resid){
        Date checkOut = null;
        String sql = "SELECT get_checkOut_by_resid(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,Integer.parseInt(resid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {checkOut = rs.getDate(1);}
                return checkOut;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching CheckOut_by_resid", e);
        }
        return checkOut;
    }

    public static double getAmount(Connection conn, String resid){
        double amount = -1;
        String sql = "SELECT get_amount_by_resid(?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, Integer.parseInt(resid));

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){amount = rs.getDouble(1);}
                return amount;
            }
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching amount_by_resid", e);
        }
        return amount;
    }




    public static int deleteUser(Connection conn, String email) {
        LOGGER.info("Deleting User " + SQLProcedures.getFirstName(conn, email) + "..." );
        String sql = "SELECT deleteUser_by_email(?)";
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

    public static int deleteRes(Connection conn, int resid) {
        LOGGER.info("Deleting Reservation " + resid + "..." );
        String sql = "SELECT deleteRes_by_resid(?)";
        int state = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,resid);

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
                        .append(" | Time: ").append(rs.getTimestamp("timestamp_val"))
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





