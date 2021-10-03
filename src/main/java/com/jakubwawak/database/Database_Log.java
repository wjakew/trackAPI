package com.jakubwawak.database;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            ppst.setInt(5,user_id);

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
}
