/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.administrator.TokenCheck;
import com.jakubwawak.database.Database_2FactorAuth;
import com.jakubwawak.maintanance.Standard_Answer;
import com.jakubwawak.users.User_Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.Track;
import java.sql.SQLException;

@RestController
public class TFactor_Auth {

    @GetMapping("track-2fa-confirm/{user_id}/{confirmation_code}")
    public Standard_Answer user_2fa_confirmation(@PathVariable int user_id, @PathVariable int confirmation_code) throws SQLException {
        TrackApiApplication.database.log("NEW JOB: TRACK-2FA-CONFIRM","JOB-GOT");
        Standard_Answer sa = new Standard_Answer();
        sa.flag = 1;
        Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
        sa.data = Integer.toString(d2fa.confirm_authorization(user_id,confirmation_code));
        return sa;
    }

    @GetMapping("2fa-roll/{app_token}/{session_token}/{user_id}")
    public Standard_Answer roll_2fa_code(@PathVariable String app_token,@PathVariable String session_token, @PathVariable int user_id) throws SQLException {
        TrackApiApplication.database.log("NEW JOB: 2FA-ROLL","JOB-GOT");
        Standard_Answer sa = new Standard_Answer();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)) {
            Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
            int code = d2fa.roll_2fa(TrackApiApplication.database.get_userid_bysession(session_token));
            sa.data = Integer.toString(code);
        } else {
            sa.flag = sv.flag;
        }
        return sa;
    }

    @GetMapping("2fa-auth/{app_token}/{user_id}/{fa_token}")
    public User_Data auth_by_2fa(@PathVariable String app_token, @PathVariable int user_id, @PathVariable int fa_token) throws SQLException {
        TrackApiApplication.database.log("NEW JOB: 2FA-AUTH","JOB-GOT");
        TokenCheck tc = new TokenCheck(app_token);
        User_Data ud = new User_Data();
        if ( tc.check() == 1 ){
            Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
            ud.user_id = d2fa.authorize(user_id,fa_token);
        }
        else{
            ud.user_id = -11;
            ud.flag = tc.check();
        }
        return ud;
    }
}
