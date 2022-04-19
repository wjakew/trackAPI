/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.todo;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.database.Database_ToDo;
import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class ToDo_Handler {

    @GetMapping("/todo-add/{app_token}/{session_token}/{todo_title}/{todo_desc}/{todo_impor}/{todo_colour}")
    public ToDo add_todo(@PathVariable String app_token,@PathVariable String session_token, @PathVariable String todo_title,
                         @PathVariable String todo_desc,@PathVariable int todo_impor,@PathVariable int todo_colour) throws SQLException {
        Database_ToDo dtd = new Database_ToDo(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TODO-ADD","JOB-GOT");
        ToDo object = new ToDo();
        if ( sv.connector_validation(app_token) ){
            object.todo_title = todo_title;
            object.todo_desc = todo_desc;
            object.todo_impor = todo_impor;
            object.todo_colour = todo_colour;
            object.todo_state = 0;
            object.user_id = TrackApiApplication.database.get_userid_bysession(session_token);
            object = dtd.add_todo(object);
            if(object.flag == 1){
                TrackApiApplication.database.log("New user todo added to database","TODO-ADD");
            }
            else{
                TrackApiApplication.database.log("Failed to add todo, check log","TODO-ADD-FAILED");
            }
        }
        else{
            object.flag = sv.flag;
        }
        return object;
    }

    @GetMapping("/todo-list/{app_token}/{session_token}/{mode}")
    public Viewer show_todo(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int mode) throws SQLException {
        Database_ToDo dtd = new Database_ToDo(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TODO-LIST","JOB-GOT");
        Viewer view = new Viewer();
        if ( sv.connector_validation(app_token) ){
            view.view = dtd.glance_todo(TrackApiApplication.database.get_userid_bysession(session_token),mode);
            view.flag = 1;
            TrackApiApplication.database.log("View of todos loaded","TODO-LIST");
        }
        else{
            TrackApiApplication.database.log("Failed to load view of todos","TODO-LIST-FAILED");
            view.flag = 0;
        }
        return view;
    }

    @GetMapping("/todo-state/{app_token}/{session_token}/{todo_id}/{todo_state}")
    public ToDo state_todo(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int todo_id,
                           @PathVariable int todo_state) throws SQLException {
        Database_ToDo dtd = new Database_ToDo(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TODO-STATE","JOB-GOT");
        ToDo object = new ToDo();
        if ( sv.connector_validation(app_token) ){
            object = dtd.get_todo(todo_id,TrackApiApplication.database.get_userid_bysession(session_token));
            object.flag = -1;
            try {
                dtd.set_state(object.todo_id, object.user_id, todo_state);
                object.flag = 1;
                TrackApiApplication.database.log("Todo state updated!","TODO-STATE-UPDATE");
            }catch(SQLException e) {
                TrackApiApplication.database.log("Failed to update todo state ("+e.toString()+")","TODO-STATE-FAILED");
                object.flag = 1;
            }
        }
        else{
            object.flag = sv.flag;
        }
        return object;
    }

    @GetMapping("/todo-remove/{app_token}/{session_token}/{todo_id}")
    public ToDo remove_todo(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int todo_id) throws SQLException {
        Database_ToDo dtd =new Database_ToDo(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        TrackApiApplication.database.log("NEW JOB: TODO-REMOVE","JOB-GOT");
        ToDo object = new ToDo();
        if ( sv.connector_validation(app_token) ){
            try {
                dtd.remove_todo(todo_id, TrackApiApplication.database.get_userid_bysession(session_token));
                object.flag = 1;
            }catch(SQLException e){
                object.flag = -1;
            }
        }
        else{
            object.flag = sv.flag;
        }
        return object;
    }

    @GetMapping("/todo-get/{app_token}/{session_token}/{todo_id}")
    public ToDo get_todo(@PathVariable String app_token,@PathVariable String session_token,@PathVariable int todo_id) throws SQLException {
        Database_ToDo dtd = new Database_ToDo(TrackApiApplication.database);
        Session_Validator sv = new Session_Validator(session_token);
        ToDo object = new ToDo();
        if(sv.connector_validation(app_token)){
            try{
                object = dtd.get_todo(todo_id,TrackApiApplication.database.get_userid_bysession(session_token));
            }catch(SQLException e){
                object.flag = -1;
            }
        }
        else{
            object.flag = sv.flag;
        }
        return object;
    }
}
