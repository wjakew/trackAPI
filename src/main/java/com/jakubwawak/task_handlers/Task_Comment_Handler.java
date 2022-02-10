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
public class Task_Comment_Handler {

    @GetMapping("/task-comment-list/{app_token}/{session_token}/{task_id}")
    public Viewer list_comments(@PathVariable String app_token, @PathVariable String session_token, @PathVariable int task_id) throws SQLException {
        Viewer view = new Viewer();
        TrackApiApplication.database.log("NEW JOB: TASK-COMMENT-LIST","JOB-GOT");
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_Task dt = new Database_Task(TrackApiApplication.database);
            view.view = dt.list_comments(task_id);
            view.flag = sv.flag;
            TrackApiApplication.database.log("Comment list loaded","TASK_C-LIST");
        }
        else{
            TrackApiApplication.database.log("Failed to authorize","TASK_C-LIST-FAILED");
            view.flag = sv.flag;
        }
        return view;
    }

    @GetMapping("/task-comment-add/{app_token}/{session_token}/{task_id}/{task_comment_content}")
    public Task_Comment add_comments(@PathVariable String app_token,@PathVariable String session_token,
            @PathVariable int task_id,@PathVariable String task_comment_content) throws SQLException {
        Task_Comment tc = new Task_Comment();
        TrackApiApplication.database.log("NEW JOB: TASK-COMMENT-ADD","JOB-GOT");
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Task dt = new Database_Task(TrackApiApplication.database);
            if ( dt.add_comment(TrackApiApplication.database.get_userid_bysession(session_token),
                    task_id,task_comment_content) == 1){
                TrackApiApplication.database.log("Added comment!","TASK_C-ADD");
                tc.flag = 1;
                TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                        session_token,"Trying to add comment on task ("+task_id+")","Comment added!");
            }
            else{
                TrackApiApplication.database.log("Failed to authorize","TASK_C-ADD-FAILED");
                tc.flag = -1;
            }
        }
        return tc;
    }
}
