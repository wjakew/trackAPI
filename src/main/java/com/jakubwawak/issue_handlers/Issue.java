/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.issue_handlers;

import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Issue {

    /**
     * CREATE TABLE ISSUE
     * (
     *   issue_id INT PRIMARY KEY AUTO_INCREMENT,
     *   user_id INT,
     *   project_id INT,
     *   issue_name VARCHAR(200),
     *   issue_desc TEXT,
     *   issue_priority INT, -- VALUES FROM 1 TO 5
     *   issue_group INT, -- field for setting states as PLANNED IN WORK DONE VALUES FROM 0 TO 2
     *   issue_state VARCHAR(100), -- CODES: DONE, UNDONE, date ( time to finish )
     *   issue_time_creation TIMESTAMP,
     *   issue_time_due TIMESTAMP,
     *
     *   CONSTRAINT fk_issue FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
     *   CONSTRAINT fk_issue2 FOREIGN KEY (project_id) REFERENCES TASK(task_id)
     * );
     */

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
    public int issue_id;
    public int user_id;
    public int project_id;
    public String issue_name;
    public String issue_desc;
    public int issue_priority;
    public int issue_group;
    public String issue_state;
    public LocalDateTime issue_time_creation;
    public LocalDateTime issue_time_due;

    /**
     * Connstructor
     */
    public Issue(){
        flag = 0;
        issue_id = -1;
        user_id = -1;
        project_id = -1;
        issue_name = "";
        issue_desc = "";
        issue_priority = 1;
        issue_group = 1;
        issue_state = "UNDONE";
        issue_time_creation = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        issue_time_due = null;
    }

    /**
     * Connstructor with database support
     * @param rs
     */
    public Issue(ResultSet rs) throws SQLException {
        flag = 1;
        issue_id = rs.getInt("issue_id");
        user_id = rs.getInt("user_id");
        project_id = rs.getInt("project_id");
        issue_name = rs.getString("issue_name");
        issue_desc = rs.getString("issue_desc");
        issue_priority =rs.getInt("issue_priority");
        issue_group =rs.getInt("issue_group");
        issue_state = rs.getString("issue_state");
        issue_time_creation = rs.getObject("issue_time_creation",LocalDateTime.class);
        issue_time_due = null;
    }

    /**
     * Function for getting issue name
     * @throws SQLException
     */
    public void get_name() throws SQLException {
        String query = "SELECT issue_name FROM ISSUE WHERE issue_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,issue_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                issue_name = rs.getString("issue_name");
            }
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to get issue name ("+e.toString()+")","ISSUE-NAME-FAILED");
        }
    }

    /**
     * Function for loading database
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO ISSUE\n" +
                "(user_id,project_id,issue_name,issue_desc," +
                "issue_priority,issue_group,issue_state,issue_time_creation," +
                "issue_time_due)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?,?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setInt(2,project_id);
            ppst.setString(3,issue_name);
            ppst.setString(4,issue_desc);
            ppst.setInt(5,issue_priority);
            ppst.setInt(6,issue_group);
            ppst.setString(7,issue_state);
            ppst.setObject(8,issue_time_creation);
            ppst.setObject(9,issue_time_due);

            ppst.execute();
            TrackApiApplication.database.log("Loaded to database issue "+issue_name+" for project_id "+project_id,"ISSUE-LOAD");
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load issue to database ("+e.toString()+")","ISSUE-LOAD-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing issue
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM ISSUE WHERE issue_id=?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,this.issue_id);

            ppst.execute();
            TrackApiApplication.database.log("Issue issue_id "+issue_id+" removed","ISSUE-REMOVE-SUCCESS");
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove issue ("+e.toString()+")","ISSUE-REMOVE-FAILED");
            flag = -1;
        }
    }

}
