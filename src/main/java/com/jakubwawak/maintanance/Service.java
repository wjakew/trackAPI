/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.database.Database_Log;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Service {

    @GetMapping("/service/{service_tag}/{key}/{value}")
    public Service_Answer service_action(@PathVariable String service_tag,@PathVariable String key,@PathVariable String value) throws SQLException {
        Service_Answer sa = new Service_Answer(service_tag);
        if(sa.flag == 1){
            switch(key){
                case"mail":
                    //sending email to app owner with logs
                    // number of logs given with value
                    // if value == 0 send 50
                    try{
                        int amount = Integer.parseInt(value);
                        Database_Log dl = new Database_Log();
                        String data = dl.load_program_log(amount);
                        MailConnector mc = new MailConnector();
                        mc.send("kubawawak@gmail.com","TRACKAPI LOG REQUEST",data);
                        if (!mc.error) {
                            TrackApiApplication.database.log("Send log to system administrator", "SERVICE-MAIL-SUCCESS");
                            sa.answer = "SERVICE EMAIL SEND";
                        }
                        else{
                            TrackApiApplication.database.log("Failed to send log to system administrator","SERVICE-MAIL-SUCCESS");
                        }
                    }catch(Exception e){
                        TrackApiApplication.database.log("Failed to send mail ("+e.toString()+")","SERVICE-MAIL-ERROR");
                        sa.answer = "SERVICE EMAIL ERROR";
                    }
                    break;
            }
        }
        return sa;
    }
}
