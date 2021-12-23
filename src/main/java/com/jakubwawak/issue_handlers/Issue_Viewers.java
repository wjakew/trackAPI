/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.issue_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Issue;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Issue_Viewers {

    @GetMapping("/issue-viewer/{app_token}/{session_token}/{mode}")
    public Viewer get_all_issues(@PathVariable String app_token, @PathVariable String session_token,
                                 @PathVariable int mode) throws SQLException {
        Session_Validator sv = new Session_Validator(session_token);
        Viewer viewer = new Viewer();
        TrackApiApplication.database.log("NEW JOB: ISSUE-VIEWER","JOB-GOT");
        if (sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            viewer.view = di.get_all_issues_glances(user_id,mode);
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }

    @GetMapping("/issue-get/{app_token}/{session_token}/{issue_id}")
    public Issue get_issue(@PathVariable String app_token, @PathVariable String session_token,@PathVariable int issue_id) throws SQLException {
        Issue issue = new Issue();
        TrackApiApplication.database.log("Issue get invoked","ISSUE-GET");
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ISSUE-GET","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            issue = di.get_issue(issue_id);
            TrackApiApplication.database.log("Issue loaded","ISSUE-GET-LOAD");
        }
        else{
            issue.flag = sv.flag;
        }
        return issue;
    }

    @GetMapping ("/issue-history/{app_token}/{session_token}/{issue_id}")
    public Viewer get_history(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int issue_id) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ISSUE-HISTORY","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            viewer.view = di.get_issue_history(issue_id);
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }

    @GetMapping("/issue-archive/{app_token}/{session_token}")
    public Viewer get_issue_archive(@PathVariable String app_token,@PathVariable String session_token) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ISSUE-ARCHIVE","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Issue di = new Database_Issue();
            viewer.sv = sv;
            viewer.view = di.get_issue_archive(TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }
}
