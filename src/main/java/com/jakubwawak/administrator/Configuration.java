/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for loading configuration files
 */
public class Configuration {

    static String file_src = "config.trak";
    /**
     * Supported file format:
     * -----------------------
     * ip%data
     * name%data
     * user%data
     * password%data
     * -----------------------
     */

    public String database_ip;
    public String database_name;
    public String database_user;
    public String database_password;

    public boolean exists,error;

    File file_object;
    ArrayList<String> file_lines;

    /**
     * Configuration
     */
    public Configuration(){
        file_object = new File(file_src);
        exists = false;
        error = false;
        file_lines = new ArrayList<>();

        if ( file_object.exists() ){
            exists = true;
        }

        database_ip = "";
        database_name = "";
        database_user = "";
        database_password ="";
    }

    /**
     * Function for reading file content
     * @throws FileNotFoundException
     */
    public void read_file() throws FileNotFoundException {
        try{
            Scanner sc = new Scanner(file_object);
            while (sc.hasNextLine())
            {
                file_lines.add(sc.nextLine());
            }
        } catch (IOException e) {
            System.out.println("ERROR-CON03 Error reading file ("+e.toString()+")");
            error = true;
        }
    }

    /**
     * Function for loading data from file
     */
    public void load_file_data(){
        for(String line : file_lines){
            if ( line.contains("ip")){
                this.database_ip = line.split("%")[1];
            }
            else if (line.contains("name")){
                this.database_name = line.split("%")[1];
            }
            else if (line.contains("user")){
                this.database_user = line.split("%")[1];
            }
            else if (line.contains("password")){
                this.database_password = line.split("%")[1];
            }
        }
    }

    /**
     * Function for loading data from file
     */
    public void load_user_data(){
        try{
            System.out.println("Configuration file creator:");
            Scanner sc = new Scanner(System.in);
            Console cnsl = System.console();
            System.out.print("ip?");
            this.database_ip = sc.nextLine();
            System.out.print("database name?");
            this.database_name = sc.nextLine();
            System.out.print("database user?");
            this.database_user = sc.nextLine();
            if ( cnsl != null){
                char[] ch = cnsl.readPassword("password?");
                this.database_password = String.copyValueOf(ch);
            }
            else{
                System.out.print("database password?");
                this.database_password = sc.nextLine();
            }
        }catch(Exception e){
            System.out.println("ERROR-CON02 Failed to load data from user ("+e.toString()+")");
        }

    }

    /**
     * Function for coping and saving configuration to file
     * @throws IOException
     */
    public void copy_configuration() throws IOException {
        try{
            /**
             * ip%data
             * name%data
             * user%data
             * password%data
             */
            FileWriter fw = new FileWriter(file_src);
            fw.write("ip%"+this.database_ip+"\n");
            fw.write("name%"+this.database_name+"\n");
            fw.write("user%"+this.database_user+"\n");
            fw.write("password%"+this.database_password+"\n");

            fw.close();
        }catch(IOException e) {
            System.out.println("ERROR-CON01 File error (" + e.toString() + ")");
            error = true;
        }
    }

    /**
     * Function for showing configuration
     */
    public void show_configuration(){
        System.out.println("Configuration data:");
        System.out.println("ip: "+database_ip);
        System.out.println("database name: "+database_name);
        System.out.println("database user: "+database_user);
        System.out.println("database password: "+database_password);
    }

    /**
     * Function for validation object
     * @return boolean
     */
    public boolean validation(){
        return file_lines.size() == 4;
    }
}
