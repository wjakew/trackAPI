/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.issue_handlers.Issue;
import com.jakubwawak.task_handlers.Task;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Function for maintaning board data on database
 */
public class Database_Board {

    public Database_Connector database;

    /**
     * Constructor
     */
    public Database_Board(){
        this.database = TrackApiApplication.database;
    }

    /**
     * Function for loading board glances
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> load_board_glances(int user_id) throws SQLException {
        String query = "SELECT * FROM BOARD WHERE user_id=?;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getInt("board_id")+": "+rs.getString("board_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load board glances ("+e.toString()+")","BOARD-GLANCES-FAILED");
            data.add("error");
        }
        return data;
    }

    /**
     * Function for loading list of board elements
     * @param board_id
     * @return ArrayList
     */
    public ArrayList<String> load_board_elements(int board_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT object_id,board_list_object FROM BOARD_ELEMENT WHERE board_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,board_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                switch(rs.getString("board_list_object")){
                    case "ISSUE":
                        Issue issue = new Issue();
                        issue.issue_id = rs.getInt("object_id");
                        issue.get_name();
                        if ( issue.check_state() == 0)
                            data.add(issue.issue_id+":ISSUE| "+issue.issue_name);
                        break;
                    case "TASK":
                        Task task = new Task();
                        task.task_id = rs.getInt("object_id");
                        task.get_name();
                        if (task.check_state() == 0)
                            data.add(task.task_id+":TASK| "+task.task_name);
                        break;
                }
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to load board elements","BOARDEL-VIEWER-FAILED");
            data.add("error");
        }
        return data;
    }
}
