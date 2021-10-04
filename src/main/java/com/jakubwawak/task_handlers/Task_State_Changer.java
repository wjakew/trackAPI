package com.jakubwawak.task_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Task;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Object for changing key components of Task object
 */
@RestController
public class Task_State_Changer {

    @GetMapping("/task-done/{app_token}/{session_token}/{task_id}")
    public Task set_done(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int task_id) throws SQLException {
        Database_Task dt = new Database_Task(TrackApiApplication.database);
        Task task = new Task();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            if( dt.set_task_done(task_id) == 1){
                task.flag = 1;
            }
        }
        else{
            task.flag = -1;
        }
        return task;
    }

    @GetMapping("/task-open/{app_token}/{session_token}/{task_id}")
    public Task set_open(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int task_id) throws SQLException {
        Database_Task dt = new Database_Task(TrackApiApplication.database);
        Task task = new Task();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            if ( dt.set_task_open(task_id) == 1){
                task.flag = 1;
            }
        }
        else{
            task.flag = -1;
        }
        return task;
    }
}
