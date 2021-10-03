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

}
