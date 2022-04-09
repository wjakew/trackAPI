/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.maintanance.MailConnector;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.mail.MailSender;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

/**
 * Object for maintaining 2factor authentication on database
 */
public class Database_2FactorAuth {

    /**
     * CREATE TABLE TWO_FACTOR_ENABLED
     * (
     *     user_id INT PRIMARY KEY AUTO_INCREMENT,
     *     2fa_email VARCHAR(200),
     *     2fa_confirmed INT,
     *
     *     CONSTRAINT fk_twofactorenabled1 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
     * );
     */

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_2FactorAuth(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for setting confirmation links to users
     * @param user_id
     * @return String
     */
    String generate_start_link(int user_id) throws SQLException {
        // track-2fa-confirm/{user_id}/{confirmation_code}
        String query = "SELECT 2fa_confirmed FROM TWO_FACTOR_ENABLED where user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return "http://"+TrackApiApplication.service_ip.split("/")[1]+"/track-2fa-confirm"+"/"+user_id+"/"+rs.getInt("2fa_confirmed");
            }
            return "error";
        }catch(Exception e){
            database.log("Failed to generate start link ("+e.toString()+")","2FALINK-FAILED");
            return "database_error";
        }
    }

    /**
     * Function for authorizing 2fa
     * @param user_id
     * @param fa_code
     * @return Integer
     */
    public int authorize(int user_id,int fa_code) throws SQLException {
        database.log("Trying to authorize "+user_id+" with "+fa_code,"2FAAUTH");
        String query = "SELECT user_id FROM TWO_FACTOR_CODES WHERE user_id = ? and fa_code = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setInt(2,fa_code);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                user_id =  rs.getInt("user_id");
                database.log("2fa code and user match!","2FAAUTH");
                query = "DELETE FROM TWO_FACTOR_CODES where user_id = ?  and fa_code = ?;";
                ppst = database.con.prepareStatement(query);
                ppst.setInt(1,user_id);
                ppst.setInt(2,fa_code);
                ppst.execute();
                database.log("2fa code removed","2FAAUTH");
                database.log("User "+user_id+" authorized on database","2FAAUTH");
                return user_id;
            }
            return -11;
        }catch(SQLException e){
            database.log("Failed to authorize user ("+user_id+" with "+fa_code+") ("+e.toString(),"2FAAUTH-FAILED");
            return -6;
        }
    }

    /**
     * Function for sending confirmation email
     * @return String
     */
    public void send_confirmation_mail(int user_id,String email) throws SQLException {
        MailConnector mc = new MailConnector();
        String content = "Hi!\nClick link to confirm your 2 factor authentication email:\n"+generate_start_link(user_id)
                +"\n\nTrack Team";
        String subject = "2FA Confirmation - Track";
        mc.send(email,subject,content);
    }

    /**
     * Function for creating 2fa codes
     * @return Integer
     */
    int get_code(){
        Random rand = new Random();
        return rand.nextInt(9999);
    }

    /**
     * Function for checking if user have enabled 2fa
     * @param user_id
     * @return Integer
     * return codes:
     *  1 - user using 2fa and confirmed
     *  0 - user using 2fa but no mail confirmation
     * -1 - 2fa not enabled
     * -2 - database error
     */
    public int check_2fa_enabled(int user_id) throws SQLException {
        String query = "SELECT 2fa_confirmed FROM TWO_FACTOR_ENABLED WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                if  (rs.getInt("2fa_confirmed") == 0){
                    return 1;
                }
                return 0;
            }
            return -1;
        } catch (SQLException e) {
            database.log("Failed to check 2fa enabled ("+e.toString()+")","2FACHECK-FAILED");
            return -2;
        }
    }

    /**
     * Function for getting user email
     * @param user_id
     * @return String
     */
    String get_email(int user_id) throws SQLException {
        String query = "SELECT 2fa_email FROM TWO_FACTOR_ENABLED WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if( rs.next() ){
                return rs.getString("2fa_email");
            }
            return "none";
        }catch(SQLException e){
            database.log("Failed to get email ("+e.toString()+")","2FAEMAIL-FAILED");
            return "error";
        }
    }
    /**
     * Function for showing list of users with 2fa enabled
     */
    public void show_2fa_enabled_users() throws SQLException {
        String query = "SELECT user_id,2fa_confirmed FROM TWO_FACTOR_ENABLED;";
        try{
            System.out.println("Showing 2fa enabled users:");
            PreparedStatement ppst = database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                String data = rs.getInt("user_id")+" - "+database.get_userlogin_byid(rs.getInt("user_id"));
                if ( rs.getInt("2fa_confirmed") != 0 ){
                    data = data + " ,code:"+rs.getInt("2fa_confirmed") ;
                }
                else{
                    data = data + "CONFIRMED";
                }
                System.out.println(data);
            }
        }catch(Exception e){
            database.log("Failed to show enabled users ("+e.toString()+")","2FASHOW-FAILED");
        }
    }

    /**
     * Function for enabling authorization on account
     * @param user_id
     * @param email
     * @return Integer
     */
    public int enable_authorization(int user_id, String email) throws SQLException {
        String query = "INSERT INTO TWO_FACTOR_ENABLED (user_id,2fa_email,2fa_confirmed) VALUES (?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,email);
            ppst.setInt(3,get_code());
            ppst.execute();
            database.log("2FA enabled for user_id:"+user_id,"2FA-ENABLE");
            send_confirmation_mail(user_id,email);
            return 1;
        }catch(SQLException e){
            database.log("Failed to enable authorization ("+e.toString()+")","2FA-ENABLE-FAILED");
            return -1;
        }catch(Exception e){
            database.log("Error: "+e.toString(),"ENABLE-AUTH");
            return -1;
        }
    }

    /**
     * Function for clearing user codes from database
     * @param user_id
     * @return Integer
     */
    public int clear_user_codes(int user_id) throws SQLException {
        String query = "DELETE FROM TWO_FACTOR_CODES WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.execute();
            return 1;
        } catch (SQLException e) {
            database.log("Failed to clear user 2fa codes ("+e.toString()+")","2FACLEAR-USER-FAILED");
            return -1;
        }
    }

    /**
     * Function for clearing all user codes
     * @return Integer
     */
    public int clear_codes() throws SQLException {
        String query = "DELETE FROM TWO_FACTOR_CODES;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.execute();
            database.log("Removed all 2fa codes","2FACLEAR");
            return 1;
        }catch(Exception e){
            database.log("Failed to remove all codes from database ("+e.toString()+")","2FACLEAR-FAILED");
            return -1;
        }
    }

    /**
     * Function for getting 2fa code
     * @param user_id
     * @return Integer
     * -1 - no 2fa code
     * -2 - database error
     *  > - 2fa code
     */
    int get_2fa_code(int user_id) throws SQLException {
        String query = "SELECT 2fa_confirmed FROM TWO_FACTOR_ENABLED WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                return rs.getInt("2fa_confirmed");
            }
            return -1;
        }catch(Exception e){
            database.log("Failed to get 2fa code ("+e.toString()+")","GET2FACODE-FAILED");
            return -2;
        }
    }

    /**
     * Function for confirming authorization
     * @param user_id
     * @param confirmation_code
     * @return Integer
     *  1 - authorization confirmed
     * -1 - database error
     *  0 - one confirmation code failed to check
     */
    public int confirm_authorization(int user_id,int confirmation_code) throws SQLException {
        if ( confirmation_code == get_2fa_code(user_id)){
            String query = "UPDATE TWO_FACTOR_ENABLED SET 2fa_confirmed = 0 WHERE user_id = ?";
            try{
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,user_id);
                ppst.execute();
                database.log("Authorization confirmed!","2FAAUTH-CONF");
                return 1;
            }catch(Exception e){
                database.log("Failed to confirm authorization ("+e.toString()+")","2FAAUTH-CONF-FAILED");
                return -1;
            }
        }
        return 0;
    }

    /**
     * Function for disable authorization
     * @param user_id
     * @return Integer
     * @throws SQLException
     */
    public int disable_authorization(int user_id) throws SQLException {
        String query = "DELETE FROM TWO_FACTOR_ENABLED WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.execute();
            database.log("2FA disabled for user_id:"+user_id,"2FA-DISABLED");
            return 1;
        }catch(Exception e){
            database.log("Failed to disable autorization ("+e.toString()+")","2FA-DISABLE-FAILED");
            return -1;
        }
    }

    /**
     * Function for rolling new 2fa
     * @param user_id
     * @return Integer
     */
    public int roll_2fa(int user_id) throws SQLException {
        /**
         * CREATE TABLE TWO_FACTOR_CODES
         * (
         *     user_id INT PRIMARY KEY AUTO_INCREMENT,
         *     2fa_code INT,
         *
         *     CONSTRAINT fk_twofactorcodes FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        if ( check_2fa_enabled(user_id) == 1){
            String query = "INSERT INTO TWO_FACTOR_CODES (user_id,2fa_code) VALUES (?,?);";

            try{
                int fa_code = get_code();
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,user_id);
                ppst.setInt(2,fa_code);
                ppst.execute();
                database.log("Rolled 2fa for user_id: "+user_id,"2FAROLL");
                MailConnector mc = new MailConnector();

                String email = get_email(user_id);

                if ( !email.equals("none") && !email.equals("error")){
                    mc.send(email,"2FA CODE - TRACK","Your 2FA code: "+fa_code+"\n Track Team");
                    database.log("Email with code sent!","2FACODE-EMAIL");
                    return 1;
                }
                else{
                    database.log("Problem with 2fa code. Check log.","2FACODE-EMAIL-FAILED");
                    return -1;
                }
            } catch (SQLException e) {
                database.log("Failed to roll 2fa code ("+e.toString()+")","2FAROLL-FAILED");
                return -1;
            }
        }
        else if (check_2fa_enabled(user_id) == 0 ){
            database.log("2FA for user_id:"+user_id+" not confirmed","2FAROLL");
            return 0;
        }
        else if (check_2fa_enabled(user_id) == -1){
            database.log("2FA not enabled for user.","2FAROLL-NOTENABLED");
            return -2;
        }
        else{
            database.log("2FA roll error. Check log.","2FAROLL-FAILED");
            return -3;
        }
    }

    /**
     * Function for rolling new 2fa
     * @param user_id
     * @return Integer
     */
    public int manual_roll_2fa(int user_id) throws SQLException {
        /**
         * CREATE TABLE TWO_FACTOR_CODES
         * (
         *     user_id INT PRIMARY KEY AUTO_INCREMENT,
         *     2fa_code INT,
         *
         *     CONSTRAINT fk_twofactorcodes FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        if ( check_2fa_enabled(user_id) == 1){
            String query = "INSERT INTO TWO_FACTOR_CODES (user_id,2fa_code) VALUES (?,?);";

            try{
                int fa_code = get_code();
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,user_id);
                ppst.setInt(2,fa_code);
                ppst.execute();
                database.log("Rolled 2fa for user_id: "+user_id,"2FAROLL");
                database.log("2FA code: user_id:"+user_id+" code: "+fa_code,"2FA");
                return 1;
            } catch (SQLException e) {
                database.log("Failed to roll 2fa code ("+e.toString()+")","2FAROLL-FAILED");
                return -1;
            }
        }
        else if (check_2fa_enabled(user_id) == 0 ){
            database.log("2FA for user_id:"+user_id+" not confirmed","2FAROLL");
            return 0;
        }
        else if (check_2fa_enabled(user_id) == -1){
            database.log("2FA not enabled for user.","2FAROLL-NOTENABLED");
            return -2;
        }
        else{
            database.log("2FA roll error. Check log.","2FAROLL-FAILED");
            return -3;
        }
    }
}
