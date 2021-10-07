/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.board;

import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Object for representing database Board
 */
public class Board {

    /**
     * flag return codes:
     *  1 - object loaded to database
     * -1 - database error
     * -5 - user not found
     * -6 - board not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */
    public int flag;

    public int board_id;
    public String board_name;
    public int user_id;
    public String board_desc;
    public LocalDateTime board_time;

    /**
     * Constructor
     */
    public Board(){
        flag = 0;
        board_id = -1;
        board_name = "";
        user_id = -1;
        board_desc = "";
        board_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
    }

    /**
     * Constructor with database support
     * @param rs
     */
    public Board(ResultSet rs) throws SQLException {
        board_id = rs.getInt("board_id");
        board_name = rs.getString("board_name");
        user_id = rs.getInt("user_id");
        board_desc = rs.getString("board_desc");
        board_time = rs.getObject("board_time",LocalDateTime.class);
        flag = 1;
    }

    /**
     * Function for getting board_id just by board_name
     */
    public void get_board_id() throws SQLException {
        String query = "SELECT board_id FROM BOARD WHERE board_name = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,board_name);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                board_id = rs.getInt("board_id");
                flag = 1;
                TrackApiApplication.database.log("Loaded board_id","BOARD-GETID-SUCCESS");
            }
            else{
                flag = -1;
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get board id ("+e.toString()+")","BOARD-GETID-FAILED");
        }
    }

    /**
     * Function for loading data to database
     */
    public void database_load() throws SQLException {
        String query = "INSERT INTO BOARD\n" +
                "(board_name,user_id,board_desc,board_time)\n" +
                "VALUES\n" +
                "(?,?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,board_name);
            ppst.setInt(2,user_id);
            ppst.setString(3,board_desc);
            ppst.setObject(4,board_time);

            ppst.execute();
            flag = 1;
            TrackApiApplication.database.log("Board loaded to database","BOARD-LOAD-SUCCESS");
            get_board_id();
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load board to database ("+e.toString(),"BOARD-LOAD-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing board from database
     * @throws SQLException
     */
    public void remove() throws SQLException {
        String query = "DELETE FROM BOARD WHERE board_id = ?;";
        try{
            if ( board_element_remove() == 1){
                TrackApiApplication.database.log("Board elements deleted","BOARDEL-REMOVE-SUCCESS");
            }
            else{
                TrackApiApplication.database.log("WARNING: BOARD ELEMENTS FAILED TO DELETE","BOARDEL-REMOVE-FAILED");
            }
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,board_id);
            ppst.execute();
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove board","BOARD-REMOVE-FAILED");
            flag = -1;
        }
    }

    /**
     * Function for removing board elements from board
     * @return Integer
     * @throws SQLException
     */
    public int board_element_remove() throws SQLException {
        String query = "DELETE FROM BOARD_ELEMENT WHERE board_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,board_id);
            ppst.execute();
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove board elements ("+e.toString()+")","ELEMENTS-REMOVE-FAILED");
            return -1;
        }
    }
}
