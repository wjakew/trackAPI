/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.shared_handler;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.trackAPI.TrackApiApplication;
import com.jakubwawak.users.User_Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Shared_Handler {

    @GetMapping("/shared-set/{app_token}/{session_token}/{project_id}/{user_login}")
    public Shared share_set(@PathVariable String app_token, @PathVariable String session_token
            ,@PathVariable int project_id, @PathVariable String user_login) throws SQLException {
        Shared shared = new Shared();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: SHARED-SET","JOB-GOT");
        if (sv.connector_validation(app_token)){
            User_Data user = new User_Data();
            user.user_login = user_login;
            user.get_userid_by_login(user_login);
            shared.project_id = project_id;
            shared.user_id = user.user_id;
            shared.database_load();
            if(shared.flag == 1)
                TrackApiApplication.database.log("Shared to "+user_login+" project_id: "+project_id+" session("+session_token+")","SHARE-SET-SUCCESS");
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to share project ("+project_id+")","Project shared with (user_id:"+user_login+")!");
        }else{
            shared.flag = sv.flag;
        }
        return shared;
    }

    @GetMapping("/shared-remove/{app_token}/{session_token}/{project_id}")
    public Shared share_remove(@PathVariable String app_token,@PathVariable String session_token,
                               @PathVariable int project_id) throws SQLException {
        Shared shared = new Shared();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: SHARED-REMOVE","JOB-GOT");
        if (sv.connector_validation(app_token)){
            shared.project_id = project_id;
            shared.remove();
            if ( shared.flag == 1)
                TrackApiApplication.database.log("Removed share of project_id "+project_id,"SHARE-REMOVE-SUCCESS");
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove share on project ("+project_id+")","Removed sharing on project!");
        }
        else{
            shared.flag = sv.flag;
        }
        return shared;
    }
}
