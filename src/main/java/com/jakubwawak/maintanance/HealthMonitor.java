/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.trackAPI.TrackApiApplication;

/**
 * Object for getting status data from database
 */
public class HealthMonitor {
    public String start_time;
    public String database_status;
    public String database_ip;
    public String version;
    public String build_number;

    /**
     * Constructor
     */
    public HealthMonitor(){
        start_time = TrackApiApplication.database.run_time.toString();
        if ( TrackApiApplication.database.connected ){
            database_status = "connected";
        }
        else{
            database_status = "disconnected";
        }
        database_ip = TrackApiApplication.database.ip;
        version = TrackApiApplication.version;
        build_number = TrackApiApplication.build;
    }
}
