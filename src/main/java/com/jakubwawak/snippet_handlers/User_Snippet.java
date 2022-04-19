/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.snippet_handlers;

import com.jakubwawak.database.Database_Snippet;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Object for storing data of user snippets
 */
public class User_Snippet {
    /**
     * CREATE TABLE USER_SNIPPET
     * (
     *     user_snippet_id INT PRIMARY KEY AUTO_INCREMENT,
     *     user_snippet_time TIMESTAMP,
     *     user_id INT,
     *     user_snippet_title VARCHAR(250),
     *     user_snippet_content TEXT,
     *
     *     CONSTRAINT fk_usersnippet FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
     * );
     */

    /**
     * flag return codes:
     *  0 - nothing done
     *  1 - object loaded to database
     * -1 - database error
     * -2 - user not owner
     * -5 - user not found
     * -6 - issue not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */
    public int flag;
    public int user_snippet_id;
    public LocalDateTime user_snippet_time;
    public int user_id;
    public String user_snippet_title;
    public String user_snippet_content;

    /**
     * Constructor
     */
    public User_Snippet(){
        flag = 0;
        user_snippet_id = -1;
        user_snippet_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        user_id = -1;
        user_snippet_title = "blank";
        user_snippet_content = "blank";
    }

    /**
     * Constructor with database usage
     * @param to_add
     */
    public User_Snippet(ResultSet to_add) throws SQLException {
        user_snippet_id = to_add.getInt("user_snippet_id");
        user_snippet_time = to_add.getObject("user_snippet_time",LocalDateTime.class);
        user_id = to_add.getInt("user_id");
        user_snippet_title = to_add.getString("user_snippet_title");
        user_snippet_content = to_add.getString("user_snippet_content");
        TrackApiApplication.database.log("Loaded snippet, user_snippet_id:"+user_snippet_id,"USER-SNIPPET");
        flag = 1;
    }

    /**
     * Function fro getting object glances
     * @return String
     */
    public String get_glance(){
        return user_snippet_id+":"+user_snippet_title+"\n"+user_snippet_content;
    }

    /**
     * Function for adding object to database
     */
    public void database_load() throws SQLException {
        Database_Snippet ds = new Database_Snippet(TrackApiApplication.database);
        try{
            ds.add_snippet(this);
            flag = 1;
            TrackApiApplication.database.log("Added snippet to database!","USER-SNIPPET-LOAD");
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load snippet to database ("+e.toString()+")","USER-SNIPPET-LOAD-FAILED");
            flag = -1;
        }
    }
}
