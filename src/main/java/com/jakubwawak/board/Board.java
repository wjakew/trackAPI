/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.board;

import com.jakubwawak.trackAPI.TrackApiApplication;

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
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.execute();
            flag = 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove board","BOARD-REMOVE-FAILED");
            flag = -1;
        }
    }
}
