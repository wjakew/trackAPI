/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.track_setters;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Project;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class Project_Viewers {

    @GetMapping("/project-viewer/{app_token}/{session_token}")
    public Viewer get_all_projects(@PathVariable String app_token,@PathVariable String session_token) throws SQLException {
        TrackApiApplication.database.log("Loading glances of projects for user","PROJECT-VIEWER-GLANCES");
        ArrayList<String> data = new ArrayList<>();
        Session_Validator sv = new Session_Validator(session_token);
        Viewer viewer = new Viewer();
        viewer.sv = sv;
        if ( sv.connector_validation(app_token)){
            Database_Project dp = new Database_Project();
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            data = dp.get_all_project_glances(user_id);

            if(data.size() == 0){
                data.add("Empty");
            }
            viewer.view = data;
            TrackApiApplication.database.log("Glances loaded","PROJECT-VIEWER-GLANCES");
        }
        else{
            TrackApiApplication.database.log("Glances falied to load. Wrong validation","PROJECT-VIEWER-ERROR");
            data.add("error");
        }
        return viewer;
    }
}
