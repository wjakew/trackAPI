/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Function for maintaning board data on database
 */
public class Database_Board {

    public Database_Connector database;

    /**
     * Constructor
     */
    public Database_Board(){
        this.database = TrackApiApplication.database;
    }

    /**
     * Function for loading board glances
     * @param user_id
     * @return ArrayList
     */
    public ArrayList<String> load_board_glances(int user_id) throws SQLException {
        String query = "SELECT * FROM BOARD WHERE user_id=?;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(rs.getInt("board_id")+": "+rs.getString("board_name"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load board glances ("+e.toString()+")","BOARD-GLANCES-FAILED");
            data.add("error");
        }
        return data;
    }
}
