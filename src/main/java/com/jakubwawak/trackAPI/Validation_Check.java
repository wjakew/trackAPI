/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.administrator.TokenCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Validation_Check {

    /**
     * Function for checking if user has a valid session
     * @param token
     * @param user_id
     * @return Session_Validator
     * @throws SQLException
     */
    @GetMapping("/session-validation/{token}/{user_id}")
    public Session_Validator check_validation(@PathVariable String token, @PathVariable int user_id) throws SQLException {
        TokenCheck tc = new TokenCheck(token);
        Session_Validator sv;
        if ( tc.check() == 1 ){
            sv = new Session_Validator(user_id);
            sv.validate();
        }
        else{
            sv = new Session_Validator();
        }
        return sv;
    }
}
