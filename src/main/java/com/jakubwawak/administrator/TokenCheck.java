/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for creating auth token
 */
public class TokenCheck {

    public String token,status;

    /**
     * Constructor
     */
    public TokenCheck(String token){
        this.token = token;
        status = "none";
    }

    /**
     * Class for creating token
     * @return String
     */
    public void load() throws SQLException {
        String query = "SELECT token_value FROM TOKEN where token_value = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,token);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                status = "token_exists";
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load token "+token+" to check ("+e.toString()+")","ERROR-TKN1");
        }
    }

    /**
     * Function for checking token
     * @return Integer
     * @throws SQLException
     */
    public int check() throws SQLException {
        String query = "SELECT token_value FROM TOKEN where token_value = ?;";

        try{
            TrackApiApplication.database.log("Trying to validate app token ("+token+")","TOKENCHECK");
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,token);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ) {
                TrackApiApplication.database.log("Token ("+token+") validated!","TOKENCHECK");
                return 1;
            }
            TrackApiApplication.database.log("Token not found","TOKENCHECK");
            return 0;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load token "+token+" to check ("+e.toString()+")","ERROR-TKN1");
            return -1;
        }
    }
}
