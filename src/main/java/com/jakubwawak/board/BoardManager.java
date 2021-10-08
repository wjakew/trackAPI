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

/**
 * Class for maintaing board elements
 */
@RestController
public class BoardManager {

    /**
     * Function for adding or removing board_elements
     * @param app_token
     * @param session_token
     * @param action
     * @param object
     * @param object_id
     * @param board_id
     * @return Board_Element
     * object:           action
     * 0 - task          0 - remove
     * 1 - issue         1 - add
     */
    @GetMapping("/boardmanager/{app_token}/{session_token}/{action}/{object}/{object_id}/{board_id}")
    public Board_Element load_board_element(@PathVariable String app_token,@PathVariable String session_token,
                                            @PathVariable int action,@PathVariable int object, @PathVariable int object_id,
                                            @PathVariable int board_id) throws SQLException {

        Board_Element be = new Board_Element();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            be.board_id = board_id;
            be.board_element_id = object_id;
            switch(object){
                case 0:
                    be.board_list_object = "TASK";
                    break;
                case 1:
                    be.board_list_object = "ISSUE";
                    break;
            }
            be.object_id = object_id;
            TrackApiApplication.database.log("Trying to add element: "+be.board_list_object+" id: "+be.board_element_id+" to board_id "+be.board_id,"BOARDMANAGER");
            be.database_load();
        }
        else{
            be.flag = sv.flag;
        }
        return be;
    }

    /**
     * Function for removing object from board elements
     * @param app_token
     * @param session_token
     * @param object
     * @param object_id
     * @return Board_Element
     * @throws SQLException
     * object
     * 0 - task
     * 1 - issue
     */
    @GetMapping("/boardmanager-remove/{app_token}/{session_token}/{object}/{object_id}/{board_id}")
    public Board_Element remove_board_element(@PathVariable String app_token,@PathVariable String session_token,
                                                   @PathVariable int object, @PathVariable int object_id,
                                              @PathVariable int board_id) throws SQLException {
        Board_Element be = new Board_Element();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            be.object_id = object_id;
            be.board_id = board_id;
            switch(object){
                case 0:
                    be.board_list_object = "TASK";
                    break;
                case 1:
                    be.board_list_object = "ISSUE";
                    break;
            }
            be.remove();
        }
        else{
            be.flag = sv.flag;
        }
        return be;
    }
}
