package com.jakubwawak.wrapper;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.administrator.TokenCheck;

import java.sql.SQLException;

public class Wrapper {

    public int user_id;
    public int app_tk;
    public int user_tk;

    public Response response;

    public Wrapper(int user_id,String token) throws SQLException {

        TokenCheck tc = new TokenCheck(token);
        tc.check();
        if ( tc.status.equals("token_exists")){
            app_tk = 1;
        }
        else{
            app_tk = 0;
        }
        Session_Validator sv = new Session_Validator(user_id);
        sv.validate();
        user_tk = sv.validation_flag;
    }
}
