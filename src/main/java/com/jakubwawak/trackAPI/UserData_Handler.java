package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.TokenCheck;
import com.jakubwawak.users.User_Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
        if (tc.check() == 1){
            ud.load_data(user_id);
        }
        else{
            ud.user_id = -4;
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
     */
    @GetMapping("/login/{token}/{user_login}/{user_password}")
    public User_Data login(@PathVariable String token,@PathVariable String user_login,@PathVariable String user_password) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        User_Data ud = new User_Data();
        if ( tc.check() == 1){
            ud.login(user_login,user_password);
        }
        else{
            ud.user_id = -6;
        }
        return ud;
    }

}
