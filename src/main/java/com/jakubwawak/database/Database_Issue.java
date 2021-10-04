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

public class Database_Issue {

    Database_Connector database;

    /**
     * Constructor
     */
    public Database_Issue(){
        database = TrackApiApplication.database;
    }

    /**
     * Function for getting all issues
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> get_all_issues_glances(int user_id,int mode) throws SQLException {
        String query = "";
        if ( mode == 0)
            query = "SELECT * FROM ISSUE WHERE user_id = ? and issue_state = 'UNDONE';";
        else
            query = "SELECT * FROM ISSUE WHERE user_id = ? and project_id = ? and issue_state = 'UNDONE';";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            if (mode == 0)
                ppst.setInt(1,user_id);
            else{
                ppst.setInt(1,user_id);
                ppst.setInt(2,mode);
            }
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add(rs.getInt("issue_id")+": "+rs.getString("issue_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
            TrackApiApplication.database.log("Issues glances loaded","ISSUE-GLANCES-LOADED");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get all issues glances ("+e.toString()+")","ISSUE-GLANCES-GET-FAILED");
            data.add("error");
        }
        return data;
    }
}
