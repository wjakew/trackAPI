package com.jakubwawak.trackAPI;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthMonitor_Handler {

    @GetMapping("/health")
    public HealthMonitor get_health(){
        return new HealthMonitor();
    }
}
