/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.shared_handler;

import com.jakubwawak.trackAPI.TrackApiApplication;
import com.jakubwawak.users.User_Data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Shared {

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -6 - issue not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */

    public int flag;

    public int user_id;
    public int project_id;

    /**
     * Constructor
     */
    public Shared(){
        flag = 0;
        user_id = -1;
        project_id = -1;
    }

    /**
     * Constructor with database support
     * @param rs
     * @throws SQLException
     */
    public Shared(ResultSet rs) throws SQLException {
        user_id = rs.getInt("user_id");
        project_id = rs.getInt("project_id");
        flag = 1;
    }

    /**
     * Function for loading object to database
     * @throws SQLException
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO SHARED_ELEMENTS (user_id,project_id) VALUES(?,?);";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setInt(2,project_id);

            ppst.execute();
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load to database shared elements ("+e.toString()+")","SHARE-LOAD-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing data from share
     * @throws SQLException
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM SHARED_ELEMENTS where project_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ppst.execute();
            flag = 1;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to remove shared data ("+e.toString()+")","SHARED-REMOVE-ERROR");
        }
    }

    /**
     * Function for loading shared data view
     * @return String
     */
    public String load_view() throws SQLException {
        String data = "";
        String query = "SELECT project_name,user_id from PROJECT where project_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                User_Data user = new User_Data();
                user.user_id = user_id;
                user.get_login();
                data = data + project_id +": "+rs.getString("project_name")+",owner: "+user.user_login;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get view of shared project ("+e.toString()+")","SHARED-VIEW-FAILED");
        }
        return data;
    }
}
