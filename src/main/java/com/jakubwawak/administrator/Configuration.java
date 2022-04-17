/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.maintanance.ConsoleColors;

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
     * email_address%XXXXXXXXXXXX
     * email_pass%XXXXXXXXXXX
     * url%data
     * mode%data
     * -----------------------
     */

    public String database_ip;
    public String database_name;
    public String database_user;
    public String database_password;
    public String database_url;
    public String database_mode;

    public String mail_email_address,mail_email_password;

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
        mail_email_address = "";
        mail_email_password = "";
        database_url = "";
        database_mode = "";
    }

    /**
     * Configuration with custom file
     * @param file_src
     */
    public Configuration(String file_src){
        file_object = new File(file_src);
        this.file_src = file_src;
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
        mail_email_address = "";
        mail_email_password = "";

        database_url = "";
        database_mode = "";
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
        try{
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
                else if (line.contains("email_address")){
                    this.mail_email_address = line.split("%")[1];
                }
                else if (line.contains("email_pass")){
                    this.mail_email_password = line.split("%")[1];
                }
                else if (line.contains("url")){
                    this.database_url = line.split("%")[1];
                }
                else if (line.contains("mode")){
                    this.database_mode = line.split("%")[1];
                }
            }
        }catch(Exception e){
            System.out.println(ConsoleColors.RED_BOLD_BRIGHT+"Configuration file readout error"+ConsoleColors.RESET);
            System.out.println(e.toString());
            System.out.println("Remove .track configuration file and restart the program");
            System.exit(1);
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
            System.out.print("database mode (file/server)?");
            String user_dec = sc.nextLine();
            if ( user_dec.equals("file") ){
                this.database_mode = "file";
                System.out.print("file url: ");
                this.database_url = sc.nextLine();

                this.database_ip = "none";

                this.database_name = "none";

                this.database_user = "none";

                this.database_password = "none";

                this.mail_email_address = "none";
                this.mail_email_password = "none";
            }
            else if (user_dec.equals("server")){
                this.database_mode = "server";
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
                System.out.print("mail address provider(leave blank to use default)?");
                this.mail_email_address = sc.nextLine();
                if ( !this.mail_email_address.equals("")){
                    if ( cnsl != null){
                        char[] ch = cnsl.readPassword("password?");
                        this.mail_email_password= String.copyValueOf(ch);
                    }
                    else{
                        System.out.print("password?");
                        this.mail_email_password = sc.nextLine();
                    }
                }
            }
            else{
                System.out.println("Wrong input");
                System.exit(0);
            }

        }catch(Exception e){
            System.out.println("ERROR-CON02 Failed to load data from user ("+e.toString()+")");
        }
    }

    /**
     * Function for checking if email data is correct
     * @return boolean
     */
    public boolean check_mail_data(){
        return !(this.mail_email_address.equals("") || this.mail_email_password.equals(""));
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
             * email_address%XXXXXXXXXXXX
             * email_pass%XXXXXXXXXXX
             * url%data
             * mode%data
             */

            FileWriter fw = new FileWriter(file_src);
            fw.write("ip%"+this.database_ip+"\n");
            fw.write("name%"+this.database_name+"\n");
            fw.write("user%"+this.database_user+"\n");
            fw.write("password%"+this.database_password+"\n");
            fw.write("email_address%"+this.mail_email_address+"\n");
            fw.write("email_pass%"+this.mail_email_password+"\n");
            fw.write("url%"+this.database_url+"\n");
            fw.write("mode%"+this.database_mode);

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
        if ( this.database_mode.equals("server")){
            System.out.println("ip: "+database_ip);
            System.out.println("database name: "+database_name);
            System.out.println("database user: "+database_user);
            System.out.println("database password: "+database_password);
        }
        else{
            System.out.println("database url: "+database_url);
        }
        System.out.println("email password: "+mail_email_password);
        System.out.println("email address: "+mail_email_address);
        if ( mail_email_password.equals("") || mail_email_address.equals("") ){
            System.out.println("WARNING! EMAIL ADDRESS OR PASSWORD EMPTY!!!");
        }
    }

    /**
     * Function for validation object
     * @return boolean
     */
    public boolean validation(){
        return file_lines.size() >= 4;
    }
}
