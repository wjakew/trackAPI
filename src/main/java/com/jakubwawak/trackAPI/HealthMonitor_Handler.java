/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.maintanance.HealthMonitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;
import java.sql.SQLException;

@RestController
public class HealthMonitor_Handler {

    @GetMapping("/health")
    public HealthMonitor get_health() throws UnknownHostException, SQLException {
        TrackApiApplication.database.log("NEW JOB: HEALTH","JOB-GOT");
        return new HealthMonitor();
    }

    @GetMapping("/who/{macaddress}/{ip}")
    public Who get_who(@PathVariable String macaddress,@PathVariable String ip) throws SQLException {
        TrackApiApplication.database.log("NEW JOB: WHO","JOB-GOT");
        Who who = new Who();
        who.whotable_ip = ip;
        who.whotable_mac = macaddress;
        who.load();
        return who;
    }
}

