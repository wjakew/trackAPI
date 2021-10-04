/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.issue_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class Issue_Setters {

    /**
     * Function for setting issue on database
     * @param app_token
     * @param session_token
     * @param project_id
     * @param issue_name
     * @param issue_desc
     * @param issue_priority
     * @param issue_time_due
     * @return Issue
     * @throws SQLException
     */
    @GetMapping("/issue-set/{app_token}/{session_token}/{project_id}/{issue_name}/{issue_desc}" +
            "/{issue_priority}/{issue_time_due}")
    public Issue issue_set(@PathVariable String app_token, @PathVariable String session_token,
                     @PathVariable int project_id, @PathVariable String issue_name,@PathVariable String issue_desc,
                     @PathVariable int issue_priority,
                     @PathVariable String issue_time_due) throws SQLException {
        Issue issue = new Issue();
        TrackApiApplication.database.log("Trying to add new issue","ISSUE-SET");
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            issue.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            issue.project_id = project_id;
            issue.issue_name = issue_name;
            issue.issue_desc = issue_desc;
            issue.issue_priority = issue_priority;
            issue.issue_group = 0;
            issue.issue_state = "UNDONE";

            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(issue_time_due, formatter);
                issue.issue_time_due = dateTime;
                issue.database_load();
                TrackApiApplication.database.log("New issue added","ISSUE-SET-SUCCESS");
            }catch(Exception e){
                issue.issue_time_due = null;
                issue.database_load();
                TrackApiApplication.database.log("New issue added without due date","ISSUE-SET-SUCCESS");
            }
        }
        else{
            issue.flag = sv.flag;
        }
        return issue;
    }

    @GetMapping("/issue-remove/{app_token}/{session_token}/{issue_id}")
    public Issue issue_remove(@PathVariable String app_token, @PathVariable String session_token,@PathVariable int issue_id) throws SQLException {
        Issue issue = new Issue();
        issue.issue_id = issue_id;
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            issue.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            issue.remove();
        }
        else{
            issue.flag = sv.flag;
        }
        return issue;
    }

}
