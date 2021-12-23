/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.maintanance.HealthMonitor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
