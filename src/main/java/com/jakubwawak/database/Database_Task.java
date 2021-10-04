/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;


import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.task_handlers.Task;
import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.nio.channels.spi.AbstractSelectionKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
                query = "SELECT * FROM TASK where user_id = ? and task_state = 'UNDONE';";
                break;
            default:
                query = "SELECT * FROM TASK where project_id = ? and task_state = 'UNDONE';";
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

    /**
     * Function for getting task
     * @param task_id
     * @return Task
     */
    public Task get_task(int task_id) throws SQLException {
        String query = "SELECT * FROM TASK where task_id=?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,task_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                TrackApiApplication.database.log("Loaded task data for task_id "+task_id,"TASK-GET-SUCCESS");
                return new Task(rs);
            }
            return null;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get task ("+e.toString()+")","TASK-GET-FAILED");
            return null;
        }
    }

    /**
     * Function for getting task history
     * @param task_id
     * @return
     * @throws SQLException
     */
    public ArrayList<String> get_task_history(int task_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM OBJECT HISTORY WHERE object_id = ? and object_category = 'TASK';";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,task_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getObject("history_object_time", LocalDateTime.class).toString()+" - "+rs.getString("history_desc"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
            TrackApiApplication.database.log("Created task history","TASK-HISTORY-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get task history ("+e.toString()+")","TASK-HISTORY-FAILED");
        }
        return data;
    }

    /**
     * Function fro setting task done
     * @param task_id
     * @return Integer
     */
    public int set_task_done(int task_id) throws SQLException {
        String query = "UPDATE TASK SET task_state = 'DONE' where task_id =?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,task_id);
            ppst.execute();
            TrackApiApplication.database.log("Task task_id "+task_id+" set to done","TASK-DONE");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Task failed to set done ("+e.toString()+")","TASK-DONE-FAILED");
            return -1;
        }
    }

    /**
     * Function for opening again tasks
     * @param task_id
     * @return Integer
     * @throws SQLException
     */
    public int set_task_open(int task_id) throws SQLException {
        String query = "UPDATE TASK SET task_state = 'UNDONE' where task_id =?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,task_id);
            ppst.execute();
            TrackApiApplication.database.log("Task task_id "+task_id+" set to open","TASK-DONE");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Task failed to set open ("+e.toString()+")","TASK-DONE-FAILED");
            return -1;
        }
    }

    /**
     * Function for loading view of archived tasks
     * @param user_id
     * @return ArrayList
     * @throws SQLException
     */
    public ArrayList<String> load_archive(int user_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM TASK WHERE task_state = 'DONE' and user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getInt("task_id")+": "+rs.getString("task_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
            TrackApiApplication.database.log("Archived tasks loaded","TASK-ARCHIVE-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load archived tasks ("+e.toString()+")","TASK-ACHIVE-ERROR");
            data.add("error");
        }
        return data;
    }
}
