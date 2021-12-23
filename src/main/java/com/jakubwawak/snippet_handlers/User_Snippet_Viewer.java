package com.jakubwawak.snippet_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Snippet;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class User_Snippet_Viewer {

    @GetMapping("/snippet-viewer/{app_token}/{session_token}")
    public Viewer get_snippet_view(@PathVariable String app_token, @PathVariable String session_token) throws SQLException {
        Session_Validator sv = new Session_Validator(session_token);
        Viewer viewer = new Viewer();
        TrackApiApplication.database.log("NEW JOB: SNIPPET-VIEWER","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Snippet ds = new Database_Snippet(TrackApiApplication.database);
            viewer = ds.load_snippet_glances(TrackApiApplication.database.get_userid_bysession(session_token));
            viewer.flag = 1;
        }
        else{
            viewer.flag = sv.flag;
        }
        return viewer;
    }
}
