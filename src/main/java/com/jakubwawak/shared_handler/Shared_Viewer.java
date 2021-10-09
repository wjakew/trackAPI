/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.shared_handler;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Share;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.sql.SQLException;

@RestController
public class Shared_Viewer {

    @GetMapping("/shared-viewer-myshares/{app_token}/{session_token}")
    public Viewer load_myshares(@PathVariable String app_token, @PathVariable String session_token) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Share ds = new Database_Share();
            viewer.view = ds.load_user_shares(TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }


    @GetMapping("/shared-viewer-sharedtome/{app_token}/{session_token}")
    public Viewer load_sharedtome(@PathVariable String app_token,@PathVariable String session_token) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if(sv.connector_validation(app_token)){
            Database_Share ds = new Database_Share();
            viewer.view = ds.load_sharedtouser(TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }
}
