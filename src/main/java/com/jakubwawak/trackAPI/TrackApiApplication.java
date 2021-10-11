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

	public static String version = "v1.0.0";
	public static String build = "1110REV01";

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
		if ( load_configuration() ){
			if ( load_database_connection() ){
				if ( authorize() ){
					header();
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

	/**
	 * Function for authorizing admin on trackAPI
	 * @return boolean
	 */
	public static boolean authorize() throws SQLException, NoSuchAlgorithmException {
		header();
		Scanner sc = new Scanner(System.in);
		Console cnsl = System.console();
		System.out.println("@@ Admin authorization @@");
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
		System.out.println("@@ Database connector creator @@");
		System.out.println("Connecting to database..");
		database = new Database_Connector();
		System.out.println("Trying to connect as "+configuration.database_user+" to "+configuration.database_ip+"..");
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
		System.out.println("@@ Configuration Loader @@");
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
		System.out.print(header);
		System.out.println("version: "+version+", build: "+build);

	}

}
