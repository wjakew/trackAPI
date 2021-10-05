/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.issue_handlers.Issue;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    /**
     * Function for getting issues
     * @param issue_id
     * @return Issue
     */
    public Issue get_issue(int issue_id) throws SQLException {
        String query = "SELECT * FROM ISSUE where issue_id=?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,issue_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                TrackApiApplication.database.log("Issue loaded issue_id "+issue_id,"ISSUE-GET-SUCCESS");
                return new Issue(rs);
            }
            return null;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get issue issue_id "+issue_id+" ("+e.toString()+")","ISSUE-GET-FAILED");
            return null;
        }
    }

    /**
     * Function for updating group
     * @param issue_id
     * @param issue_group
     * @return Issue
     */
    public int update_group(int issue_id,int issue_group,int user_id) throws SQLException {
        String query = "";
        TrackApiApplication.database.log("Reques for update group ("+issue_id+"/"+issue_group+"/"+user_id+")","ISSUE-GROUP");
        if (issue_group == 3) {
            query = "UPDATE ISSUE SET issue_group = 3, issue_state = 'DONE' where issue_id = ?;";
        } else {
            query = "UPDATE ISSUE SET issue_group = ?, issue_state = 'UNDONE' where issue_id = ?;";
        }
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            switch(issue_group){
                case 3:
                    ppst.setInt(1,issue_id);
                    break;
                default:
                    ppst.setInt(1,issue_group);
                    ppst.setInt(2,issue_id);
                    break;
            }
            ppst.execute();
            TrackApiApplication.database.log("issue_group for issue_id "+issue_id+" updated","ISSUE-GROUP-UPDATE");
            Database_Log dl = new Database_Log();
            dl.object_log("issue_group updated.","ISSUE",issue_id,user_id);
            return 1;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to update issue_group ("+e.toString()+")","ISSUE-GROUP-FAILED");
            return -1;
        }
    }

    /**
     * Function for getting issue history
     * @param issue_id
     * @return ArrayList
     */
    public ArrayList<String> get_issue_history(int issue_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM OBJECT_HISTORY where history_category = 'ISSUE' and history_object_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,issue_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getObject("history_object_time", LocalDateTime.class).toString()+" : "+rs.getString("history_desc"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
            TrackApiApplication.database.log("Issue history loaded","ISSUE-HISTORY-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get issue history ("+e.toString()+")","ISSUE-HISTORY-FAILED");
        }
        return data;
    }

    /**
     * Function for getting archived issues
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> get_issue_archive(int user_id) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM ISSUE where issue_state = 'DONE' and user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);

            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getInt("issue_id")+": "+rs.getString("issue_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
            TrackApiApplication.database.log("Issue archive loaded","ISSUE-ARCHIVE-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get issue archive ("+e.toString()+")","ISSUE-ARCHVE-FAILED");
        }
        return data;
    }
}
