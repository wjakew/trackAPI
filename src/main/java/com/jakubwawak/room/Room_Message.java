/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.room;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Function for storing room message data
 */
public class Room_Message {

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -99 - session has expired
     * -11 - invalid app token
     * -88 - database error when checking session token
     */
    public int flag;

    public int room_message_id;
    public String room_message_content;
    public LocalDateTime room_time;
    public int room_id;

    public int user_id;
    public String user_login;

    public int ping_id;
    public int content_id;

    /**
     * Constructor
     */
    public Room_Message(){
        flag = -1;
        room_message_id = -1;
        room_message_content = "empty";
        room_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        room_id = -1;
        user_id = -1;
        ping_id = -1;
        content_id = -1;
        user_login = "";
    }

    /**
     * Constructor with database support
     * @param rs
     */
    public Room_Message(ResultSet rs) throws SQLException {
        room_message_id = rs.getInt("room_message_id");
        room_message_content = rs.getString("room_message_content");
        room_time = rs.getObject("user_login",LocalDateTime.class);
        room_id = rs.getInt("room_id");
        user_id = rs.getInt("user_id");
        ping_id = rs.getInt("ping_id");
        content_id = rs.getInt("content_id");
        user_login = "";
        flag = 1;
    }

    /**
     * Function for getting user login
     */
    public void get_user_login() throws SQLException {
        user_login = TrackApiApplication.database.get_userlogin_byid(user_id);
    }

    /**
     * Function for looking for logins in messages
     * @throws SQLException
     */
    public void look_for_logins() throws SQLException {
        String[] words = room_message_content.split(" ");
        for(String word : words){
            if ( word.contains("@")){
                try{
                    word = word.replaceAll("@","");
                    String[] elements = word.split(":");
                    int check = TrackApiApplication.database.get_userid_bylogin(elements[1]);
                    if ( check > 0 ){
                        ping_id = check;
                        break;
                    }
                }catch(Exception e){
                    int check = TrackApiApplication.database.get_userid_bylogin(word.replaceAll("@",""));
                    if ( check > 0 ){
                        ping_id = check;
                        break;
                    }
                }
            }
        }
    }
}
