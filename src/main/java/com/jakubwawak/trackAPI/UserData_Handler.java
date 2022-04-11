/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.administrator.TokenCheck;
import com.jakubwawak.database.Database_2FactorAuth;
import com.jakubwawak.users.User_Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.Track;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@RestController
public class UserData_Handler {

    /**
     * Function for getting user data by given id
     * @param token
     * @param user_id
     * @return User_Data
     * @throws SQLException
     */
    @GetMapping("/user/{token}/{user_id}")
    public User_Data get_user(@PathVariable String token,@PathVariable int user_id) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: USER","JOB-GOT");
        if (tc.check() == 1){
            ud.load_data(user_id);
        }
        else{
            ud.user_id = -5;
        }
        return ud;
    }

    @GetMapping("/user-check/{app_token}/{session_token}/{user_login}")
    public User_Data check_user_login(@PathVariable String app_token,@PathVariable String session_token,
                                      @PathVariable String user_login) throws SQLException {
        User_Data user = new User_Data();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: USER-CHECK","JOB-GOT");
        if (sv.connector_validation(app_token)){
            user.get_userid_by_login(user_login);
        }
        else{
            user.user_id = sv.flag;
        }
        return user;
    }

    /**
     * Function for login in user
     * @param token
     * @param user_login
     * @param user_password
     * @return User_Data
     * @throws SQLException
     * return codes:
     * user_id:
     * -6 - app token is wrong
     * -1 - user not found
     * -5 - user not found
     */
    @GetMapping("/login/{token}/{user_login}/{user_password}")
    public User_Data login(@PathVariable String token,@PathVariable String user_login,@PathVariable String user_password) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: LOGIN","JOB-GOT");
        if ( tc.check() == 1){
            ud.login(user_login,user_password);
        }
        else{
            ud.user_id = -11;
        }
        return ud;
    }

    /**
     * Function for login in user
     * @param token
     * @param user_login
     * @param user_password
     * @return User_Data
     * @throws SQLException
     * return codes:
     * user_id:
     * -6 - app token is wrong
     * -69 - 2fa enabled on account
     * -1 - user not found
     * -5 - user not found
     */
    @GetMapping("/n-login/{token}/{user_login}/{user_password}")
    public User_Data new_login(@PathVariable String token,@PathVariable String user_login,@PathVariable String user_password) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: N-LOGIN","JOB-GOT");
        if ( tc.check() == 1){
            ud.user_id = TrackApiApplication.database.get_userid_bylogin(user_login);
            ud.user_login = user_login;
            ud.user_password = user_password;
            ud.check_password_fromuser_login();
            TrackApiApplication.database.log("Checking 2fa settings for user "+user_login,"2FA-START");
            if ( ud.user_id == 77 ){
                // 2fa check
                ud.user_id = TrackApiApplication.database.get_userid_bylogin(user_login);
                Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
                TrackApiApplication.database.log("Check for "+ud.user_id+" returned: "+d2fa.check_2fa_enabled(ud.user_id),"2FA-START");
                if(d2fa.check_2fa_enabled(ud.user_id) == 1){
                    d2fa.roll_2fa(ud.user_id);
                    TrackApiApplication.database.log("Sending 2fa code to authorize...","2FA-START");
                    ud.user_id = -69;
                    TrackApiApplication.database.log("Login procedure stopped. 2FA enabled","2FA-FINISH");
                }
                else{
                    TrackApiApplication.database.log("Login without 2fa authorization","2FA-FINISH");
                    ud.login(user_login,user_password);
                }
            }
        }
        else{
            ud.user_id = -11;
        }
        return ud;
    }


    /**
     * Function for registering user
     * @param token
     * @param user_name
     * @param user_surname
     * @param user_email
     * @return
     * @throws SQLException
     */
    @GetMapping("/register/{token}/{user_name}/{user_surname}/{user_email}")
    public User_Data register(@PathVariable String token,@PathVariable String user_name,@PathVariable String user_surname,@PathVariable String user_email) throws SQLException, NoSuchAlgorithmException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: REGISTER","JOB-GOT");
        if(tc.check() == 1){
            ud.user_name = user_name;
            ud.user_surname = user_surname;
            ud.user_email = user_email;
            ud.user_category = "DEVELOPER";
            ud.register();
            if ( ud.user_password.equals("")){
                ud = new User_Data();
                ud.user_id = -6;
            }
            int user_id = TrackApiApplication.database.get_userid_bylogin(ud.user_login);
            if ( user_id > 0 ){

            }
        }
        else{
            ud.user_id = -11;
        }
        return ud;
    }

    @GetMapping("/password-reset/{token}/{user_email}")
    public User_Data reset_password(@PathVariable String user_email,@PathVariable String token) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: PASSWORD-RESET","JOB-GOT");
        if(tc.check() == 1){
            ud.user_email = user_email;
            ud.reset_password();
        }
        else{
            ud.user_id = -11;
        }
        return ud;
    }

    @GetMapping("/password-reset/{app_token}/{session_token}/{current}/{new_password}")
    public User_Data password_reset(@PathVariable String app_token, @PathVariable String session_token,
                                    @PathVariable String current,@PathVariable String new_password) throws SQLException {
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("Request for changing password from session |"+session_token+"|","PASSWORD-CHANGE");
        User_Data ud = new User_Data();
        TrackApiApplication.database.log("NEW JOB: PASSWORD-RESET","JOB-GOT");
        if (sv.connector_validation(app_token)) {
            int user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            ud.user_id = user_id;
            ud.user_password = current;
            ud.user_session = session_token;
            ud.check_password();
            if ( ud.user_id != -5){
                ud.user_password = new_password;
                ud.set_password();
                TrackApiApplication.database.log("Request successful. Password changed","PASSWORD-CHANGE-SUCCESS");
            }
            else{
                TrackApiApplication.database.log("Request failed. Password validation error","PASSWORD-CHANGE-VALIDATION");
            }
        }
        else{
            ud.user_id = -11;
        }

        return ud;
    }

    @GetMapping ("/password-check/{app_token}/{session_token}/{password}")
    public User_Data check_password(@PathVariable String app_token, @PathVariable String session_token,@PathVariable String password) throws SQLException {
        User_Data ud = new User_Data();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: PASSWORD-CHECK","JOB-GOT");
        if(sv.connector_validation(app_token)){
            ud.user_session = session_token;
            ud.user_password = password;
            ud.check_password();
        }
        else{
            ud.user_id = sv.flag;
        }
        return ud;
    }

    @GetMapping ("/user-email-set/{app_token}/{session_token}/{email_value}")
    public User_Data set_email(@PathVariable String app_token,@PathVariable String session_token,@PathVariable String email_value) throws SQLException {
        User_Data ud = new User_Data();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: USER-EMAIL-SET","JOB-GOT");
        if (sv.connector_validation(app_token)){
            ud.update_email(TrackApiApplication.database.get_userid_bysession(session_token),email_value);
        }
        else{
            ud.flag = sv.flag;
        }
        return ud;
    }
}
