/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.database.Database_2FactorAuth;
import com.jakubwawak.maintanance.ConsoleColors;
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
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Object for creating menu for the system
 */
public class Menu {
    public static final String ANSI_GREEN = "\033[1;32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public boolean flag;
    Scanner user_input;
    String raw_data;
    ArrayList<String> history;
    int clear_blank;
    /**
     * Constructor
     */
    public Menu(){
        flag = true;
        history = new ArrayList<>();
        user_input = new Scanner(System.in);
        clear_blank = 0;
    }

    /**
     * Function for showing header
     */
    void show_header(){
        String header = "  ,-.       _,---._ __  / \\\n" +
                        " /  )    .-'       `./ /   \\\n" +
                        "(  (   ,'            `/    /|\n" +
                        " \\  `-\"             \\'\\   / |\n" +
                        "  `.              ,  \\ \\ /  |\n" +
                        "   /`.          ,'-`----Y   |\n" +
                        "  (            ;        |   '\n" +
                        "  |  ,-.    ,-'         |  /\n" +
                        "  |  | (   |      menu  | /\n" +
                        "  )  |  \\  `.___________|/\n" +
                        "  `--'   `--'";
        System.out.println(ConsoleColors.YELLOW_BOLD + header + ConsoleColors.RESET);
    }

    /**
     * Function for running the menu
     */
    public void run() throws IOException, SQLException, NoSuchAlgorithmException, ClassNotFoundException {
        show_header();
        while(flag) {
            System.out.print(ConsoleColors.BLUE_BOLD+TrackApiApplication.database.admin_login+"@"+"trackapi"+TrackApiApplication.version+">"+ANSI_RESET);
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
                    try{
                        HealthMonitor hm = new HealthMonitor();
                        System.out.println(hm.info());
                    }catch(Exception e){
                        System.out.println("Failed to load health, no server connected");
                    }
                    System.out.println("till death we do art. ~Jakub Wawak 2021");
                    System.out.println("LAN data: ");
                    Enumeration e2 = NetworkInterface.getNetworkInterfaces();
                    int counter = 0;
                    while(e2.hasMoreElements())
                    {
                        NetworkInterface n = (NetworkInterface) e2.nextElement();
                        Enumeration ee = n.getInetAddresses();
                        while (ee.hasMoreElements())
                        {
                            InetAddress i = (InetAddress) ee.nextElement();
                            if ( counter == 2){
                                System.out.println("LAN ip: "+i.getHostAddress());
                            }
                            counter++;
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
                case "reload":
                    System.out.println("Trying to rerun application...");
                    if ( TrackApiApplication.load_configuration() ){
                        if ( TrackApiApplication.load_database_connection() ){
                            if ( TrackApiApplication.authorize(1) ){
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
                {
                    if (raw_data.split(" ").length == 3) {
                        //cruser -login -password
                        try {
                            String user_login = raw_data.split(" ")[1];
                            String user_password = raw_data.split(" ")[2];
                            User_Data user = new User_Data();
                            user.manual_register(user_login, user_password);
                            user.create_user_configuration();
                        } catch (Exception e) {
                            System.out.println("wrong arguments.");
                        }
                    } else if ( raw_data.split(" ").length == 2) {
                        //cruser -email
                        String email = raw_data.split(" ")[1];
                        if (email.contains("@") && email.contains(".")){
                            User_Data ud = new User_Data();
                            ud.manual_register_email(email);
                        }
                    }
                    else{
                        System.out.println("Wrong arguments, check help.");
                    }
                    break;
                }
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
                    if ( raw_data.split(" ").length == 1){
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        TrackApiApplication.header();
                    }
                    else{
                        if ( raw_data.split(" ")[1].equals("blank")){
                            if ( clear_blank == 1){
                                clear_blank = 0;
                                System.out.println("Clear blank false");
                            }
                            else{
                                clear_blank = 1;
                                System.out.println("Clear blank true");
                            }
                        }
                    }
                    break;
                case "config":
                    if ( raw_data.split(" ").length == 2 && raw_data.split(" ")[1].equals("create")){
                        TrackApiApplication.configuration.load_user_data();
                        TrackApiApplication.configuration.copy_configuration();
                    }
                    else{
                        TrackApiApplication.configuration.show_configuration();
                    }
                    break;
                case "log":
                    if ( raw_data.split(" ").length == 2 ){
                        //log -size, log -state
                        try{
                            //log -size
                            int size = Integer.parseInt(raw_data.split(" ")[1]);
                            TrackApiApplication.database.show_log(size);
                        }catch(Exception e){
                            //log -state
                            switch(raw_data.split(" ")[1]){
                                case "off":
                                    System.out.println("Log printing off.");
                                    TrackApiApplication.database.log_printing = 0;
                                    break;
                                case "on":
                                    System.out.println("Log printing on.");
                                    TrackApiApplication.database.log_printing = 1;
                                    break;
                            }
                        }
                    }
                    else{
                        //log
                        TrackApiApplication.database.show_log(0);
                    }
                    break;
                case "bluser":
                {
                    if(raw_data.split(" ").length == 3){
                        switch(raw_data.split(" ")[1]){
                            case"set":
                            {
                                //setting block
                                try{
                                    int user_id = Integer.parseInt(raw_data.split(" ")[2]);
                                    User_Data ud = new User_Data();
                                    ud.user_id = user_id;
                                    ud.set_block(ud.user_id);
                                }catch(NumberFormatException e){
                                    System.out.println("Failed to parse user_id. Check help.");
                                }
                                break;
                            }
                            case"remove":
                            {
                                //removing block
                                try{
                                    int user_id = Integer.parseInt(raw_data.split(" ")[2]);
                                    User_Data ud = new User_Data();
                                    ud.user_id = user_id;
                                    ud.remove_block(ud.user_id);
                                }catch(NumberFormatException e){
                                    System.out.println("Failed to parse user_id. Check help.");
                                }
                                break;
                            }
                        }
                    }
                    else{
                        System.out.println("Wrong command usage. Check help.");
                    }
                    break;
                }
                case "lsblock":
                {
                    System.out.println("Showing current blocked users:");
                    ArrayList<String> blocked_list = TrackApiApplication.database.list_blocked_users();
                    for(String line : blocked_list){
                        System.out.println(line);
                    }
                    break;
                }
                case "web_session":
                {
                    //web_session [web_session -state, web_session create -macaddress -user_id] -state[on/off], creates new session for web
                    if(raw_data.split(" ").length == 4) {
                        switch (raw_data.split(" ")[1]) {
                            case "create":
                            {
                                try{
                                    int user_id = Integer.parseInt(raw_data.split(" ")[3]);
                                    TrackApiApplication.database.create_webtoken(raw_data.split(" ")[2],user_id);
                                }catch(NumberFormatException e){
                                    System.out.println("Wrong user_id");
                                }
                                break;
                            }
                            default:
                            {
                                System.out.println("Wrong command usage.");
                                break;
                            }
                        }
                    }
                    else if ( raw_data.split(" ").length == 2){
                        if ( raw_data.split(" ")[1].contains("on") || raw_data.split(" ")[1].contains("off")){
                            TrackApiApplication.database.update_programcodes("web_apps",raw_data.split(" ")[1]);
                        }
                    }
                    else{
                        System.out.println("Wrong command use. Check help.");
                    }
                    break;
                }
                case "2fa":
                {
                    // 2fa show
                    if( raw_data.split(" ").length == 2){
                        if ( raw_data.split(" ")[1].equals("show")){
                            Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
                            d2fa.show_2fa_enabled_users();
                        }
                        else{
                            System.out.println("Wrong command usage.");
                        }
                    }
                    //2fa enable -user_id -email
                    else if (raw_data.split(" ").length == 4){
                        if ( raw_data.split(" ")[1].equals("enable")){
                            try{
                                int user_id = Integer.parseInt(raw_data.split(" ")[2]);
                                String email = raw_data.split(" ")[3];
                                Pattern pattern = Pattern.compile(".*@*.");
                                Matcher matcher = pattern.matcher(email);
                                if ( matcher.matches() ){
                                    Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
                                    d2fa.enable_authorization(user_id,email);
                                }
                                else{
                                    System.out.println("Wrong email address");
                                }
                            }catch(NumberFormatException e){
                                System.out.println("Wrong user_id.");
                            }
                        }
                        else{
                            System.out.println("Wrong command usage.");
                        }
                    }
                    else if ( raw_data.split(" ").length == 3){
                        // 2fa disable -user_id
                        if ( raw_data.split(" ")[1].contains("disable")){
                            Database_2FactorAuth d2fa = new Database_2FactorAuth(TrackApiApplication.database);
                            try{
                                int user_id = Integer.parseInt(raw_data.split(" ")[2]);
                                d2fa.disable_authorization(user_id);
                            }catch(NumberFormatException e){
                                System.out.println("Wrong user_id.");
                            }
                        }
                    }
                    break;
                }
                case "":
                    if ( clear_blank == 1){
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        TrackApiApplication.header();
                    }
                    break;
                case "help":
                    System.out.println(ConsoleColors.RED_BOLD_BRIGHT+"crsession [crsession -user_id] - creates session for given user");
                    System.out.println("rmsession [rmsession, rmsession -user_id] - removes session, removes session for given user");
                    System.out.println("lssession - lists all sessions");
                    System.out.println("web_session [web_session -state, web_session create -macaddress] -state[on/off], creates new session for web");
                    System.out.println("2fa [2fa show,2fa enable -user_id -email ,2fa disable -user_id, 2fa create -user_id] - for maintaining 2fa security for users");
                    System.out.println("cruser [cruser -login -password, cruser -email] - creates user with given login and password");
                    System.out.println("lsuser [lsuser, lsuser active] - lists all users, lists all active users" );
                    System.out.println("mnuser [mnuser -user_id email value, mnuser -user_id reset] - sets user email, reset user password");
                    System.out.println("pauser [pauser -user_id -user_category] - sets category for user");
                    System.out.println("bluser [bluser set -user_id, bluser remove -user_id] - blocks user from the app");
                    System.out.println("lsblock - lists all blocked users");
                    System.out.println("servicetag [servicetag -new_tag] - sets new service tag value");
                    System.out.println("lsapptoken - lists all active apptokens");
                    System.out.println("crapptoken [crapptoken -user_id] - creates new apptoken");
                    System.out.println("rmapptoken [rmapptoken -user_id] - removes active apptokens");
                    System.out.println("log [log,log -size,log -state] - shows log, shows last -size amount of log, turn on/off log printing");
                    System.out.println("info - printing info about the program");
                    System.out.println("clear [clear. clear blank]- clears the terminal, sets 'enter' key as clear");
                    System.out.println("reload - run api again");
                    System.out.println("config [config, config create] - shows config, creates new config");
                    System.out.println("exit - closing api without warning "+ConsoleColors.RESET);
                    break;
            }
            index++;
        }
    }
}
