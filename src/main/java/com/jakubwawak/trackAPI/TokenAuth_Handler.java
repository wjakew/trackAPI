/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.TokenCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class TokenAuth_Handler {

    @GetMapping("token-check/{raw_token}")
    public TokenCheck get_authorization(@PathVariable String raw_token) throws SQLException {
        TokenCheck t = new TokenCheck(raw_token);
        t.load();
        return t;
    }
}
