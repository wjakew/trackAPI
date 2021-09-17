/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.maintanance.HealthMonitor;

import java.net.UnknownHostException;
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
    public void run() throws UnknownHostException {
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
    void create_action() throws UnknownHostException {
        for(String word : raw_data.split(" ")){
            switch(word){
                case "exit":
                    flag = false;
                    System.out.println("tracAPI exiting...");
                    System.exit(0);
                    break;
                case "info":
                    HealthMonitor hm = new HealthMonitor();
                    System.out.println(hm.info());
                    break;
            }
        }
    }
}
