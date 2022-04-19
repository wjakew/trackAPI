/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.room;

/**
 * Object for storing room member data
 */
public class Room_Member {

    public int room_member_id;
    public int room_id;
    public int user_id;
    public int role;

    /**
     * Constructor
     */
    public Room_Member(){
        room_member_id = -1;
        room_id = -1;
        user_id = -1;
        role = -1;
    }
}
