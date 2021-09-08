package com.jakubwawak.administrator;


import com.jakubwawak.trackAPI.TrackApiApplication;

import javax.sound.midi.Track;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Session_Validator {

    public String session_token;
    public LocalDateTime session_due;
    public int validation_flag;

    /**
     * Constructor
     * @param session_token
     */
    public Session_Validator(String session_token){
        this.session_token = session_token;
        this.session_due = null;
        validation_flag = -1;
    }

    /**
     * Blank constructor
     */
    public Session_Validator(){
        this.session_token = null;
        this.session_due = null;
        validation_flag = -1;
    }

    /**
     * Constructor for user_id validation
     * @param user_id
     * @throws SQLException
     */
    public Session_Validator(int user_id) throws SQLException {
        String query = "SELECT session_token FROM SESSION_TOKEN where user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ppst.setInt(1,user_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                session_token = rs.getString("session_token");
                validation_flag = -1;
            }
            else{
                session_token = "";
                validation_flag = -3;
            }
        }catch(SQLException e){
            TrackApiApplication.database.log("Failed to get session token for user_id "+user_id,"ERROR-SESERR01");
        }
    }

    /**
     * Function for delete session
     * @return int
     */
    public int delete_session() throws SQLException {
        String query = "DELETE FROM SESSION_TOKEN WHERE session_token =?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,session_token);

            ppst.executeQuery();
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to delete session. Token: "+session_token+"("+e.toString()+")","ERROR-SESDEL");
            return -1;
        }
    }

    /**
     * Function for validating sessions on
     * @return Integer
     * integer:
     *  1 - session validated
     *  2 - session failed to validate ( need new session login )
     * -1 - validation session error
     */
    public int validate() throws SQLException {
        String query = "SELECT * FROM SESSION_TOKEN WHERE session_token = ?;";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,session_token);
            TrackApiApplication.database.log("Trying to validate session. Token: "+session_token,"VALIDATION-START");
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                TrackApiApplication.database.log("Session validated! Token: "+session_token,"VALIDATION-CORRECT");
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
                LocalDateTime session_time = rs.getObject("session_token_time",LocalDateTime.class);
                if ( now.isBefore(session_time)){
                    session_due = session_time;
                    validation_flag = 1;
                    return 1;
                }
                else{
                    validation_flag = 2;
                    return 2;
                }
            }
            return 0;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to validate session! Token: "+session_token,"ERROR-VALIDATION");
            return -1;
        }

    }
}