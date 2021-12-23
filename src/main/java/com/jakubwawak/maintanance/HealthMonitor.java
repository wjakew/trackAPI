/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Object for getting status data from database
 */
public class HealthMonitor {
    private InetAddress ip;
    public String start_time;
    public String database_status;
    public String database_ip;
    public String service_ip;
    public String version;
    public String build_number;

    /**
     * Constructor
     */
    public HealthMonitor() throws UnknownHostException {
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
        service_ip = ip.getLocalHost().toString();
    }

    /**
     * Function for showing info
     * @return String
     */
    public String info(){
        String data = "trackAPI status:\n";
        try{
            data = data + "start time: "+start_time+"\n";
            data = data + "database status:"+database_status+"\n";
            data = data + "version: "+version+", build number: "+build_number+"\n";
            data = data + "database ip: "+database_ip;
        }catch(NullPointerException e){
            data = data + "no database connected";
        }
        return data;
    }
}
