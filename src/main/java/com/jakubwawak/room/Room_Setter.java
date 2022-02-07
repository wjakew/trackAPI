package com.jakubwawak.room;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Room;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for creating endpoints for rooms
 */
@RestController
public class Room_Setter {

    @GetMapping("/room-create/{app_token}/{session_token}/{room_name}/{room_desc}")
    public Room create_room(@PathVariable String app_token,@PathVariable String session_token,
                            @PathVariable String room_name, @PathVariable String room_desc) throws SQLException {
        Room room = new Room();
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: ROOM-CREATE","JOB-GOT");
        if ( sv.connector_validation(app_token)){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            room.room_code = dr.create_room(room_name,room_desc,TrackApiApplication.database.get_userid_bysession(session_token));
            TrackApiApplication.database.log("Room created","ROOM-CREATE");
            room.flag = 1;
        }
        else{
            room.flag = sv.flag;
            TrackApiApplication.database.log("No auth - room not created","ROOM-NOAUTH");
            room.room_code = "no_auth";
        }
        return room;
    }

    @GetMapping("/room-viewer/{app_token}/{session_token}")
    public Viewer list_rooms(@PathVariable String app_token, @PathVariable String session_token) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            viewer.view2 = dr.list_rooms(TrackApiApplication.database.get_userid_bysession(session_token));
            viewer.flag = 1;
        }
        else{
            viewer.flag = sv.flag;
        }
        return viewer;
    }

    @GetMapping("/room-remove/{app_token}/{session_token}/{room_id}/{room_password}")
    public Room remove_room(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int room_id,
                            @PathVariable String room_password) throws SQLException {
        Room room = new Room();
        room.room_id = room_id;
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token)){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            int ans = dr.remove_room(room_id,room_password);
            room.flag = ans;
        }
        else{
            room.flag = sv.flag;
        }
        return room;
    }
}
