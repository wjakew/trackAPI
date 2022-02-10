/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.board;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Board_Setters {

    @GetMapping("/board-set/{app_token}/{session_token}/{board_name}/{board_desc}")
    public Board board_set(@PathVariable String app_token, @PathVariable String session_token,@PathVariable String board_name,@PathVariable String board_desc) throws SQLException {
        Board board = new Board();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            board.board_name = board_name;
            board.board_desc = board_desc;
            board.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            board.database_load();
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to set board ("+board_name+"/"+board_desc+")","Board set!");
        }
        else{
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to set board ("+board_name+"/"+board_desc+")","Board failed to set. Wrong validation");
            board.flag = sv.flag;
        }
        return board;
    }

    @GetMapping("/board-remove/{app_token}/{session_token}/{board_id}")
    public Board remove_board(@PathVariable String app_token, @PathVariable String session_token,@PathVariable int board_id) throws SQLException {
        Board board = new Board();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            board.board_id = board_id;
            board.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            board.remove();
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove board ("+board_id+")","Board removed!");
        }else{
            board.flag = sv.flag;
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove board ("+board_id+")","Board failed to remove, wrong validation");
        }
        return board;
    }
}
