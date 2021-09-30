/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.task_handlers;

import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.nio.channels.spi.AbstractSelectionKey;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Task {

    /**
     * CREATE TABLE TASK
     * (
     *   task_id INT PRIMARY KEY AUTO_INCREMENT,
     *   user_id INT,
     *   project_id INT,
     *   task_name VARCHAR(200),
     *   task_desc TEXT,
     *   task_priority INT, -- VALUES FROM 1 TO 5
     *   task_state VARCHAR(100), -- CODES: UNDONE, DONE, date ( time to finish )
     *
     *   CONSTRAINT fk_task FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id),
     *   CONSTRAINT fk_task2 FOREIGN KEY (project_id) REFERENCES TASK(task_id)
     * );
     */

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -99 - session has expired
     * -11 - invalid app token
     * -88 - database error when checking session token
     */
    public int flag;

    public int task_id;
    public int user_id;
    public int project_id;
    public String task_name;
    public String task_desc;
    public int task_priority;
    public String task_state;

    /**
     * Constructor
     */
    public Task(){
        task_id = -1;
        user_id = -1;
        project_id = -1;
        task_name = "";
        task_desc = "";
        task_priority = 1;
        task_state = "";
    }

    /**
     * Function for loading object to database
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO TASK\n" +
                "(user_id,project_id,task_name,task_desc,task_priority,task_state)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?);";

        try{
            TrackApiApplication.database.log("Trying to load task to database","TASK-LOAD");
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setInt(1,user_id);
            ppst.setInt(2,project_id);
            ppst.setString(3,task_name);
            ppst.setString(4,task_desc);
            ppst.setInt(5,task_priority);
            ppst.setString(6,task_state);

            ppst.execute();
            TrackApiApplication.database.log("Task "+task_name+" loaded successfully","TASK-LOAD-SUCCESS");
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load task ("+e.toString()+")","TASK-LOAD-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for updating task data
     * @param code
     * @param value
     * codes:
     * name
     * desc
     * state
     */
    public void update(String code,String value) throws SQLException {
        String field_name = "";
        String query = "UPDATE TASK SET "+field_name+" = ? where task_id=?;";
        TrackApiApplication.database.log("Trying to update task data code: "+code,"TASK-UPDATE");
        switch(code){
            case "name":
                field_name = "task_name";
                break;
            case "desc":
                field_name = "task_desc";
                break;
            case "state":
                field_name = "task_state";
                break;
        }
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,value);
            ppst.execute();
            flag = 1;
            TrackApiApplication.database.log("Updated task data","TASK-UPDATE-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to update task ("+e.toString()+")","TASK-UPDATE-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing task from database
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM TASK WHERE task_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,task_id);
            ppst.execute();
            TrackApiApplication.database.log("Task removed. task_id"+task_id,"TASK-REMOVE-SUCCESS");
            flag = 1;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to remove task ("+e.toString()+")","TASK-REMOVE-FAILED");
            flag = -1;
        }

    }




}
