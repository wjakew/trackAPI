/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */

package com.jakubwawak.database;

import com.jakubwawak.administrator.Configuration;
import com.jakubwawak.administrator.RandomString;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class Database_Connector {

    public final int SESSION_TIME = 15;
    // version of database
    public final String version = "v0.0.4";
    public final String database_version = "100";
    public LocalDateTime run_time;
    // header for logging data
    // connection object for maintaing connection to the database
    public Connection con;

    // variable for debug purposes
    final int debug = 1;

    public boolean connected;                      // flag for checking connection to the database
    public String ip;                              // ip data for the connector
    public String database_name;                   // name of the database
    public String database_user;
    String database_password; // user data for cred
    public ArrayList<String> database_log;         // collection for storing data
    private ArrayList<String> database_log_copy;

    public Configuration configuration;            // field for storing configuration data

    public int admin_id;                           // id currently logged admin

    /**
     * Constructor
     */
    public Database_Connector() throws SQLException {
        con = null;
        database_log = new ArrayList<>();
        database_log_copy = new ArrayList<>();
        connected = false;
        ip = "";
        database_name = "";
        database_user = "";
        database_password = "";
        admin_id = -3;
        configuration = null;
        run_time = null;
        log("Started! Database Connector initzialazed","DATABASE");
        log("Session validation time set to: "+SESSION_TIME,"DATABASE");
    }

    /**
     * Function for gathering database log
     * @param log
     * @param code
     */
    public void log(String log,String code) throws SQLException{
        java.util.Date actual_date = new java.util.Date();
        database_log.add("("+actual_date.toString()+")"+"- "+code+" - "+log);
        database_log_copy.add("("+actual_date.toString()+")"+" - "+log);
        // load log to database
        if ( debug == 1){
            String query = "INSERT INTO PROGRAM_LOG (program_log_desc,program_log_code) VALUES (?,?); ";
            System.out.println("TRACKAPI LOG: "+database_log.get(database_log.size()-1));
            if ( con == null){
                System.out.println("DATABASE: con=null ("+log+")");
            }
            else{
                PreparedStatement ppst = con.prepareStatement(query);

                try{

                    ppst.setString(1,log);
                    ppst.setString(2,code);
                    ppst.execute();

                }catch(SQLException e){}
            }

            // after 100 records dump to file
            if(database_log.size() > 100){
                database_log.clear();
            }
        }
    }

    /**
     * Function for connecting to database
     * @param ip
     * @param database_name
     * @param user
     * @param password
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void connect(String ip,String database_name,String user,String password) throws SQLException, ClassNotFoundException{
        this.ip = ip;
        this.database_name = database_name;
        database_user = user;
        database_password = password;

        String login_data = "jdbc:mysql://"+this.ip+"/"+database_name+"?"
                + "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&" +
                "user="+database_user+"&password="+database_password;
        try{
            con = DriverManager.getConnection(login_data);
            connected = true;
            run_time = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
            log("Connected succesfully","CONNECTION");
            log(login_data.substring(0,login_data.length()-25)+"...*END*","CONNECTION");
        }catch(SQLException e){
            connected = false;
            log("Failed to connect to database ("+e.toString()+")","ERROR-DB01");
        }
        log("Database string: "+login_data.substring(0,login_data.length()-25)+"...*END*","ERROR-DB02");
    }

    /**
     * Function for getting user_id by current session
     * @param session_token
     * @return modes
     * @throws SQLException
     * return codes:
     * -5 user not found
     * -6 database error
     */
    public int get_userid_bysession(String session_token) throws SQLException {
        String query = "SELECT user_id FROM SESSION_TOKEN WHERE session_token = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,session_token);

            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                return rs.getInt("user_id");
            }
            return -5;
        } catch (SQLException e) {
            log("Failed to get user_id by given session ("+e.toString()+")","SESSION-USERID-ERROR");
            return -6;
        }
    }

    /**
     * Function for checking if user_id exists in the database
     * @param user_id
     * @return
     */
    boolean check_userid(int user_id) throws SQLException {
        String query = "SELECT user_id FROM USER_DATA WHERE user_id=?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,user_id);

            ResultSet rs = ppst.executeQuery();

            if (rs.next())
                return true;
            return false;
        }catch(SQLException e){
            log("Failed to check user_id ("+e.toString()+")","USERID-CHECK-ERROR");
            return false;
        }
    }

    /**
     * Function for creating session
     * @param user_id
     * @return String
     */
    public String create_session(int user_id) throws SQLException {
        LocalDateTime lt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        String query = "INSERT INTO SESSION_TOKEN (user_id,session_token,session_token_time) VALUES (?,?,?);";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            RandomString session = new RandomString(15);

            if ( check_userid(user_id)){
                ppst.setInt(1,user_id);
                ppst.setString(2,session.buf);
                lt = lt.plusMinutes(SESSION_TIME);
                log("Created new session: |"+session.buf+"| for user_id "+user_id,"SESSION-CRT");
                ppst.setObject(3,lt);

                ppst.execute();
                log("Session expires at "+lt.toString(),"SESSION-CRT");
                return session.buf;
            }
            else{
                log("user_id not found. Cannot create session","SESSION-CRT-NOUSER");
                return null;
            }

        } catch (SQLException e) {
            log("Failed to create session! user_id "+user_id+ " ("+e.toString()+")","SESSION-ERR");
            return null;
        }
    }
    /**
     * Function for showing current user sessions
     * @return ArrayList
     */
    public ArrayList<String> list_current_sessions() throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM SESSION_TOKEN;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add("user:"+rs.getInt("user_id")+" - "+rs.getString("session_token")
                        + " - expires at:"+rs.getObject("session_token_time",LocalDateTime.class).toString());
            }
        } catch (SQLException e) {
            log("Failed to list current sessions ("+e.toString()+")","SESSION-ERROR");
        }
        if ( data.size() == 0)
            data.add("Empty");
        return data;
    }

    /**
     * Function for removing all active sessions
     * @return int
     */
    public void remove_current_sessions() throws SQLException {
        String query = "DELETE FROM SESSION_TOKEN;";
        try{
            log("Trying to remove ALL active sessions","SESSION-RM-ALL");
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.execute();
            log("Removed ALL active sessions","SESSION-RM-ALL");
        }catch(SQLException e){
            log("Failed to remove all active sessions ("+e.toString()+")","SESSION-RM-ERROR");
        }
    }

    /**
     * Function for removing user_session
     * @param user_id
     * @return Integer
     */
    public int remove_session(int user_id) throws SQLException {
        log("Checking and removing user session..","SESSION-RM");
        String query = "DELETE FROM SESSION_TOKEN WHERE user_id=?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setInt(1,user_id);

            ppst.execute();
            log("Session was removed for user_id "+user_id,"SESSION-RM");
            return 1;
        }catch(SQLException e){
            log("Failed to remove session ("+e.toString()+")","SESSION-ERR");
            return -1;
        }
    }

    /**
     * Function for finding user_id by given login
     * @param user_login
     * @return Integer
     */
    int find_user_by_login(String user_login) throws SQLException {
        String query = "SELECT user_id FROM USER_DATA where user_login = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,user_login);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return rs.getInt("user_id");
            }
            return 0;

        } catch (SQLException e) {
            log("Failed to fin user by login ("+e.toString()+")","USER-FIND-ERROR");
            return -1;
        }
    }

    /**
     * Funciton for removing user session
     */
    public int remove_session(String login) throws SQLException {
        log("Checking and removing user session..","SESSION-RM");
        String query = "DELETE FROM SESSION_TOKEN WHERE user_id=?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            int user_id = this.find_user_by_login(login);
            ppst.setInt(1,user_id);
            ppst.execute();
            return 1;
        }catch(SQLException e){
            log("Failed to remove session ("+e.toString()+")","SESSION-ERR");
            return -1;
        }
    }

    /**
    * Function for creating user logins
     */
    public String login_generator(String user_name,String user_surname) throws SQLException {
        String base = user_name + "_" + user_surname.substring(0,1);
        String query = "SELECT user_login in USER_DATA where user_login = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setString(1,base);

            ResultSet rs = ppst.executeQuery();

            int index = 1;
            while(rs.next()){
                if ( rs.getString("user_login").equals(base)){
                    base = base + Integer.toString(index);
                    index++;
                }
            }
            return base;
        } catch (SQLException e) {
            log("Failed to generate login ("+e.toString()+")","ERROR-LOGINGENERATIION");
            return null;
        }
    }


}
