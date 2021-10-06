/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.board;

import com.jakubwawak.database.Database_Log;
import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.lang.ref.PhantomReference;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing Board_Element data
 */
public class Board_Element {

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -6 - board element not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */
    public int flag;

    public int board_element_id;
    public String board_list_object;
    public int object_id;
    public int board_id;

    /**
     * Constructor
     */
    public Board_Element(){
        board_element_id = -1;
        board_list_object = "";
        object_id = -1;
        board_id = -1;
    }

    /**
     * Constructor with database support
     * @param rs
     */
    public Board_Element(ResultSet rs) throws SQLException {
        board_element_id = rs.getInt("board_element_id");
        board_list_object = rs.getString("board_list_object");
        object_id = rs.getInt("object_id");
        board_id = rs.getInt("board_id");
    }

    /**
     * Function for loading board element to database
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO BOARD_ELEMENT\n" +
                "(board_list_object,object_id,board_id)\n" +
                "VALUES\n" +
                "(?,?,?);";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,board_list_object);
            ppst.setInt(2,object_id);
            ppst.setInt(3,board_id);
            ppst.execute();
            flag = 1;
            TrackApiApplication.database.log("Board element loaded","BOARD-LOAD-SUCCESS");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load board element to database ("+e.toString()+")","BOARDEL-LOAD-FAILED");
        }
    }

    /**
     * Function for removing board_element
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM BOARD_ELEMENT WHERE object_id = ? and board_list_object = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,object_id);
            ppst.setString(2,board_list_object);
            ppst.execute();
            flag = 1;
            TrackApiApplication.database.log("Board element removed","BOARDEL-REMOVE-SUCCESS");
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to remove board element","BOARDEL-REMOVE-FAILED");
            flag = -1;
        }
    }

}
