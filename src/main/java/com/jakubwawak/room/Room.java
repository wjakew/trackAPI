/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.room;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing Room object data
 */
public class Room {

    /**
     * flag return codes:
     *  0 - nothing done
     *  1 - object loaded to database
     * -1 - database error
     * -2 - user not owner
     * -5 - user not found
     * -6 - issue not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */
    public int flag;

    public int room_id;
    public String room_name;
    public String room_desc;
    public String room_password;
    public String room_code;

    /**
     * Constructor
     */
    public Room(){
        flag = 0;
        room_id = -1;
        room_name = "";
        room_desc = "";
        room_password ="";
        room_code = "";
    }

    /**
     * Constructor with database support
     * @param rs
     */
    public Room(ResultSet rs) throws SQLException {
        flag = rs.getInt("flag");
        room_id = rs.getInt("room_id");
        room_name = rs.getString("room_name");
        room_desc = rs.getString("room_desc");
        room_password = rs.getString("room_password");
        room_code = rs.getString("room_code");
    }
}
