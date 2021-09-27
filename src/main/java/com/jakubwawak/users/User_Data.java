/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.users;

import com.jakubwawak.administrator.Password_Validator;
import com.jakubwawak.administrator.RandomString;
import com.jakubwawak.maintanance.MailConnector;
import com.jakubwawak.trackAPI.TrackApiApplication;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import javax.sound.midi.Track;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * OBJECT RETURN CODES
 *
 * user_id field return codes:
 *
 * -99 - session validation failed
 * -11 - app token validation failed
 * -88 - database error while validation session
 *
 * check_email_avability()
 * -5 user not found
 * -6 database error
 *
 * login()
 * -5 user not found
 * -6 database error
 *
 * check_login_avaiability()
 * true - login clear to use
 * false - login is taken
 *
 * register()
 * any > 0 - user_id
 * -8 - error sending email
 * -6 - database error
 *
 * reset_password()
 * -7 - email address not found
 * -8 - error sending email
 * -6 - database error
 * -5 - user with email address not found
 */

/**
 * Object for representing user data
 */
public class User_Data {

    /**
     * CREATE TABLE USER_DATA
     * (
     *   user_id INT PRIMARY KEY AUTO_INCREMENT,
     *   user_name VARCHAR(150),
     *   user_surname VARCHAR(200),
     *   user_email VARCHAR(200),
     *   user_login VARCHAR(25),
     *   user_password VARCHAR(50),
     *   user_category VARCHAR(100) -- CODES: ADMIN,DEVELOPER,CLIENT
     * );
     */

    public int user_id;
    public String user_name;
    public String user_surname;
    public String user_email;
    public String user_login;
    public String user_category;
    public String user_session;
    public String user_password;

    /**
     * Main default constructor
     */
    public User_Data(){
        user_id = -1;
        user_name = "";
        user_surname = "";
        user_email = "";
        user_login = "";
        user_category = "";
        user_session = "blank";
        user_password = "";
    }

    /**
     * Function for adding database data to object
     * @param to_add
     */
    void database_loader(ResultSet to_add) throws SQLException {
        user_id = to_add.getInt("user_id");
        user_name = to_add.getString("user_name");
        user_surname = to_add.getString("user_surname");
        user_email = to_add.getString("user_email");
        user_login = to_add.getString("user_login");
        user_category = to_add.getString("user_category");
    }

    /**
     *Function for loading data by given user_id
     * @param user_id
     */
    public void load_data(int user_id) throws SQLException {
        String query = "SELECT * FROM USER_DATA WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                database_loader(rs);
            }
            else{
                user_id = -2;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load data by user_id ("+e.toString()+")","ERROR_USR1");
        }
    }

    /**
     * Function for loading data by given user_login or mail
     * @param user_data
     */
    public void load_data(String user_data) throws SQLException {
        String query;
        if ( user_login.contains("@"))
            query = "SELECT * FROM USER_DATA WHERE user_email = ?;";
        else
            query = "SELECT * FROM USER_DATA WHERE user_login = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_data);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                database_loader(rs);
            }
            else{
                user_id = -2;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load data by user_data ("+e.toString()+")","ERROR_USR2");
        }
    }

    /**
     * Function for checking email avability
     * @throws SQLException
     */
    public void check_email_avability() throws SQLException {
        String query = "SELECT user_email FROM USER_DATA where user_email=?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setString(1,user_email);

            ResultSet rs = ppst.executeQuery();

            if (!rs.next()){
                user_id = -5;
            }
        }catch (SQLException e){
            TrackApiApplication.database.log("Faield to check email avability ("+e.toString()+")","EMAIL-CHECK-ERROR");
            user_id = -6;
        }
    }

    /**
     * Function for login to database
     * @param user_login
     * @param password
     */
    public void login(String user_login,String password) throws SQLException {
        String query = "SELECT * FROM USER_DATA where user_login = ? and user_password = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_login);
            ppst.setString(2,password);
            TrackApiApplication.database.log("Trying to authorize "+user_login+" with "+password+"("+password+")","USER-LOGIN");

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                database_loader(rs);
                TrackApiApplication.database.remove_session(user_login);
                user_session = TrackApiApplication.database.create_session(user_id);
                TrackApiApplication.database.log("User "+user_login+" logged in!","USER-SUCCESS");
            }
            else{
                TrackApiApplication.database.log("User "+user_login+" failed to login","USER-FAILED");
                user_id = -5;
            }

        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to login user ("+e.toString()+")","ERROR-USR3");
            user_id = -6;
        }
    }

    /**
     * Function for checking login avaiablity
     */
    boolean check_login_avaiability() throws SQLException {
        String query = "SELECT user_login from USER_DATA where user_login = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setString(1,this.user_login);

            ResultSet rs = ppst.executeQuery();

            return !rs.next();
        } catch (SQLException throwables) {
            TrackApiApplication.database.log("Failed to check login avability","USERLOGINCREATOR-ERROR");
            return false;
        }
    }

    /**
     * Function for getting user_id data by given login
     * @param user_login
     * @return
     */
    int get_userid_by_login(String user_login) throws SQLException {
        String query = "SELECT user_id FROM USER_DATA WHERE user_login = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_login);
            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return rs.getInt("user_id");
            }
            return -7;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get user_id ("+e.toString()+")","USERID-ERROR");
            return -6;
        }
    }

    /**
     * Function for registering user
     */
    public void register() throws SQLException, NoSuchAlgorithmException {
        TrackApiApplication.database.log("Trying to register new user..","REGISTER");
        RandomString generator = new RandomString(12);
        user_password = generator.buf;
        Password_Validator pv = new Password_Validator(user_password);
        user_password = pv.hash();
        user_login = user_name.replaceAll(" ","")+user_surname.replaceAll(" ","");
        user_login = user_login.toLowerCase();
        user_category = "CLIENT";
        if ( this.check_login_avaiability() ){
            user_login = user_login+"1";
        }
        String query = "INSERT INTO USER_DATA\n" +
                "(user_name,user_surname,user_email,user_login,user_password,user_category)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?);";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setString(1,user_name);
            ppst.setString(2,user_surname);
            ppst.setString(3,user_email);
            ppst.setString(4,user_login);
            ppst.setString(5,pv.hash());
            ppst.setString(6,user_category);

            ppst.execute();
            TrackApiApplication.database.log("User "+user_name+" "+user_surname+" registered with "+user_login+" - "+generator.buf,"REGISTER-SUCCESS");
            this.user_password = generator.buf;
            this.user_id = get_userid_by_login(this.user_login);
            try{
                MailConnector mc = new MailConnector();
                mc.send(user_email,"Welcome to TRACK!","Your credentials for using the service\nlogin: "+user_login+"\npassword: "+user_password+"\n\n TRACK TEAM");
                TrackApiApplication.database.log("Login data send to "+user_email,"MAIL-REGISTER-SUCCESFULL");
            }catch(Exception e){
                TrackApiApplication.database.log("Failed to send login data to "+user_email+" ("+e.toString()+")","MAIL-REGISTER-FAILED");
                this.user_id = -8;
            }
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to register user ("+e.toString()+")","REGISTER-ERR");
            this.user_id = -6;
        }
    }

    /**
     * Function for reseting password
     * @throws SQLException
     */
    public void reset_password() throws SQLException {
        TrackApiApplication.database.log("Trying to reset password for user "+user_email,"PASSWORD-RESET");
        try{
            check_email_avability();
            if ( user_id != -7){
                String query = "SELECT user_id from USER_DATA where user_email = ?;";

                PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
                ppst.setString(1,this.user_email);

                RandomString generator = new RandomString(14);

                ResultSet rs = ppst.executeQuery();

                if (rs.next()){
                    int user_id = rs.getInt("user_id");

                    Password_Validator pv = new Password_Validator(generator.buf);

                    String query2 = "UPDATE USER_DATA SET user_password = ? where user_id = ?;";

                    ppst = TrackApiApplication.database.con.prepareStatement(query2);

                    ppst.setString(1,pv.hash());
                    ppst.setInt(2,user_id);

                    ppst.execute();
                    user_password = generator.buf;

                    try{
                        MailConnector mc = new MailConnector();
                        mc.send(user_email,"Password reset for the TRACK Service","Your new password is: "+user_password);
                        TrackApiApplication.database.log("Mail with new password send to "+user_email,"MAIL-SEND-SUCCESS");
                    }catch(Exception e){
                        TrackApiApplication.database.log("Failed to send email to "+user_email+" ("+e.toString()+")","MAIL-SEND-ERROR");
                        this.user_id = -8;
                    }
                    TrackApiApplication.database.log("Password for user successfuly reset","PASSWORD-RESET-CRT");
                }
                else{
                    user_id = -5;
                }
            }
        } catch (Exception e) {
            TrackApiApplication.database.log("Failed to reset password for user_id ("+e.toString(),"PASSWORD-RESET-FAILED");
            user_id = -6;
        }
    }

    /**
     * Function for checking if given password is correct
     */
    public void check_password() throws SQLException {
        String query = "SELECT user_id FROM SESSION_TOKEN where session_token = ?;";

        try{
            TrackApiApplication.database.log("Trying to check password for session "+this.user_session,"PASSWORD-CHECK");
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setString(1,this.user_session);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                TrackApiApplication.database.log("Session found!","PASSWORD-CHECK");
                int user_id = rs.getInt("user_id");
                String query2 = "SELECT user_id FROM USER_DATA WHERE user_id = ? and user_password = ?;";

                PreparedStatement ppst2 = TrackApiApplication.database.con.prepareStatement(query2);

                ppst2.setInt(1,user_id);
                ppst2.setString(2,this.user_password);

                rs = ppst.executeQuery();

                if ( rs.next() ){
                    this.user_id = user_id;
                }
                else{
                    this.user_id = -5;
                }

            }
            else{
                this.user_id = -5;
            }

        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to check password ("+e.toString()+")","PASSWORD-CHECK-ERROR");
            this.user_id = -6;
        }
    }
}
