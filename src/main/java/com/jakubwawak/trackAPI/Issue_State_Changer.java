package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Issue;
import com.jakubwawak.issue_handlers.Issue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Issue_State_Changer {

    @GetMapping("/issue-group/{app_token}/{session_token}/{issue_id}/{issue_group}")
    public Issue change_group(@PathVariable String app_token, @PathVariable String session_token,
                              @PathVariable int issue_id, @PathVariable int issue_group) throws SQLException {
        Issue issue = new Issue();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ISSUE-GROUP","JOB-GOT");
        if (sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            if ( di.update_group(issue_id,issue_group,TrackApiApplication.database.get_userid_bysession(session_token)) == 1){
                issue.flag = 1;
            }
            else{
                issue.flag = -1;
            }
        }
        else{
            issue.flag = sv.flag;
        }
        return issue;
    }

    @GetMapping("/issue-open/{app_token}/{session_token}/{issue_id}")
    public Issue issue_open(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int issue_id) throws SQLException {
        Issue issue = new Issue();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ISSUE-OPEN","JOB-GOT");
        if (sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            issue.flag = di.update_group(issue_id,0,TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            issue.flag = sv.flag;
        }
        return issue;
    }
}
