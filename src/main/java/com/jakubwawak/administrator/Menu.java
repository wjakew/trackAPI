/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.maintanance.HealthMonitor;
import com.jakubwawak.trackAPI.TrackApiApplication;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
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
                case "session":
                    ArrayList<String> data = TrackApiApplication.database.list_current_sessions();
                    System.out.println("Current sessions:");
                    for(String line: data){
                        System.out.println(line);
                    }
                    break;
                case "help":
                    System.out.println("crsession");
                    System.out.println("rmsession");
                    System.out.println("session");
                    System.out.println("info");
                    System.out.println("rerun");
                    System.out.println("exit");
                    break;
            }
            index++;
        }
    }
}
