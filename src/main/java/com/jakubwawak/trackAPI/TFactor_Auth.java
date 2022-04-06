/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.database.Database_2FactorAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class TFactor_Auth {

    @GetMapping("track-2fa-confirm/{user_id}/{confirmation_code}")
    public int user_2fa_confirmation(@PathVariable int user_id,@PathVariable int confirmation_code) throws SQLException {
        Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
        return d2fa.confirm_authorization(user_id,confirmation_code);
    }
}
