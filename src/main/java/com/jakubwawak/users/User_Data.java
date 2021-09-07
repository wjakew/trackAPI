package com.jakubwawak.users;

import com.jakubwawak.administrator.Password_Validator;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.nio.channels.spi.AbstractSelectionKey;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
     * Function for login to database
     * @param user_login
     * @param password
     */
    public void login(String user_login,String password) throws SQLException {
        String query = "SELECT * FROM USER_DATA where user_login = ? and user_password = ?;";

        try{
            Password_Validator pv = new Password_Validator(password);
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,user_login);
            ppst.setString(2,pv.hash());
            TrackApiApplication.database.log("Trying to authorize "+user_login+" with "+password,"USER-LOGIN");

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

        } catch (SQLException | NoSuchAlgorithmException e) {
            TrackApiApplication.database.log("Failed to login user ("+e.toString()+")","ERROR-USR3");
        }
    }


}
