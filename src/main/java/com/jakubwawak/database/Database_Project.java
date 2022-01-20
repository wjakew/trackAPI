/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.project_handlers.Project;
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

    /**
     * Function for getting all projects that user is a member
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> get_all_project_member_glances(int user_id) throws SQLException {
        ArrayList<String> glances = new ArrayList<>();
        String query = "SELECT * FROM PROJECT_MEMBERS WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                glances.add(get_project(rs.getInt("project_id")).get_glance());
            }
            if (glances.size() == 0){
                glances.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get all project where user is a member ("+e.toString()+")","PROJECT-MEMBER-FAILED");
            glances.add("error");
        }
        return glances;
    }

    /**
     * Function for getting project
     * @param project_id
     * @return Project
     */
    public Project get_project(int project_id) throws SQLException {
        String query = "SELECT * FROM PROJECT WHERE project_id=?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                TrackApiApplication.database.log("Project project_id "+project_id+" loaded.","PROJECT-GET-SUCCESS");
                return new Project(rs);
            }
            return null;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get project ("+e.toString()+")","PROJECT-GET-ERROR");
            return null;
        }
    }
}
