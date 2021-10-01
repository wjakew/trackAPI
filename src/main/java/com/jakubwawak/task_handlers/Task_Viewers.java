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
}
