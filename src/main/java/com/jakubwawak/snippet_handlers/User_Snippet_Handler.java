package com.jakubwawak.snippet_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Snippet;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class User_Snippet_Handler {

    @GetMapping("/snippet-set/{app_token}/{session_token}/{title}/{content}")
    public User_Snippet load_snippet(@PathVariable String app_token, @PathVariable String session_token,@PathVariable String title,
                                     @PathVariable String content) throws SQLException {
        TrackApiApplication.database.log("Got snippet to add!","JOB-SNIPPET-ADD");
        User_Snippet us = new User_Snippet();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            us.user_snippet_title = title;
            us.user_snippet_content = content;
            us.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            us.database_load();
            TrackApiApplication.database.log("Snippet added to database","JOB-SNIPPET-ADD-SUCCESS");
        }
        else{
            us.flag = sv.flag;
        }
        return us;
    }

    @GetMapping("/snippet-get/{app_token}/{session_token}/{user_snippet_id}")
    public User_Snippet get_snippet(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int user_snippet_id) throws SQLException {
        TrackApiApplication.database.log("Got job for getting snippet data","JOB-SNIPPET-GET");
        User_Snippet us = new User_Snippet();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Snippet ds = new Database_Snippet(TrackApiApplication.database);
            us = ds.get_user_snippet(user_snippet_id);
            us.flag = 1;
        }
        else{
            us.flag = sv.flag;
        }
        return us;
    }

    @GetMapping("/snippet-remove/{app_token}/{session_token}/{user_snippet_id}")
    public User_Snippet remove_snippet(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int user_snippet_id) throws SQLException {
        TrackApiApplication.database.log("Got job for snippet removal","JOB-SNIPPET-REMOVE");
        User_Snippet us = new User_Snippet();
        us.user_snippet_id = user_snippet_id;
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            Database_Snippet ds = new Database_Snippet(TrackApiApplication.database);
            if ( ds.user_snippet_remove(user_snippet_id) == 1 ){
                us.flag = 1;
            }
            us.flag = 0;
        }
        else{
            us.flag = sv.flag;
        }
        return us;
    }

}
