/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.room;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Room;
import com.jakubwawak.database.Database_Room_Message;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for creating endpoints for
 */
@RestController
public class Room_Message_Setter {

    @GetMapping("/room-message-send/{app_token}/{session_token}/{room_id}/{message_content}")
    public Room_Message set_message(@PathVariable String app_token,@PathVariable String session_token,
                                    @PathVariable int room_id, @PathVariable String message_content) throws SQLException {
        Room_Message rm = new Room_Message();
        TrackApiApplication.database.log("NEW JOB: ROOM-MESSAGE-SEND","JOB-GOT");
        rm.room_id = room_id;
        rm.room_message_content = message_content;
        rm.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Room_Message drm = new Database_Room_Message(TrackApiApplication.database);
            rm = drm.send_message(rm);
        }
        else{
            rm.flag = sv.flag;
        }
        return rm;
    }

    @GetMapping("/room-message/{app_token}/{session_token}/{room_message_id}")
    public Room_Message get_messages(@PathVariable String app_token,@PathVariable String session_token
            ,@PathVariable int room_message_id) throws SQLException {
        Room_Message rm = new Room_Message();
        TrackApiApplication.database.log("NEW JOB: ROOM-MESSAGE-GET","JOB-GOT");
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_Room_Message drm = new Database_Room_Message(TrackApiApplication.database);
            rm = drm.get_room_message(room_message_id);
            rm.flag = 1;
        }
        else{
            rm.flag = sv.flag;
        }
        return rm;
    }

    @GetMapping("/room-messages/{app_token}/{session_token}/{room_id}")
    public Viewer room_messages(@PathVariable String app_token, @PathVariable String session_token,
                                @PathVariable int room_id) throws SQLException {
        ArrayList<Room_Message> messages = new ArrayList<>();
        TrackApiApplication.database.log("NEW JOB: ROOM-MESSAGES-LIST","JOB-GOT");
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            Database_Room_Message drm = new Database_Room_Message(TrackApiApplication.database);
            messages = drm.get_room_messages(room_id);
            viewer.view3 = messages;
            viewer.flag = 1;
            viewer.field = dr.get_user_role(room_id,TrackApiApplication.database.get_userid_bysession(session_token));
        }
        else{
            viewer.flag = sv.flag;
        }
        return viewer;
    }
}
