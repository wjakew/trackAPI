/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database_Project {

    Database_Connector database;

    /**
     * Constructor
     */
    public Database_Project(){
        database = TrackApiApplication.database;
    }

    /**
     * Function for getting project glances
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> get_all_project_glances(int user_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();

        String query = "SELECT * FROM PROJECT WHERE user_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            TrackApiApplication.database.log("Query: "+ppst.toString(),"PROJECT-GLANCES");
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add(rs.getInt("project_id")+": "+rs.getString("project_name"));
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get project glances ("+e.toString()+")","PROJECT-GLANCES-ERROR");
        }
        return data;
    }
}
