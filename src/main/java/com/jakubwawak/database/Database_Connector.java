/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */

package com.jakubwawak.database;

import com.jakubwawak.administrator.Configuration;
import com.jakubwawak.administrator.RandomString;
import com.jakubwawak.maintanance.ConsoleColors;
import com.jakubwawak.trackAPI.TrackApiApplication;
import com.jakubwawak.users.User_Data;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Database_Connector {

    public final int SESSION_TIME = 15;
    // version of database
    public final String version = "v0.0.9";
    public final String database_version = "100";
    public LocalDateTime run_time;
    // header for logging data
    // connection object for maintaing connection to the database
    public Connection con;

    // variable for debug purposes
    public int log_printing = 1;

    public boolean connected;                      // flag for checking connection to the database
    public String ip;                              // ip data for the connector
    public String database_name;                   // name of the database
    public String database_user;
    String database_password; // user data for cred
    public ArrayList<String> database_log;         // collection for storing data
    private ArrayList<String> database_log_copy;

    public Configuration configuration;            // field for storing configuration data

    public int admin_id;                           // id currently logged admin
    public String admin_login;                     // login of currently logged admin

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
        admin_login = "notlogged";
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
        String query = "INSERT INTO PROGRAM_LOG (program_log_desc,program_log_code,program_log_session_token,program_log_time) VALUES (?,?,?,?); ";
        // print log to the screen
        if (log_printing == 1){
            if ( database_log.get(database_log.size()-1).contains("NEW JOB") || database_log.get(database_log.size()-1).contains("HEALTH")){
                System.out.println(ConsoleColors.BLUE_BOLD+"\nTRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else if(database_log.get(database_log.size()-1).contains("VALIDATION")){
                System.out.println(ConsoleColors.YELLOW_BOLD+"TRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else if(database_log.get(database_log.size()-1).contains("2FA")){
                System.out.println(ConsoleColors.GREEN_BOLD+"TRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else if(database_log.get(database_log.size()-1).contains("Failed") ||database_log.get(database_log.size()-1).contains("FAILED")){
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT+"TRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else{
                System.out.println("TRACKAPI LOG: "+database_log.get(database_log.size()-1));
            }
        }
        if ( con == null){
            System.out.println("DATABASE: con=null ("+log+")");
        }
        else{
            PreparedStatement ppst = con.prepareStatement(query);
            try{

                ppst.setString(1,log);
                ppst.setString(2,code);
                ppst.setString(3,"empty");
                ppst.setObject(4,LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
                ppst.execute();

            }catch(SQLException e){}
        }

        // after 100 records dump to file
        if(database_log.size() > 100){
            database_log.clear();
        }

    }

    /**
     * Function for logging data with session_token data
     * @param log
     * @param code
     * @param session_token
     * @throws SQLException
     */
    public void log(String log,String code,String session_token) throws SQLException{
        java.util.Date actual_date = new java.util.Date();
        database_log.add("("+actual_date.toString()+")"+"- "+code+" - "+log);
        database_log_copy.add("("+actual_date.toString()+")"+" - "+log);
        // load log to database
        String query = "INSERT INTO PROGRAM_LOG (program_log_desc,program_log_code,program_log_session_token,program_log_time) VALUES (?,?,?,?); ";

        // print log to the screen
        if (log_printing == 1){
            if ( database_log.get(database_log.size()-1).contains("NEW JOB") || database_log.get(database_log.size()-1).contains("HEALTH")){
                System.out.println(ConsoleColors.BLUE_BOLD+"\nTRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else if(database_log.get(database_log.size()-1).contains("Failed") ||database_log.get(database_log.size()-1).contains("FAILED")){
                System.out.println(ConsoleColors.RED_BOLD_BRIGHT+"TRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else if(database_log.get(database_log.size()-1).contains("VALIDATION")){
                System.out.println(ConsoleColors.YELLOW_BOLD+"TRACKAPI LOG: "+database_log.get(database_log.size()-1)+ConsoleColors.RESET);
            }
            else{
                System.out.println("TRACKAPI LOG: "+database_log.get(database_log.size()-1));
            }
        }

        if ( con == null){
            System.out.println("DATABASE: con=null ("+log+")");
        }
        else{
            PreparedStatement ppst = con.prepareStatement(query);

            try{

                ppst.setString(1,log);
                ppst.setString(2,code);
                ppst.setString(3,session_token);
                ppst.setObject(4,LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
                ppst.execute();

            }catch(SQLException e){}
        }

        // after 100 records dump to file
        if(database_log.size() > 100){
            database_log.clear();
        }
    }

    /**
     * Function for saving user connection data
     * @param user_id
     * @param session_token
     * @param request
     * @param answer
     */
    public void connection_logger(int user_id,String session_token,String request,String answer) throws SQLException {
        LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        /**
         * CREATE TABLE CONNECTION_LOG
         * (
         *     connection_log_id INT AUTO_INCREMENT PRIMARY KEY,
         *     user_id INT,
         *     session_token VARCHAR(20),
         *     connection_time TIMESTAMP,
         *     connection_request TEXT,
         *     connection_answer TEXT,
         *
         *     CONSTRAINT fk_connectionlog FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        String query = "INSERT INTO CONNECTION_LOG(user_id,session_token,connection_time,connection_request,connection_answer)\n" +
                "VALUES\n" +
                "(?,?,?,?,?);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,session_token);
            ppst.setObject(3,ldt);
            ppst.setString(4,request);
            ppst.setString(5,answer);
            ppst.execute();
            log("Added object to connection log!","CON-LOG");
        } catch (SQLException e) {
            log("Failed to add to connection log ("+e.toString()+")","CON-LOG-FAILED");
            log(ldt.toString()+" - user_id: "+user_id+" ("+request+")","CON-LOG-BACKUP");
        }
    }

    /**
     * Function for showing log
     * @param size
     */
    public void show_log(int size){
        System.out.println("Showing log: ");
        if ( size != 0 ){
            int log_size = database_log.size();
            if ( log_size > size ){
                int start = log_size - size;
                for ( int i = start; i < log_size; i++){
                    System.out.println(i+": "+database_log.get(i));
                }
            }
            else{
                System.out.println("Log size smaller than number.");
                for (String line : database_log){
                    System.out.println(line);
                }
            }
        }
        else{
            for (String line : database_log){
                System.out.println(line);
            }
        }
        System.out.println("END OF LOG");
    }

    /**
     * Showing archive log from database
     * @param size
     */
    public void show_all_log(int size) throws SQLException {

        /**
         * CREATE TABLE PROGRAM_LOG
         * (
         *   program_log_id INT AUTO_INCREMENT PRIMARY KEY,
         *   program_log_code VARCHAR(30),
         *   program_log_desc VARCHAR(300),
         *   program_log_session_token VARCHAR(10),
         *   program_log_time TIMESTAMP
         * );
         */
        System.out.println("Showing all log (size:"+size+")");
        if(size != 0 ){
            String query = "SELECT * FROM PROGRAM_LOG ORDER BY program_log_id DESC LIMIT ?;";
            try{
                PreparedStatement ppst = this.con.prepareStatement(query);
                ppst.setInt(1,size);
                ResultSet rs = ppst.executeQuery();
                while(rs.next()){
                    System.out.println(rs.getInt("program_log_id")+"| "
                            +rs.getString("program_log_code")+"|>"+rs.getString("program_log_session_token")+"| "+rs.getString("program_log_desc"));
                }
            }catch(SQLException e){
                log("Failed to load log from database ("+e.toString()+")","LOGARCH-FAILED");
            }
        }
        else{
            System.out.println("END OF LOG.");
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
            log("Removing old/saved sessions from database...","CONNECTION-GARBAGE-COLLECTOR");
            remove_current_sessions();
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
     * Function for getting user login by given user id
     * @param user_login
     * @return Integer
     */
    public int get_userid_bylogin(String user_login) throws SQLException {
        String query = "SELECT user_id FROM USER_DATA WHERE user_login = ?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setString(1,user_login);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                TrackApiApplication.database.log("User found by login","USER-DATA-GET");
                return rs.getInt("user_id");
            }
            return 0;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get user by login ("+e.toString()+")","USER-DATA-GET-FAILED");
            return -1;
        }
    }

    /**
     * Function for getting user login by given id
     * @param user_id
     * @return String
     */
    public String get_userlogin_byid(int user_id) throws SQLException {
        String query = "SELECT user_login FROM USER_DATA WHERE user_id = ?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            if(rs.next()){
                TrackApiApplication.database.log("Login found for given id","USER-DATA-GET");
                return rs.getString("user_login");
            }
            return "blank";
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get user login by given id ("+e.toString()+")","USER-DATA-GET-FAILED");
            return null;
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
                archive_session(session.buf,user_id);
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
     * Function for saving archived session to database
     * @param session_token
     * @param user_id
     * @throws SQLException
     */
    void archive_session(String session_token,int user_id) throws SQLException {
        /**
         * CREATE TABLE SESSION_TOKEN_ARCH
         * (
         *     session_token_archive_id INT AUTO_INCREMENT PRIMARY KEY,
         *     user_id INT,
         *     session_token VARCHAR(20),
         *     session_token_time TIMESTAMP,
         *
         *     CONSTRAINT fk_session_token_arch FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        String query = "INSERT INTO SESSION_TOKEN_ARCH (user_id,session_token,session_token_time) VALUES (?,?,?);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,session_token);
            ppst.setObject(3,LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
            ppst.execute();
            log("Saved archive session_token to database","SESSION-ARCH");
        } catch (SQLException e) {
            log("Failed to save session_token ("+e.toString()+")","SESSION-ARCH-FAILED");
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

    /**
     * Function for listing users
     * @return ArrayList
     * @throws SQLException
     */
    public ArrayList<String> list_users() throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM USER_DATA;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add("id: "+rs.getInt("user_id")+ " login: "+rs.getString("user_login"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to load user list ("+e.toString()+")","USER-LIST-FAILED");
            data.add("error");
        }
        return data;
    }

    /**
     * Function for listing active users
     * @return ArrayList
     */
    public ArrayList<String> list_active_users() throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        /**
         * CREATE TABLE SESSION_TOKEN
         * (
         *   session_token_id INT AUTO_INCREMENT PRIMARY KEY,
         *   user_id INT,
         *   session_token VARCHAR(20),
         *   session_token_time TIMESTAMP,
         *
         *   CONSTRAINT fk_session_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        String query = "SELECT * from SESSION_TOKEN;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                int user_id = rs.getInt("user_id");
                User_Data user = new User_Data();
                user.load_data(user_id);
                data.add("id: "+user_id+" login: "+user.user_login+" session: "+rs.getString("session_token")
                        +" expires: "+rs.getObject("session_token_time",LocalDateTime.class).toString());
            }
            if (data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to get list of active users ("+e.toString()+")","USERAC-LIST-FAILED");
            data.add("error");
        }
        return data;
    }

    /**
     * Function for listing all blocked users
     * @return ArrayList
     */
    public ArrayList<String> list_blocked_users() throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM USER_GRAVEYARD";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add("user_id: "+rs.getInt("user_id")+" since "+rs.getObject("graveyard_date",LocalDateTime.class).toString());
            }
            if ( data.size() == 0 ){
                data.add("Empty");
            }
        }catch(Exception e){
            TrackApiApplication.database.log("Failed to get list of blocked users ("+e.toString()+")","USER-LISTB-FAILED");
        }
        return data;
    }

    /**
     * Function for updating servicetag
     * @param new_servicetag
     * @return Integer
     */
    public int update_servicetag(String new_servicetag) throws SQLException {
        String query = "UPDATE PROGRAMCODES SET programcodes_values = ? where programcodes_key = 'servicetag';";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,new_servicetag);
            ppst.execute();
            TrackApiApplication.database.log("Updated servicetag!","SERVICETAG-UPDATE");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to update servicetag ("+e.toString()+")","SERVICETAG-UPDATE-FAILED");
            return -1;
        }
    }

    /**
     * Function for updating programcodes data
     * @param programcodes_key
     * @param programcodes_values
     * @return Integer
     */
    public int update_programcodes(String programcodes_key,String programcodes_values) throws SQLException {
        String query = "UPDATE PROGRAMCODES SET programcodes_values = ? where programcodes_key = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setString(1,programcodes_values);
            ppst.setString(2,programcodes_key);
            ppst.execute();
            TrackApiApplication.database.log("Updated programcodes value!","SERVICETAG-UPDATE");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to update data ("+e.toString()+")","SERVICETAG-UPDATE-FAILED");
            return -1;
        }
    }

    /**
     * Function for listing apptokens
     * @return ArrayList
     */
    public ArrayList<String> list_apptoken() throws SQLException {
        String query = "SELECT * FROM TOKEN;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add("user_id: "+rs.getInt("user_id")+" apptoken: "+rs.getString("token_value"));
            }
            if ( data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to list apptoken data ("+e.toString()+")","APPTOKEN-LIST-FAILED");
            data.add("error");
        }
        return data;
    }

    /**
     * Function for creating apptokens
     * @param user_id
     * @return
     * @throws SQLException
     */
    public int create_apptoken(int user_id) throws SQLException {
        /**
         * CREATE TABLE TOKEN
         * (
         *   token_id INT AUTO_INCREMENT PRIMARY KEY,
         *   user_id INT,
         *   token_value VARCHAR(100),
         *
         *   CONSTRAINT fk_token FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        String query = "INSERT INTO TOKEN (user_id,token_value) VALUES (?,?);";
        RandomString generator = new RandomString(10);
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,generator.buf);
            ppst.execute();
            TrackApiApplication.database.log("Created apptoken for user_id: "+user_id+" token: |"+generator.buf+"|","APPTOKEN");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to create apptoken ("+e.toString()+")","APPTOKEN-FAILED");
            return -1;
        }
    }

    /**
     * Function for creating webtokens
     * @param mac_address
     * @return Integer
     */
    public int create_webtoken(String mac_address,int user_id) throws SQLException {
        /**
         * CREATE TABLE SESSION_WHITETABLE
         * (
         *     session_whitetable_id INT AUTO_INCREMENT PRIMARY KEY,
         *     user_id INT,
         *     session_token VARCHAR(70),
         *     session_token_time TIMESTAMP,
         *
         *     CONSTRAINT fk_session_whitetable FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */
        String query = "INSERT INTO SESSION_WHITETABLE (user_id,session_token,session_token_time)\n" +
                "VALUES (?,?,?);";
        RandomString generator = new RandomString(10);
        String token = "$WEB$"+generator.buf;
        TrackApiApplication.database.log("Created new web token: "+token,"APPTOKEN-CREATE");
        LocalDateTime ldt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.setString(2,token);
            ppst.setObject(3,ldt);
            ppst.execute();
            return 1;
        }catch(Exception e){
            TrackApiApplication.database.log("Failed to create webtoken ("+e.toString()+")","APPTOKEN-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing apptoken
     * @param user_id
     * @return Integer
     */
    public int remove_apptoken(int user_id) throws SQLException {
        String query = "DELETE FROM TOKEN WHERE user_id = ?;";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ppst.execute();
            TrackApiApplication.database.log("Removed apptoken for user_id: "+user_id,"APPTOKEN-REMOVE");
            return 1;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to remove apptoken ("+e.toString()+")","APPTOKEN-REMOVE-FAILED");
            return -1;
        }
    }

    /**
     * Function for updating user_category
     * @param user_id
     * @param user_category
     * @return Integer
     */
    public int update_user_category(int user_id,String user_category) throws SQLException {
        /**
         * CREATE TABLE USER_DATA
         * (
         *   user_id INT PRIMARY KEY AUTO_INCREMENT,
         *   user_name VARCHAR(150),
         *   user_surname VARCHAR(200),
         *   user_email VARCHAR(200),
         *   user_login VARCHAR(25),
         *   user_password VARCHAR(50),
         *   user_category VARCHAR(100) -- CODES: ADMIN,DEVELOPER,CLIENT
         * );
         */

        String query = "UPDATE USER_DATA SET user_category = ? WHERE user_id = ?;";
        String[] objects = {"ADMIN","DEVELOPER","CLIENT"};
        List<String> data = Arrays.asList(objects);

        if ( data.contains(user_category)){
            try{
                PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);
                ppst.setString(1,user_category);
                ppst.setInt(2,user_id);
                ppst.execute();
                TrackApiApplication.database.log("Updated user_category to "+user_category+" for user_id: "+user_id,"USER-CATEGORY");
                return 1;
            } catch (SQLException e) {
                TrackApiApplication.database.log("Failed to update user_category ("+e.toString()+")","USER-CATEGORY-UPDATE");
                return -1;
            }
        }
        else{
            TrackApiApplication.database.log("Wrong user_category name","USER-CATEGORY-WRNAME");
            return -2;
        }
    }
}
