/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.task_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Task_Setters {

    /**
     * Function for adding task to database
     * @param app_token
     * @param session_token
     * @param project_id
     * @param task_name
     * @param task_desc
     * @param task_priority
     * @return
     * @throws SQLException
     */
    @GetMapping("/task-set/{app_token}/{session_token}/{project_id}/{task_name}/{task_desc}/{task_priority}")
    public Task task_set(@PathVariable String app_token,@PathVariable String session_token,
                         @PathVariable int project_id, @PathVariable String task_name,
                         @PathVariable String task_desc, @PathVariable int task_priority) throws SQLException {
        Task task = new Task();
        Session_Validator sv = new Session_Validator(session_token);

        if ( sv.connector_validation(app_token)){
            task.project_id = project_id;
            task.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            task.task_name = task_name;
            task.task_desc = task_desc;
            task.task_priority = task_priority;
            task.task_state = "UNDONE";

            task.database_load();
        }
        else{
            task.flag = sv.flag;
        }
        return task;
    }

    /**
     * Function for updating task
     * @param app_token
     * @param session_token
     * @param task_id
     * @param code
     * @param value
     * @return Task
     *
     */
    @GetMapping("/task-update/{app_token}/{session_token}/{task_id}/{code}/{value}")
    public Task task_update(@PathVariable String app_token, @PathVariable String session_token,
                            @PathVariable int task_id, @PathVariable String code, @PathVariable String value) throws SQLException {
        Task task = new Task();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            task.task_id = task_id;
            task.update(code,value);
        }
        else{
            task.flag = sv.flag;
        }
        return task;
    }

    @GetMapping ("/task-remove/{app_token}/{session_token}/{task_id}")
    public Task task_remove(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int task_id ) throws SQLException {
        Task task = new Task();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            task.task_id = task_id;
            task.remove();
        }
        else{
            task.flag = sv.flag;
        }
        return task;
    }

}
