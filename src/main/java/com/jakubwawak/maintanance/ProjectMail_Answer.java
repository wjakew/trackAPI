/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Issue;
import com.jakubwawak.database.Database_Project;
import com.jakubwawak.database.Database_Task;
import com.jakubwawak.project_handlers.Project;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class ProjectMail_Answer {

    @GetMapping("/project-mail-share/{app_token}/{session_token}/{project_id}/{e_mail}")
    public Standard_Answer mail_share_project(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int project_id,
                           @PathVariable String e_mail) throws SQLException {
        Standard_Answer sa = new Standard_Answer();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            if ( e_mail.contains("@") && e_mail.contains(".")){
                Database_Project dp = new Database_Project();
                Project project = dp.get_project(project_id);
                String data = "project_name: "+project.project_name+"\nproject_desc:\n"+project.project_desc+"\n-----\n";
                data = data + "tasks:";
                Database_Task dt = new Database_Task(TrackApiApplication.database);
                ArrayList<String> tasks = dt.get_task_glances(project_id,TrackApiApplication.database.get_userid_bysession(session_token));
                for(String line: tasks){
                    data = data + line + "\n";
                }
                data = data+"\n------\nissues\n";
                Database_Issue di = new Database_Issue();
                ArrayList<String> issues = di.get_all_issues_glances(TrackApiApplication.database.get_userid_bysession(session_token),project_id);
                for(String line: issues){
                    data = data + line + "\n";
                }
                Date date = new Date();
                data = data + "Last updated: "+date.toString()+"\nTrackAPI "+TrackApiApplication.version;
                MailConnector mc = new MailConnector();
                mc.send(e_mail,"Project details: "+project.project_name,data);
                sa.flag = 1;
            }
            else{
                sa.flag = -1;
            }
        }
        else{
            sa.flag = sv.flag;
        }
        return sa;
    }
}
