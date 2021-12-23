/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.task_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Task;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Task_Viewers {

    @GetMapping("/task-viewer/{app_token}/{session_token}/{mode}")
    public Viewer get_all_tasks(@PathVariable String app_token, @PathVariable String session_token,@PathVariable int mode) throws SQLException {
        TrackApiApplication.database.log("Loading glances for task in mode: "+mode,"TASK-VIEWER-GLANCES");
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        viewer.sv = sv;
        TrackApiApplication.database.log("NEW JOB: TASK-VIEWER","JOB-GOT");
        if (sv.connector_validation(app_token)){
            Database_Task dt = new Database_Task(TrackApiApplication.database);
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            viewer.view = dt.get_task_glances(mode,user_id);
            TrackApiApplication.database.log("Glances loaded","TASK-VIEWER-GLANCES-SUCCESS");
        }
        else{
            TrackApiApplication.database.log("Validation error","TASK-VIEWER-GLANCES-VALIDATION");
        }
        return viewer;
    }

    @GetMapping("/task-get/{app_token}/{session_token}/{task_id}")
    public Task get_task(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int task_id) throws SQLException {
        Task task = new Task();
        Database_Task dt = new Database_Task(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TASK-GET","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            task = dt.get_task(task_id);
            if ( task!= null) {
                TrackApiApplication.database.log("Data for task task_id " + task_id + "loaded.", "TASK-DATA-GET");
                return task;
            }
            TrackApiApplication.database.log("Data for task not found","TASK-DATA-FAILED");
            task.flag = -1;
            return task;
        }
        else{
            task.flag = sv.flag;
        }
        return task;
    }

    @GetMapping("/task-history/{app_token}/{session_token}/{task_id}")
    public Viewer get_task_history(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int task_id) throws SQLException {
        Viewer view = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TASK-HISTORY","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Task dt = new Database_Task(TrackApiApplication.database);
            view.view = dt.get_task_history(task_id);
            TrackApiApplication.database.log("View for task history loaded","TASK-VIEWER-HISTORY");
        }
        else{
            view.flag = sv.flag;
        }
        return view;
    }

    @GetMapping ("/task-archive/{app_token}/{session_token}")
    public Viewer get_task_archive(@PathVariable String app_token,@PathVariable String session_token) throws SQLException {
        Viewer view = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Task dt = new Database_Task(TrackApiApplication.database);
            view.view = dt.load_archive(TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            view.flag = sv.flag;
        }
        return view;
    }
}
