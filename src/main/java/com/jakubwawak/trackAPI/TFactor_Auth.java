/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_2FactorAuth;
import com.jakubwawak.maintanance.Standard_Answer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class TFactor_Auth {

    @GetMapping("track-2fa-confirm/{user_id}/{confirmation_code}")
    public Standard_Answer user_2fa_confirmation(@PathVariable int user_id, @PathVariable int confirmation_code) throws SQLException {
        Standard_Answer sa = new Standard_Answer();
        sa.flag = 1;
        Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
        sa.data = Integer.toString(d2fa.confirm_authorization(user_id,confirmation_code));
        return sa;
    }

    @GetMapping("2fa-roll/{app_token}/{session_token}/{user_id}")
    public Standard_Answer roll_2fa_code(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int user_id) throws SQLException {
        Standard_Answer sa = new Standard_Answer();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
            int code = d2fa.roll_2fa(TrackApiApplication.database.get_userid_bysession(session_token));
            sa.data = Integer.toString(code);
        }
        else{
            sa.flag = sv.flag;
        }
        return sa;
    }
}
