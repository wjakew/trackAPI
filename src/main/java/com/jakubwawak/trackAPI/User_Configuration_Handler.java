package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.maintanance.User_Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class User_Configuration_Handler {

    @GetMapping("/user-configuration/{app_token}/{session_token}")
    public User_Configuration load_user_configuration(@PathVariable String app_token,@PathVariable String session_token) throws SQLException {
        User_Configuration uc = new User_Configuration();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: USER-CONFIGURATION","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            if ( user_id > 0 ){
                uc.user_id = user_id;
                uc.load_configuration();
            }
        }
        else{
            uc.flag = sv.flag;
        }
        return uc;
    }
}
