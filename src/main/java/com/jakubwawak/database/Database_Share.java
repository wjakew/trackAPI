/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.project_handlers.Project;
import com.jakubwawak.trackAPI.TrackApiApplication;
import com.jakubwawak.users.User_Data;

import java.nio.channels.spi.AbstractSelectionKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database_Share {

    Database_Connector database;

    /**
     * Connector
     */
    public Database_Share(){
        database = TrackApiApplication.database;
    }

    /**
     * Function for loading user shared projects
     * @return ArrayList<>
     */
    public ArrayList<String> load_user_shares(int user_id) throws SQLException {
        ArrayList<String> response = new ArrayList<>();
        Database_Project dp = new Database_Project();
        ArrayList<String> data = dp.get_all_project_glances(user_id);
        try{
            String query = "SELECT user_id FROM SHARED_ELEMENTS WHERE project_id = ?; ";
            for(String glance : data){
                int project_id = Integer.parseInt(glance.split(":")[0]);
                try{
                    PreparedStatement ppst = database.con.prepareStatement(query);
                    ppst.setInt(1,project_id);
                    ResultSet rs = ppst.executeQuery();
                    if ( rs.next()){
                        User_Data user = new User_Data();
                        user.user_id = rs.getInt("user_id");
                        user.load_data(user.user_id);
                        response.add(project_id+": project shared to "+user.user_login);
                    }
                }catch(SQLException e){
                    database.log("Failed to load user shares ("+e.toString()+")","USER-SHARES-FAILED");
                    response.add("error");
                }
            }
            if  (response.size() == 0){
                response.add("Empty");
            }
        }catch(Exception e){
            database.log("Failed to load user shares ("+e.toString()+")","USER-SHARE-FAILED");
        }
        return response;
    }

    /**
     * Function for loading shared to user data
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> load_sharedtouser(int user_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT project_id WHERE user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                Project project = new Project();
                project.project_id = rs.getInt("project_id");
                project.database_load();
                data.add(project.project_id+":"+project.project_name);
            }
            if ( data.size() == 0)
                data.add("Empty");
        } catch (SQLException e) {
            database.log("Failed to load shared to user projects ("+e.toString()+")","USER-SHARED-FAILED");
            data.add("error");
        }
        return data;
    }
}
