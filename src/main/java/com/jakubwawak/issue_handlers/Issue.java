package com.jakubwawak.issue_handlers;

import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
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
     * -88 - database error when checking session token
     */
    int flag;
    int issue_id;
    int user_id;
    int project_id;
    String issue_name;
    String issue_desc;
    int issue_priority;
    int issue_group;
    String issue_state;
    LocalDateTime issue_time_creation;
    LocalDateTime issue_time_due;

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
        issue_state = "";
        issue_time_creation = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        issue_time_due = null;
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
            TrackApiApplication.database.log("Loaded to databaseissue "+issue_name+" for project_id "+project_id,"ISSUE-LOAD");
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load issue to database ("+e.toString()+")","ISSUE-LOAD-FAILED");
            flag = -1;
        }
    }

}
