package com.jakubwawak.database;

import com.jakubwawak.maintanance.Viewer;
import com.jakubwawak.snippet_handlers.User_Snippet;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.apache.catalina.User;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *Object for maintaining database connection to the snippets
 */
public class Database_Snippet {

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

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Snippet(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for adding snippet to database
     * @param to_add
     */
    public int add_snippet(User_Snippet to_add) throws SQLException {
        String query = "INSERT INTO USER_SNIPPET(user_snippet_time,user_id,user_snippet_title,user_snippet_content)\n"
                +"VALUES\n"+
                "(?,?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setObject(1,to_add.user_snippet_time);
            ppst.setInt(2,to_add.user_id);
            ppst.setString(3, to_add.user_snippet_title);
            ppst.setString(4,to_add.user_snippet_content);
            ppst.execute();
            TrackApiApplication.database.log("Added snippet! Title: "+to_add.user_snippet_title,"SNIPPET-ADD");
            return 1;

        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to add snippet ("+e.toString()+")","SNIPPET-ADD-FAILED");
            return -1;
        }
    }

    /**
     * Function for getting user snippet from database
     * @param user_snippet_id
     * @return
     */
    public User_Snippet get_user_snippet(int user_snippet_id) throws SQLException {
        String query = "SELECT * FROM USER_SNIPPET WHERE user_snippet_id=?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_snippet_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                TrackApiApplication.database.log("User snippet loaded from database","USER-SNIPPET-GET");
                return new User_Snippet(rs);
            }
            TrackApiApplication.database.log("User snippet not found, loading blank","USER-SNIPPET-GET-BLANK");
            return new User_Snippet();
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get user snippet ("+e.toString()+")","USER-SNIPPET-GET-FAILED");
            User_Snippet us = new User_Snippet();
            us.flag = -1;
            return us;
        }
    }

    /**
     * Function for loading list of snippet glances
     * @param user_id
     * @return Viewer
     */
    public Viewer load_snippet_glances(int user_id) throws SQLException {
        Viewer viewer = new Viewer();
        String query = "SELECT * FROM USER_SNIPPET where user_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                User_Snippet us = new User_Snippet(rs);
                viewer.view.add(us.get_glance());
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get snippet glances for (user_id:"+user_id+") ("+e.toString()+")","SNIPPET-GLANCES-ERROR");
            viewer.view.add("Error");
        }
        return viewer;
    }

    /**
     * Function for removing user_snippet from database
     * @param user_snippet_id
     * @return Integer
     * @throws SQLException
     */
    public int user_snippet_remove(int user_snippet_id) throws SQLException {
        String query = "DELETE FROM USER_SNIPPET WHERE user_snippet_id=?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_snippet_id);
            ppst.execute();
            TrackApiApplication.database.log("User snippet removed","SNIPPET-REMOVE");
            return 1;
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to remove Snippet ("+e.toString()+")","SNIPPET-REMOVE-FAILED");
            return -1;
        }
    }
}
