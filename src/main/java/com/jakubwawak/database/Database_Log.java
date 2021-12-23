package com.jakubwawak.database;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Database_Log {

    Database_Connector database;

    public Database_Log(){
        database = TrackApiApplication.database;
    }

    /**
     * Function for adding log data to OBJECT_HISTORY
     * @param log
     * @param category
     * @param object_id
     * @param user_id
     */
    public int object_log(String log, String category,int object_id,int user_id) throws SQLException {
        LocalDateTime actual = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        String query = "INSERT INTO OBJECT_HISTORY\n" +
                "(user_id,history_category,history_object_id,history_desc,history_object_time)\n" +
                "VALUES\n" +
                "(?,?,?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,category);
            ppst.setInt(3,object_id);
            ppst.setString(4,log);
            ppst.setObject(5,actual);

            ppst.execute();
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to log object history ("+e.toString()+")","OBJECT-HISTORY-FAILED");
            return -1;
        }
    }

    /**
     * Function for setting user log to database
     * @param log
     * @param code
     * @param user_id
     * @return Integer
     */
    public int user_log(String log,String code,int user_id) throws SQLException {
        String query = "INSERT INTO LOG_HISTORY\n" +
                "(user_id,log_history_code,log_history_desc)\n" +
                "VALUES\n" +
                "(?,?,?);";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setInt(1,user_id);
            ppst.setString(2,code);
            ppst.setString(3,log);

            ppst.execute();
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to log user history ("+e.toString()+")","USER-LOG-ERROR");
            return -1;
        }
    }

    /**
     * Function for loading program log data to list
     * @param size
     * @return ArrayList
     */
    public String load_program_log(int size) throws SQLException {
        String query = "SELECT * FROM PROGRAM_LOG ORDER BY program_log_id DESC LIMIT ?;";
        String data ="Program log data (size = "+size+"):\n";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            if ( size == 0)
                size = 50;
            ppst.setInt(1,size);
            ResultSet rs = ppst.executeQuery();
            while (rs.next()) {
                data = data + rs.getString("program_log_code")+" - "+rs.getString("program_log_desc")+"\n";
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load program log "+e.toString()+")","PROGRAM-LOG-ERROR");
            data = data + "ERROR PARSING: "+e.toString()+"\n";
        }
        return data;
    }
}
