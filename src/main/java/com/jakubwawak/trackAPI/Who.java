/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Object for setting connection data
 */
public class Who {

    /**
     * CREATE TABLE WHO_TABLE
     * (
     *   whotable_id INT AUTO_INCREMENT PRIMARY KEY,
     *   whotable_mac VARCHAR(50),
     *   whotable_ip VARCHAR(50),
     *   whotable_time TIMESTAMP
     * );
     */

    public String whotable_mac;
    public String whotable_ip;
    public String whotable_time;
    public String whotable_milis;

    /**
     * Constructor
     */
    public Who(){
        whotable_mac = "";
        whotable_ip = "";
        whotable_time = "";
        whotable_milis = "0";
    }

    /**
     * Function for loading data to database
     */
    public void load() throws SQLException {
        /**
         * CREATE TABLE WHO_TABLE
         * (
         *   whotable_id INT AUTO_INCREMENT PRIMARY KEY,
         *   whotable_mac VARCHAR(50),
         *   whotable_ip VARCHAR(50),
         *   whotable_time TIMESTAMP
         * );
         */
        String query = "INSERT INTO WHO_TABLE (whotable_mac,whotable_ip,whotable_time) VALUES (?,?,?);";
        try{
            LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
            whotable_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString();
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ZonedDateTime zdt = ldt.atZone(ZoneId.of("Europe/Warsaw"));
            whotable_milis = Long.toString(zdt.toInstant().toEpochMilli());
            ppst.setString(1,whotable_mac);
            ppst.setString(2,whotable_ip);
            ppst.setObject(3,whotable_time);
            TrackApiApplication.database.log("Loaded connection data to who table","WHO-TABLE");
            ppst.execute();
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to add connection data to database ("+e.toString()+")","WHO-LOAD-FAILED");
            whotable_milis = "-1";
        }
    }
}
