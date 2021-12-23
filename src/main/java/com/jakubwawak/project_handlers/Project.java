/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.project_handlers;

import com.jakubwawak.shared_handler.Shared;
import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Project {
    /**
     * CREATE TABLE PROJECT
     * (
     *   project_id INT PRIMARY KEY AUTO_INCREMENT,
     *   user_id INT,
     *   project_name VARCHAR(250),
     *   project_desc TEXT,
     *   project_creation_date TIMESTAMP,
     *   project_state VARCHAR(100), -- CODES: active, unactive, date ( time to finish )
     *
     *   CONSTRAINT fk_project FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
     * );
     */

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -6 - project not found
     * -99 - session has expired
     * -11 - invalid app token
     * -88 - database error when checking session token
     */
    public int flag;

    public int project_id;
    public int user_id;
    public String project_name;
    public String project_desc;
    public LocalDateTime project_creation_date;
    public String project_state;

    /**
     * Constructor
     */
    public Project(){
        flag = 0;

        project_id = -1;
        user_id = -1;
        project_name = "";
        project_desc = "";
        project_creation_date = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        project_state = "";
    }

    /**
     * Constructor with database load
     * @param project_id
     */
    public Project(int project_id) throws SQLException {
        String query = "SELECT * FROM PROJECT WHERE project_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                this.project_id = rs.getInt("project_id");
                user_id = rs.getInt("user_id");
                project_name = rs.getString("project_name");
                project_desc = rs.getString("project_desc");
                project_creation_date = rs.getObject("project_creation_date",LocalDateTime.class);
                project_state = rs.getString("project_state");
                flag = 1;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load project from database ("+e.toString()+")","PROJECT-LOAD-FAILED");
            flag = -1;
        }
    }

    /**
     * Constructor with database support
     * @param rs
     */
    public Project (ResultSet rs) throws SQLException {
        flag = 0;

        project_id = rs.getInt("project_id");
        user_id = rs.getInt("user_id");
        project_name = rs.getString("project_name");
        project_desc = rs.getString("project_desc");
        project_creation_date = rs.getObject("project_creation_date",LocalDateTime.class);
        project_state = rs.getString("project_state");
    }

    /**
     * Function for loading data to database
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO PROJECT\n" +
                "(project_name,project_desc,project_creation_date,project_state,user_id)\n" +
                "VALUE\n" +
                "(?,?,?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,project_name);
            ppst.setString(2,project_desc);
            ppst.setObject(3,project_creation_date);
            ppst.setString(4,project_state);
            ppst.setInt(5,user_id);
            ppst.execute();
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load project to database ("+e.toString()+")","PROJECT-LOAD-ERROR");
            flag = -1;
        }
    }

    /**
     * Function for updating
     * @param code
     * @param value
     * codes:
     * name - updates project_name
     * desc - updates project_desc
     * state - updates project_state
     */
    public void update(String code,String value) throws SQLException {
        TrackApiApplication.database.log("Trying to update project, project_id: "+project_id,"PROJECT-UPDATE");
        String data = "";
        String query = "UPDATE PROJECT SET "+data+"= ? WHERE project_id=?;";
        switch(code){
            case "name":
                data = "project_name";
                break;
            case "desc":
                data = "project_desc";
                break;
            case "state":
                data = "project_state";
                break;
        }

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,value);
            ppst.setInt(2,this.project_id);

            ppst.execute();
            TrackApiApplication.database.log("Updated project "+code
                    +" project_id "+project_id,"PROJECT-UPDATE-SUCCESSFUL");
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to update project ("+e.toString()+")","PROJECT-UPDATE-FAILED");
            flag = -1;
        }
    }
    /**
     * Function for removing Project by given project_id
     * @return Project
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM PROJECT WHERE project_id = ?;";
        try{
            remove_tasks();
            remove_issues();
            Shared shared = new Shared();
            shared.project_id = project_id;
            shared.remove();
            TrackApiApplication.database.log("Trying to remove project_id "+project_id,"PROJECT-REMOVE");
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,this.project_id);
            ppst.execute();
            flag = 1;
            TrackApiApplication.database.log("Project project_id "+project_id+" removed.","PROJECT-REMOVE-SUCCESSFUL");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove project ("+e.toString()+")","PROJECT-REMOVE-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing projects tasks
     */
    private void remove_tasks() throws SQLException {
        String query = "DELETE FROM TASK WHERE project_id = ?;";
        TrackApiApplication.database.log("Removing tasks belong to project_id "+project_id,"PROJECT-TASK-REMOVE");
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ppst.execute();
            TrackApiApplication.database.log("Project task removed","PROJECT-TASK-REMOVE-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove task from project_id "+project_id
                    +" ("+e.toString()+")","PROJECT-TASK-REMOVE-FAILED");

        }
    }

    /**
     * Function for removing project issues
     * @throws SQLException
     */
    private void remove_issues() throws SQLException {
        String query = "DELETE FROM ISSUE WHERE project_id =?;";
        TrackApiApplication.database.log("Removing issues belong to project_id "+project_id,"PROJECT-ISSUE-REMOVE");

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,project_id);
            ppst.execute();
            TrackApiApplication.database.log("Issues removed","PROJECT-ISSUES-SUCCESS");
        }catch(SQLException e ){
            TrackApiApplication.database.log("Failed to remove issues belong to project_id "+project_id+" ("+e.toString()+")","PROJECT-ISSUE-FAILED");
        }
    }
}
