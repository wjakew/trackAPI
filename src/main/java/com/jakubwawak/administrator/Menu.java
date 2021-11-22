/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.maintanance.HealthMonitor;
import com.jakubwawak.trackAPI.TrackApiApplication;
import com.jakubwawak.users.User_Data;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

/**
 * Object for creating menu for the system
 */
public class Menu {

    public boolean flag;
    Scanner user_input;
    String raw_data;
    ArrayList<String> history;
    /**
     * Constructor
     */
    public Menu(){
        flag = true;
        history = new ArrayList<>();
        user_input = new Scanner(System.in);
    }

    /**
     * Function for showing header
     */
    void show_header(){
        String header = " _ __ ___   ___ _ __  _   _\n" +
                "| '_ ` _ \\ / _ \\ '_ \\| | | |\n" +
                "| | | | | |  __/ | | | |_| |\n" +
                "|_| |_| |_|\\___|_| |_|\\__,_|";
        System.out.println(header);
    }

    /**
     * Function for running the menu
     */
    public void run() throws IOException, SQLException, NoSuchAlgorithmException, ClassNotFoundException {
        show_header();
        while(flag) {
            System.out.print(">");
            raw_data = user_input.nextLine();
            history.add(raw_data);
            create_action();
        }
    }

    /**
     * Function for creating action
     */
    void create_action() throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        int index = 0;
        for(String word : raw_data.split(" ")){
            switch(word){
                case "exit":
                    flag = false;
                    System.out.println("tracAPI exiting...");
                    System.exit(0);
                    break;
                case "info":
                    HealthMonitor hm = new HealthMonitor();
                    System.out.println("till death we do art. ~Jakub Wawak 2021");
                    System.out.println(hm.info());
                    System.out.println("LAN data: ");
                    Enumeration e2 = NetworkInterface.getNetworkInterfaces();
                    while(e2.hasMoreElements())
                    {
                        NetworkInterface n = (NetworkInterface) e2.nextElement();
                        Enumeration ee = n.getInetAddresses();
                        while (ee.hasMoreElements())
                        {
                            InetAddress i = (InetAddress) ee.nextElement();
                            System.out.println(i.getHostAddress());
                        }
                    }
                    try{
                        URL whatismyip = new URL("http://checkip.amazonaws.com");
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                whatismyip.openStream()));

                        String ip = in.readLine(); //you get the IP as a String
                        System.out.println("external ip: "+ip);
                    }catch(Exception e){
                        System.out.println("Cannot load external public ip. ("+e.toString()+")");
                    }
                    System.out.println("end.");
                    break;
                case "rerun":
                    System.out.println("Trying to rerun application...");
                    if ( TrackApiApplication.load_configuration() ){
                        if ( TrackApiApplication.load_database_connection() ){
                            if ( TrackApiApplication.authorize() ){
                                TrackApiApplication.header();
                                SpringApplication.run(TrackApiApplication.class);
                            }
                        }
                    }
                    break;
                case "crsession":
                    try {
                        int user_id = Integer.parseInt(raw_data.split(" ")[index + 1]);
                        TrackApiApplication.database.create_session(user_id);
                    } catch (Exception e) {
                        System.out.println("no arguments.");
                    }
                    break;
                case "rmsession":
                    if ( raw_data.split(" ").length > 1 ){
                        try {
                            int user_id = Integer.parseInt(raw_data.split(" ")[index + 1]);
                            TrackApiApplication.database.remove_session(user_id);
                        } catch (NumberFormatException e) {
                            System.out.println("no arguments.");
                        }
                    }
                    else{
                        TrackApiApplication.database.remove_current_sessions();
                    }
                    break;
                case "lssession":
                    ArrayList<String> data = TrackApiApplication.database.list_current_sessions();
                    System.out.println("Current sessions:");
                    for(String line: data){
                        System.out.println(line);
                    }
                    break;
                case "cruser":
                    if (raw_data.split(" ").length == 3 ){
                        try{
                            String user_login = raw_data.split(" ")[1];
                            String user_password = raw_data.split(" ")[2];
                            User_Data user = new User_Data();
                            user.manual_adder(user_login,user_password);
                        }catch(Exception e){
                            System.out.println("wrong arguments.");
                        }
                    }
                    else{
                        System.out.println("no arguments.");
                    }
                    break;
                case "lsuser":
                    if ( raw_data.split(" ").length > 1 ){
                        try{
                            if ( raw_data.split(" ")[1].equals("active")){
                                System.out.println("Showing active users:");
                                for(String line : TrackApiApplication.database.list_active_users()){
                                    System.out.println(line);
                                }
                                System.out.println("end");
                            }
                        }catch(Exception e){
                            System.out.println("wrong arguments.");
                        }
                    }
                    else{
                        System.out.println("Showing all users:");
                        for(String line : TrackApiApplication.database.list_users()){
                            System.out.println(line);
                        }
                        System.out.println("end.");
                    }
                    break;
                case "mnuser":
                    String[] user_input = raw_data.split(" ");
                    try{
                        int user_id = Integer.parseInt(user_input[1]);
                        switch(user_input.length){
                            case 4:
                                // email adder
                                User_Data user = new User_Data();
                                user.update_email(user_id,user_input[3]);
                                break;
                            case 3:
                                // automatic password reset
                                User_Data user2 = new User_Data();
                                user2.reset_password(user_id);
                                break;
                        }
                    }catch(Exception e){
                        System.out.println("wrong arguments.");
                    }
                    break;
                case "pauser":
                    if ( raw_data.split(" ").length == 3){
                        try{
                            int user_id = Integer.parseInt(raw_data.split(" ")[1]);
                            String user_category = raw_data.split(" ")[2];
                            TrackApiApplication.database.update_user_category(user_id,user_category);
                        }catch(Exception e){
                            System.out.println("wrong arguments.");
                        }
                    }
                    else{
                        System.out.println("wrong arguments.");
                    }
                    break;
                case "servicetag":
                    if ( raw_data.split(" ").length == 2 ){
                        TrackApiApplication.database.update_servicetag(raw_data.split(" ")[1]);
                    }
                    else{
                        System.out.println("wrong arguments.");
                    }
                    break;
                case "lsapptoken":
                    System.out.println("Active apptoken data:");
                    for(String line : TrackApiApplication.database.list_apptoken()){
                        System.out.println(line);
                    }
                    System.out.println("end.");
                    break;
                case "crapptoken":
                    try{
                        int user_id = Integer.parseInt(raw_data.split(" ")[1]);
                        TrackApiApplication.database.create_apptoken(user_id);
                    }catch(Exception e){
                        System.out.println("wrong arguments.");
                    }
                    break;
                case "rmapptoken":
                    try{
                        int user_id = Integer.parseInt(raw_data.split(" ")[1]);
                        TrackApiApplication.database.remove_apptoken(user_id);
                    }catch(Exception e){
                        System.out.println("wrong arguments.");
                    }
                    break;
                case "clear":
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                    break;
                case "help":
                    System.out.println("crsession [crsession -user_id] - creates session for given user");
                    System.out.println("rmsession [rmsession, rmsession -user_id] - removes session, removes session for given user");
                    System.out.println("lssession - lists all sessions");
                    System.out.println("rmuser [rmuser -user_id] - removes user by given user_id");
                    System.out.println("crusser [crusser -login -password] - creates user with given login and password");
                    System.out.println("lsuser [lsuser, lsuser active] - lists all users, lists all active users" );
                    System.out.println("mnuser [mnuser -user_id email value, mnuser -user_id reset] - sets user email, reset user password");
                    System.out.println("pauser [pauser -user_id -user_category] - sets category for user");
                    System.out.println("servicetag [servicetag -new_tag] - sets new service tag value");
                    System.out.println("lsapptoken - lists all active apptokens");
                    System.out.println("crapptoken [crapptoken -user_id] - creates new apptoken");
                    System.out.println("rmapptoken [rmapptoken -user_id] - removes active apptokens");
                    System.out.println("info - printing info about the program");
                    System.out.println("clear - clears terminal");
                    System.out.println("rerun - running api again");
                    System.out.println("exit - closing api without warning ");
                    break;
            }
            index++;
        }
    }
}
