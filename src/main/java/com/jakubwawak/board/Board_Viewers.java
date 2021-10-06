/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.board;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Board;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Board_Viewers {

    @GetMapping("/board-viewer/{app_token}/{session_token}")
    public Viewer get_boards(@PathVariable String app_token, @PathVariable String session_token) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            Database_Board db = new Database_Board();
            viewer.view = db.load_board_glances(TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            viewer.sv = sv;
        }
        return viewer;
    }
}
