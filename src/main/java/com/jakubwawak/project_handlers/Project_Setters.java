/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.project_handlers;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Log;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Project_Setters {

    /**
     * CREATE TABLE PROJECT
     * (
     *   project_id INT PRIMARY KEY AUTO_INCREMENT,
     *   user_id INT,
     *   project_name VARCHAR(250),
     *   project_desc TEXT,
     *   project_creation_date TIMESTAMP,
     *   project_state VARCHAR(100), -- CODES: active, unactive, date ( time to finish )
     *
     *   CONSTRAINT fk_project FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
     * );
     */
    @GetMapping("/project-set/{app_token}/{session_token}/{project_name}/{project_desc}/{project_state}")
    public Project project_set(@PathVariable String app_token,@PathVariable String session_token,@PathVariable String project_name,
                       @PathVariable String project_desc,@PathVariable String project_state) throws SQLException {
        Project project = new Project();
        TrackApiApplication.database.log("User trying to add new project to database","PROJECT-ADD");
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: PROJECT-SET","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            if ( user_id > 0){
                project.user_id = user_id;
                project.project_name = project_name;
                project.project_desc = project_desc;
                project.project_state = project_state;
                project.database_load();
                if ( project.flag == 1){
                    TrackApiApplication.database.log("Project added","PROJECT-ADD-SUCCESSFUL");
                }
                else{
                    TrackApiApplication.database.log("Failed to add project","PROJECT-ADD-ERROR");
                }
            }
            else{
                project.flag= -5;
            }
        }
        return project;
    }

    @GetMapping("/project-remove/{app_token}/{session_token}/{project_id}")
    public Project project_remove(@PathVariable String app_token,@PathVariable String session_token,
                                  @PathVariable int project_id) throws SQLException {
        Project project = new Project();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: PROJECT-REMOVE","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            project.project_id = project_id;
            project.remove();
            Database_Log dl = new Database_Log();
            dl.object_log("Removed project.","PROJECT",project_id,TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            project.project_id = sv.flag;
        }
        return project;
    }

    @GetMapping("/project-update/{app_token}/{session_token}/{project_id}/{code}/{value}")
    public Project project_update(@PathVariable String app_token,@PathVariable String session_token,
                                  @PathVariable int project_id,@PathVariable String code, @PathVariable String value) throws SQLException {
        Project project = new Project();
        TrackApiApplication.database.log("Trying to update project_id "+project_id,"PROJECT-UPDATE");
        project.project_id = project_id;
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: PROJECT-UPDATE","JOB-GOT");
        if ( sv.connector_validation(app_token) ){
            project.update(code,value);
            Database_Log dl = new Database_Log();
            dl.object_log("Updated project. "+code+" set to "+value,"PROJECT",project_id,
                    TrackApiApplication.database.get_userid_bysession(session_token));
        }
        return project;
    }
}
