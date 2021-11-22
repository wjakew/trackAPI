/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing and presenting user configuration data
 */
public class User_Configuration {

    public int flag;
    public int user_id;
    public String config1;
    public String config2;
    public String config3;


    /**
     * Constructor
     */
    public User_Configuration(){
        flag = 0;
        user_id = -1;
        config1 = "";
        config2 = "";
        config3 = "";
    }

    /**
     * Constructor with database support
     * @param user_id
     */
    public User_Configuration(int user_id){
        flag = 0;
        this.user_id = user_id;
        config1 = "";
        config2 = "";
        config3 = "";
    }

    /**
     * Function for loading configuration
     */
    public void load_configuration() throws SQLException {
        String query = "SELECT * FROM USER_CONFIGURATION WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                config1  = rs.getString("config1");
                config2  = rs.getString("config2");
                config3 = rs.getString("config3");
                TrackApiApplication.database.log("Loaded user configuration","USER-CONFIGURATION");
                flag = 1;
            }
            else{
                TrackApiApplication.database.log("No configuration for user","USER-CONFIGURATION-EMPTY");
                flag = -1;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load configuration","USER-CONFIGURATION-FAILED");
        }
    }
}
