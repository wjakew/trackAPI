package com.jakubwawak.room;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_Room;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.format.datetime.DateTimeFormatAnnotationFormatterFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.View;
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
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to set room ("+room_name+")","Room added!");
            room.flag = 1;
        }
        else{
            room.flag = sv.flag;
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to set room ("+room_name+")","Failed to add room - failed validating!");
            TrackApiApplication.database.log("No auth - room not created","ROOM-NOAUTH");
            room.room_code = "no_auth";
        }
        return room;
    }

    @GetMapping("/room-data/{app_token}/{session_token}/{room_id}")
    public Viewer get_room_data(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int room_id) throws SQLException {
        Viewer viewer = new Viewer();
        Session_Validator sv = new Session_Validator(session_token);
        if (sv.connector_validation(app_token)){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            viewer.view.add(dr.get_room_data(room_id,TrackApiApplication.database.get_userid_bysession(session_token)));
            viewer.flag = 1;
        }
        else{
            viewer.flag = sv.flag;
        }
        return viewer;
    }

    @GetMapping("/room-addmember/{app_token}/{session_token}/{room_id}/{user_id}/{role}")
    public Room add_member(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int room_id,
                           @PathVariable int user_id,@PathVariable int role) throws SQLException {
        Room room = new Room();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            if ( dr.create_room_member(room_id,user_id,role,
                    TrackApiApplication.database.get_userid_bysession(session_token)) == 1){
                TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                        session_token,"Trying to add new room member (room_id:"+room_id+")","Added new member (user_id:"+user_id+") to room");
                room.flag = 1;
            }
            else{
                TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                        session_token,"Trying to add new room member (room_id:"+room_id+")","Failed to add room member to room, user is not admin.");
                room.flag = -2;
            }
        }
        else{
            room.flag = sv.flag;
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to add new room member (room_id:"+room_id+")","Failed to add room member - wrong validation.");
        }
        return room;
    }

    @GetMapping("/room-removemember/{app_token}/{session_token}/{room_id}/{user_id}")
    public Room remove_member(@PathVariable String app_token,@PathVariable String session_token,
                              @PathVariable int room_id, @PathVariable int user_id) throws SQLException {
        Room room = new Room();
        Session_Validator sv = new Session_Validator(session_token);
        if ( sv.connector_validation(app_token) ){
            Database_Room dr = new Database_Room(TrackApiApplication.database);
            if ( dr.remove_room_member(room_id,user_id) == 1){
                room.flag = 1;
                TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                        session_token,"Trying to remove room member(room_id:"+room_id+")","Room member (user_id: "+user_id+") removed!");
            }
            else{
                room.flag = -2;
                TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                        session_token,"Trying to remove room member (room_id:"+room_id+")","Failed to remove room, user is not admin");
            }

        }
        else{
            room.flag = sv.flag;
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove room member (room_id:"+room_id+")","Failed to remove room, validation error.");
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
            for(Room r : viewer.view2){
                viewer.view.add(dr.get_room_members(r.room_id));
            }
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
            room.flag = dr.remove_room(room_id,room_password);
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove room (room_id:"+room_id+")","Room removed!");
        }
        else{
            room.flag = sv.flag;
            TrackApiApplication.database.connection_logger(TrackApiApplication.database.get_userid_bysession(session_token),
                    session_token,"Trying to remove room (room_id:"+room_id+")","Failed to remove room - wrong validation");
        }
        return room;
    }
}
