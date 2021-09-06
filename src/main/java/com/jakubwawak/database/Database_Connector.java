/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.administrator.Configuration;
import com.jakubwawak.administrator.RandomString;
import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Database_Connector {

    // version of database
    public final String version = "v0.0.1";
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

    public Configuration configuration;            // vield for storing configuration data

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
        //log("Started! Database Connector initzialazed");
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
                System.out.println("Bład bazy: con=null ("+log+")");
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
     * Function for creating session
     * @param user_id
     * @return String
     */
    public String create_session(int user_id) throws SQLException {
        LocalDateTime lt = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        String query = "INSERT INTO SESSION_TOKEN (user_id,session_token,session_token_time) VALUES (?,?,?);";

        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            RandomString session = new RandomString(15);

            ppst.setInt(1,user_id);
            ppst.setString(2,session.buf);
            lt = lt.plusMinutes(15);
            log("Created new session: "+session.buf+"for user_id "+user_id,"SESSION-CRT");
            ppst.setObject(3,lt);

            ppst.execute();
            return session.buf;
        } catch (SQLException e) {
            log("Failed to create session! user_id "+user_id+ " ("+e.toString()+")","SESSION-ERR");
            return null;
        }
    }


}
