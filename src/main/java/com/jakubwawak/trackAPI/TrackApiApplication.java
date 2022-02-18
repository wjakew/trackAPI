/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.trackAPI;

import com.jakubwawak.administrator.Configuration;
import com.jakubwawak.administrator.Menu;
import com.jakubwawak.administrator.Password_Validator;
import com.jakubwawak.database.Database_Admin;
import com.jakubwawak.maintanance.ConsoleColors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import com.jakubwawak.database.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

@SpringBootApplication(scanBasePackages = {"com.jakubwawak"})
public class TrackApiApplication {

	public static String version = "v1.2.5";
	public static String build = "180222REV01";

	public static Configuration configuration;
	public static Database_Connector database;
	public static Menu menu;

	/**
	 * Main function of the program
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, NoSuchAlgorithmException {
		clear_console();
		header();
		System.out.print("Loaded startup arguments: ");
		for(String arg : args){
			System.out.println(arg);
		}
		System.out.println("\nLoaded "+args.length+" arguments.");
		if ( args.length == 0){
			// normal startup
			if ( load_configuration() ){
				if ( load_database_connection() ){
					if ( authorize(1) ){
						header();
						System.out.println("Please wait till Spring initializes...");
						SpringApplication.run(TrackApiApplication.class, args);
						System.out.println("trackAPI is running currently. To check commands type /help/");
					}
				}
			}
			else{
				System.out.println("Program aborted");
			}
			// main menu
			menu = new Menu();
			menu.run();
		}
		else{
			//fast startup
			for(String arg : args){
				if ( arg.contains(".trak") ){
					System.out.println("Using configuration file: "+arg);
					configuration = new Configuration(arg);
					if ( configuration.exists ){
						configuration.read_file();
						System.out.println("Configuration file reading..");
						configuration.load_file_data();
						if ( configuration.validation() ){
							System.out.println("Configuration data validated!");
							configuration.show_configuration();
							// configuration file validated
							System.out.println("Connecting to database..");
							database = new Database_Connector();
							System.out.println("Trying to connect as "+configuration.database_user+" to "+configuration.database_ip+"..");
							System.out.println("Using password: "+configuration.database_password.substring(0,2)+"XXXXX");
							try{
								database.connect(configuration.database_ip, configuration.database_name,
										configuration.database_user,configuration.database_password);

								if (database.connected){
									System.out.println("Connection to database established");
									// ready to authorize
									if ( authorize(0) ){
										header();
										System.out.println("Please wait till Spring initializes...");
										SpringApplication.run(TrackApiApplication.class, args);
										System.out.println("trackAPI is running currently. To check commands type /help/");
										menu = new Menu();
										menu.run();
									}
								}
								else{
									System.out.println("Connection failed");
								}
							}catch(SQLException e){
								System.out.println("ERROR-TAA01 Failed to connect to database ("+e.toString()+")");
							}
						}
						else{
							System.out.println("Configuration file not validated, wrong file");
						}
					}
					else{
						System.out.println("File error: cannot find config file");
					}
				}
				break;
			}

		}
	}

	/**
	 * Function for authorizing admin on trackAPI
	 * @return boolean
	 * @param mode
	 * mode:
	 * 1 - console cleared
	 * 0 - console not cleared before authorization
	 */
	public static boolean authorize(int mode) throws SQLException, NoSuchAlgorithmException {
		if ( mode == 1)
			header();
		Scanner sc = new Scanner(System.in);
		Console cnsl = System.console();
		System.out.println(ConsoleColors.BLUE_BOLD+"@@ Admin authorization @@"+ConsoleColors.RESET);
		System.out.print("admin login?");
		String admin_login = sc.nextLine();
		String password = "";
		if (cnsl != null){
			char[] ch = cnsl.readPassword("password?");
			password = String.copyValueOf(ch);
		}
		else{
			System.out.print("password?");
			password = sc.nextLine();
		}
		Database_Admin da = new Database_Admin(database);
		Password_Validator pv = new Password_Validator(password);
		if ( da.log_admin(admin_login,pv.hash())){
			System.out.println("Admin logged");
			return true;
		}
		else {
			System.out.println("Admin failed to log in");
			return false;
		}
	}

	/**
	 * Function for establishing connection to database
	 * @return boolean
	 * @throws SQLException
	 */
	public static boolean load_database_connection() throws SQLException, ClassNotFoundException {
		header();
		Scanner sc = new Scanner(System.in);
		System.out.println(ConsoleColors.BLUE_BOLD+"@@ Database connector creator @@"+ConsoleColors.RESET);
		System.out.println("Connecting to database..");
		database = new Database_Connector();
		System.out.println("Trying to connect as "+configuration.database_user+" to "+configuration.database_ip+"..");
		System.out.println("Using password: "+configuration.database_password.substring(0,2)+"XXXXX");
		System.out.print("continue(y/n)?");
		String ans = sc.nextLine();
		if (ans.equals("y")){
			try{
				database.connect(configuration.database_ip, configuration.database_name,
						configuration.database_user,configuration.database_password);

				if (database.connected){
					System.out.println("Connection to database established");
					return true;
				}
				else{
					System.out.println("Connection failed");
					return false;
				}
			}catch(SQLException e){
				System.out.println("ERROR-TAA01 Failed to connect to database ("+e.toString()+")");
				return false;
			}
		}
		else{
			System.out.println("Connection aborted");
			return false;
		}
	}

	/**
	 * Function for loading configuration
	 */
	public static boolean load_configuration() throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.println(ConsoleColors.BLUE_BOLD+"@@ Configuration Loader @@"+ConsoleColors.RESET);
		configuration = new Configuration();
		if (configuration.exists){
			System.out.println("Configuration file exists..");
			configuration.read_file();
			System.out.println("Configuration file reading..");
			configuration.load_file_data();
			System.out.println("Configuration file loading data..");
			if ( configuration.validation() ){
				System.out.println("Configuration data validated!");
				configuration.show_configuration();
				System.out.print("continue(y/n)?");
				String ans = sc.nextLine();
				return ans.equals("y");
			}
			else{
				System.out.println("Configuration file not validated, wrong file");
				return false;
			}
		}
		else{
			System.out.println("Configuration file not exist..");
			System.out.print("create(y/n)?");
			String ans = sc.nextLine();
			if ( ans.equals("y") ){
				configuration.load_user_data();
				System.out.println("save to file(y/n)?");
				ans = sc.nextLine();
				if (ans.equals("y")){
					configuration.copy_configuration();
				}
				if (!configuration.error){
					System.out.println("Configuration correct");
					System.out.println("continue(y/n)?");
					ans = sc.nextLine();
					return ans.equals("y");
				}
				else{
					System.out.println("Configuration error.Aborted.");
					return false;
				}
			}
			else{
				System.out.println("Aborted");
				return false;
			}
		}
	}

	/**
	 * Function for clearing console
	 */
	public static void clear_console(){
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	/**
	 * Function for loading header
	 */
	public static void header(){
		clear_console();
		String header = " _                  _               _\n" +
				"| |_ _ __ __ _  ___| | ____ _ _ __ (_)\n" +
				"| __| '__/ _` |/ __| |/ / _` | '_ \\| |\n" +
				"| |_| | | (_| | (__|   < (_| | |_) | |\n" +
				" \\__|_|  \\__,_|\\___|_|\\_\\__,_| .__/|_|\n" +
				"                             |_|";
		System.out.print(ConsoleColors.YELLOW_BRIGHT+header+ConsoleColors.RESET);
		System.out.println(ConsoleColors.YELLOW_BOLD+"version: "+version+", build: "+build+ConsoleColors.RESET);

	}

}
