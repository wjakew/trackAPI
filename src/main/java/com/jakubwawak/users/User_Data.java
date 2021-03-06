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

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
/**
 * OBJECT RETURN CODES
 *
 * user_id field return codes:
 *
 *   1  - ok
 * -99  - session validation failed
 * -11  - app token validation failed
 * -88  - database error while validation session
 * -101 - wrong 2fa token
 *
 * check_email_avability()
 * -5 user not found
 * -6 database error
 *
 * login()
 * -5 user not found
 * -6 database error
 * -9 user blocked
 * -69 2fa enabled
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

    public int flag;
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
        flag = 0;
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
     * Function for administration to manual add user
     * @param user_login
     * @param user_password
     * @throws SQLException
     */
    public void manual_register(String user_login, String user_password) throws SQLException {
        this.user_login = user_login;
        this.user_password = user_password;

        String query = "INSERT INTO USER_DATA\n" +
                "(user_name,user_surname,user_email,user_login,user_password,user_category)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?);";
        try{
            Password_Validator pv = new Password_Validator(this.user_password);
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setString(1,user_name);
            ppst.setString(2,user_surname);
            ppst.setString(3,user_email);
            ppst.setString(4,user_login);
            ppst.setString(5,pv.hash());
            ppst.setString(6,user_category);

            ppst.execute();
            TrackApiApplication.database.log("User "+user_name+" "+user_surname+" registered with "+user_login+" - "+user_login,"REGISTER-SUCCESS");
            get_userid_by_login(user_login);
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to register user ( "+e.toString()+")","REGISTER-FAILED");
        } catch (NoSuchAlgorithmException e) {
            TrackApiApplication.database.log("Failed to register user ( "+e.toString()+")","REGISTER-FAILED");
        }
    }

    /**
     * Function for registering user only by email
     * @param email
     */
    public void manual_register_email(String email) throws SQLException, NoSuchAlgorithmException {
        TrackApiApplication.database.log("Trying to register new user..","REGISTER");
            RandomString generator = new RandomString(12);
            user_password = generator.buf;
            Password_Validator pv = new Password_Validator(user_password);
            user_password = pv.hash();
            user_login = email.split("@")[0].toLowerCase();
            user_name = user_login;
            user_surname = user_login;
            user_category = "CLIENT";
            user_email = email;

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
                this.get_userid_by_login(this.user_login);
                this.create_user_configuration();
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
     * Function for getting user login data
     */
    public void get_login() throws SQLException {
        String query = "SELECT user_login FROM USER_DATA WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                user_login = rs.getString("user_login");
            }
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to get user login ("+e.toString()+")","USER-LOGIN-FAILED");
        }
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
     * Function for reseting password
     * @param user_id
     * @throws SQLException
     */
    public void reset_password(int user_id) throws SQLException {
        String query = "SELECT user_email FROM USER_DATA WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                String user_email = rs.getString("user_email");
                if ( user_email.equals("")){
                    TrackApiApplication.database.log("user_email field is empty","USER-RESET");
                }
                else{
                    RandomString generator = new RandomString(14);
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
            }
            else{
                TrackApiApplication.database.log("no user with given user_id","USER-RESET");
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            TrackApiApplication.database.log("Failed to reset user password ("+e.toString()+")","USER-RESET-FAILED");
        }
    }

    /**
     * Function for updating user_email
     * @param user_id
     * @param user_email
     * @throws SQLException
     */
    public void update_email(int user_id,String user_email) throws SQLException {
        String query = "UPDATE USER_DATA SET user_email = ? WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_email);
            ppst.setInt(2,user_id);
            ppst.execute();
            TrackApiApplication.database.log("Updated user_email. Set to "+user_email,"UPDATE-EMAIL-SUCCESS");
            flag = 1;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to update user_email ("+e.toString()+")","UPDATE-EMAIL-FAILED");
            flag = -1;
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
     * Function for setting block on the account
     * @param user_id
     * @return boolean
     */
    public boolean set_block(int user_id) throws SQLException {
        String query = "INSERT INTO USER_GRAVEYARD (user_id,graveyard_date) VALUES (?,?);";
        LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setObject(2,ldt);
            ppst.execute();
            TrackApiApplication.database.log("Setting block for user_id (user_id:"+user_id+")","USER-BLOCK-SUCCESS");
            return true;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to set block on user (user_id:"+user_id+") ("+e.toString()+")","USER-BLOCK-FAILED");
            return false;
        }
    }

    /**
     *Function for removing block from the account
     * @param user_id
     * @return boolean
     */
    public boolean remove_block(int user_id) throws SQLException {
        String query = "DELETE FROM USER_GRAVEYARD WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.execute();
            TrackApiApplication.database.log("Removed block from account.","USER-BLOCKED-REMOVED");
            return true;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to remove block ("+e.toString()+")","USER-BLOCKED-FAILED");
            return false;
        }
    }

    /**
     * Function for checking user account graveyard
     * @param user_login
     * @return boolean
     */
    public boolean check_block(String user_login) throws SQLException {
        get_userid_by_login(user_login);
        TrackApiApplication.database.log("Checking block on ("+user_login+") (id:"+user_id+")","USR-BLOCK-CHECK");
        if ( user_id > 0 ){
            String query = "SELECT * FROM USER_GRAVEYARD WHERE user_id = ?;";
            try{
                PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
                ppst.setInt(1,user_id);
                ResultSet rs = ppst.executeQuery();
                if (rs.next()) {
                    TrackApiApplication.database.log("Block on account found!", "USR-BLOCK-CHECK");
                    return true;
                }
                TrackApiApplication.database.log("Account not blocked.","USR-BLOCK-CHECK");
                return false;
            }catch(Exception e){
                TrackApiApplication.database.log("Failed to check block... ("+e.toString()+")","USR-BLOCK-FAILED");
                return false;
            }
        }
        else{
            return false;
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
                if ( !check_block(rs.getString("user_login"))){
                    database_loader(rs);
                    TrackApiApplication.database.remove_session(user_login);
                    user_session = TrackApiApplication.database.create_session(user_id);
                    TrackApiApplication.database.log("User "+user_login+" logged in!","USER-SUCCESS");
                }
                else{
                    TrackApiApplication.database.log("User blocked on app.","USER-BLOCKED");
                    user_id = -9;
                }

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
     * Function for login user after
     * @throws SQLException
     */
    public void login() throws SQLException {
        String query = "SELECT * FROM USER_DATA where user_id = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            TrackApiApplication.database.log("Adding data to authorized user (user_id:"+user_id+")","USER-LOGIN");
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                if ( !check_block(rs.getString("user_login"))){
                    database_loader(rs);
                    TrackApiApplication.database.remove_session(user_login);
                    user_session = TrackApiApplication.database.create_session(user_id);
                    flag = 1;
                    TrackApiApplication.database.log("User "+user_login+" logged in!","USER-SUCCESS");
                }
                else{
                    TrackApiApplication.database.log("User blocked on app.","USER-BLOCKED");
                    user_id = -9;
                }
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
    public void get_userid_by_login(String user_login) throws SQLException {
        String query = "SELECT user_id FROM USER_DATA WHERE user_login = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_login);
            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                this.user_id = rs.getInt("user_id");
            }else{
                user_id = -7;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get user_id ("+e.toString()+")","USERID-ERROR");
            user_id = -6;
        }
    }

    /**
     * Function for checking if email is connected to account
     * @return Boolean
     */
    boolean check_email_connected() throws SQLException {
        String query = "SELECT user_id FROM USER_DATA WHERE user_email = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_email);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return true;
            }
            return false;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to check if email is connected ( "+e.toString()+")","EMAIL-CHECK-FAILED");
            return true;
        }
    }

    /**
     * Function for registering user
     */
    public void register() throws SQLException, NoSuchAlgorithmException {
        TrackApiApplication.database.log("Trying to register new user..","REGISTER");
        if ( check_email_connected()){
            TrackApiApplication.database.log("Email is connected to another user. Aborted","REGISTER");
            user_id = -7;
        }
        else{
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
                this.get_userid_by_login(this.user_login);
                this.create_user_configuration();
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
    }

    /**
     * Function for creating first configuration
     * @return Integer
     */
    public int create_user_configuration() throws SQLException {
        String query = "INSERT INTO USER_CONFIGURATION\n" +
                        "(user_id,config1,config2,config3)\n" +
                        "VALUES\n" +
                        "(?,?,?,?);";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,"DARK");
            ppst.setString(3,"");
            ppst.setString(4,"");
            ppst.execute();
            TrackApiApplication.database.log("Created user configuration ","USER-CONFIGURATION");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to create user configuration ("+e.toString()+")","USER-CONFIGURATION-FAILED");
            return -1;
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
     * Function for loading email data for user
     */
    void load_email() throws SQLException {
        String query = "SELECT user_email FROM USER_DATA WHERE user_id=?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                user_email = rs.getString("user_email");
            }
            TrackApiApplication.database.log("Loaded user email from database","LOAD-EMAIL-SUCCESSFUL");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load user email ("+e.toString()+")","LOAD-EMAIL-FAILED");
        }
    }

    /**
     * Function for setting new password for user
     * @throws SQLException
     */
    public void set_password() throws SQLException {
        String query = "UPDATE USER_DATA SET user_password = ? where user_id=?;";
        TrackApiApplication.database.log("Password reset for user_id "+user_id+" invoked!","USER-RESET");
        try{
            MailConnector mc = new MailConnector();
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_password);
            ppst.setInt(2,user_id);
            ppst.execute();
            TrackApiApplication.database.log("New password set for user_id "+user_id+".","USER-RESET-SUCCESS");
            load_email();
            if ( !user_email.equals("")){
                try{
                    mc.send(this.user_email,"TRACK NOTIFICATION PASSWORD CHANGED","Your password was changed.");
                }catch(Exception e){
                    TrackApiApplication.database.log("Failed to send email ("+e.toString()+")","MAIL-SEND-ERROR");
                }
            }
            else{
                TrackApiApplication.database.log("Failed to send mail. user_mail empty","MAIL-SEND-EMPTY");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to reset user password ("+e.toString()+")","USER-RESET-FAILED");
        }
    }

    /**
     * Function for checking if given password is correct using user_session token
     */
    public void check_password() throws SQLException {
        user_id = TrackApiApplication.database.get_userid_bysession(this.user_session);
        String query = "SELECT user_password FROM USER_DATA WHERE user_id = ?;";

        TrackApiApplication.database.log("Checking password for user_id "+user_id,"PASSWORD-CHECK");
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                if ( !rs.getString("user_password").equals(user_password)){
                    TrackApiApplication.database.log("Password check failed, wrong password","PASSWORD-CHECK-VALIDATION");
                    user_id = -5;
                }
                else{
                    TrackApiApplication.database.log("Password check successfull.","PASWORD-CHECK-SUCCESS");
                    user_id = 77;
                }
            }
            else{
                TrackApiApplication.database.log("Cannot find user with user_id "+user_id,"PASSWORD-CHECK-NOUSER");
                user_id = -5;
            }

        }catch(Exception e){
            TrackApiApplication.database.log("Failed to check user password ("+e.toString()+")","PASSWORD-CHECK-FAILED");
        }
    }

    /**
     * Function for checking user password using user_id field
     * @throws SQLException
     */
    public void check_password_fromuser_login() throws SQLException {
        String query = "SELECT user_password FROM USER_DATA WHERE user_login = ?;";

        TrackApiApplication.database.log("Checking password for user_id "+user_id,"PASSWORD-CHECK");
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_login);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                if ( !rs.getString("user_password").equals(user_password)){
                    TrackApiApplication.database.log("Password check failed, wrong password","PASSWORD-CHECK-VALIDATION");
                    user_id = -5;
                }
                else{
                    TrackApiApplication.database.log("Password check successfull.","PASWORD-CHECK-SUCCESS");
                    user_id = 77;
                }
            }
            else{
                TrackApiApplication.database.log("Cannot find user with user_id "+user_id,"PASSWORD-CHECK-NOUSER");
                user_id = -5;
            }

        }catch(Exception e){
            TrackApiApplication.database.log("Failed to check user password ("+e.toString()+")","PASSWORD-CHECK-FAILED");
        }
    }
}
