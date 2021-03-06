/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;


import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Session_Validator {

    public String session_token;
    public LocalDateTime session_due;
    public int validation_flag;
    public int flag;

    /**
     * Constructor
     * @param session_token
     */
    public Session_Validator(String session_token) throws SQLException {
        this.session_token = session_token;
        this.session_due = null;
        validation_flag = validate();
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
     * Function for validating connection
     * @param app_token
     * @return boolean
     */
    public boolean connector_validation(String app_token) throws SQLException {
        TokenCheck tc = new TokenCheck(app_token);
        if ( tc.check() == 1){
            TrackApiApplication.database.log("Apptoken validation status: "+validation_flag,"SESSION-FLAG-STATUS");
            if ( validation_flag == 1){
                TrackApiApplication.database.log("------Connection validation successful for "+session_token+"/"+app_token,"CONNECT-VALIDATION",session_token);
                return true;
            }
            else{
                TrackApiApplication.database.log("------Connection validation failed for "+session_token+"/"+app_token,"CONNECT-VALIDATION-FAILED",session_token);
                flag = -99;
                return false;
            }
        }
        TrackApiApplication.database.log("------Connection validation failed for "+session_token+"/"+app_token,"CONNECT-VALIDATION-TOKEN",session_token);
        flag = -11;
        return false;
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

        TrackApiApplication.database.log("CONNECTION VALIDATION","VALIDATION");
        TrackApiApplication.database.log("App connection","VALIDATION");
        String query = "SELECT * FROM SESSION_TOKEN WHERE session_token = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,session_token);
            TrackApiApplication.database.log("Trying to validate session. Token: "+session_token,"VALIDATION-START");
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                TrackApiApplication.database.log("Session found. Checking data","VALIDATION-CHECK");
                LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));

                LocalDateTime session_time = null;

                if ( TrackApiApplication.configuration.database_mode.equals("server")) {
                     session_time = rs.getObject("session_token_time", LocalDateTime.class);
                }
                else if (TrackApiApplication.configuration.database_mode.equals("file")){
                    TrackApiApplication.database.log("Loaded from database (raw time): "+rs.getString("session_token_time"),"VALIDATION-CHECK");
                    String time = rs.getString("session_token_time");
                    try{
                        String elements [] = time.split("T");
                        int year = Integer.parseInt(elements[0].split("-")[0]);
                        int month_number = Integer.parseInt(elements[0].split("-")[1]);
                        Month month = Month.of(month_number);
                        int day = Integer.parseInt(elements[0].split("-")[2]);
                        int hour = Integer.parseInt(elements[1].split(":")[0]);
                        int minutes = Integer.parseInt(elements[1].split(":")[1]);
                        session_time = LocalDateTime.of(year,month,day,hour,minutes);
                        TrackApiApplication.database.log("Parsed session time: "+session_time.toString(),"SESSION-TIME");
                    }catch(Exception e){
                        TrackApiApplication.database.log("Error parsing session time ("+e.toString()+")","SESSION-TIME-FAILED");
                    }
                }

                if ( session_time != null ){
                    TrackApiApplication.database.log("Session expire time: "+session_time.toString(),"VALIDATION-CHECK");

                    if ( now.isBefore(session_time)){
                        TrackApiApplication.database.log("Session validated! Token: "+session_token,"VALIDATION-CORRECT");
                        session_due = session_time;
                        validation_flag = 1;
                        return 1;
                    }
                    else{
                        TrackApiApplication.database.log("Session token expired!","VALIDATION-EXPIRED");
                        validation_flag = 2;
                        return 2;
                    }
                }

            }
            return 0;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to validate session! Token: "+session_token,"ERROR-VALIDATION");
            return -1;
        }
    }
}
