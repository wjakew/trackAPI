/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;


import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database_Task {

    Database_Connector database;

    public Database_Task(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for getting task glances
     * @param mode
     * @return
     * modes:
     * 0 - all user tasks
     * >0 - tasks for given project_id
     */
    public ArrayList<String> get_task_glances(int mode,int user_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "";
        switch(mode){
            case 0:
                query = "SELECT * FROM TASK where user_id = ?;";
                break;
            default:
                query = "SELECT * FROM TASK where project_id = ?;";
                break;
        }

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            switch(mode){
                case 0:
                    ppst.setInt(1,user_id);
                    break;
                default:
                    ppst.setInt(1,mode);
                    break;
            }

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add(rs.getInt("task_id")+": "+rs.getString("task_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }

        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to tasks glances ("+e.toString()+")","TASK-GLANCES-ERROR");
            data.add("error");
        }
        return data;
    }
}
