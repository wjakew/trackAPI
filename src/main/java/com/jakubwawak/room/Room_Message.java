/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.room;
/**
 * Function for storing room message data
 */
public class Room_Message {

    public int room_message_id;
    public String room_message_content;
    public int room_id;
    public int user_id;
    public int ping_id;
    public int content_id;

    /**
     * Constructor
     */
    public Room_Message(){
        room_message_id = -1;
        room_message_content = "";
        room_id = -1;
        user_id = -1;
        ping_id = -1;
        content_id = -1;
    }
}
