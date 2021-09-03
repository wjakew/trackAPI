/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.administrator.Password_Validator;
import com.mysql.cj.xdevapi.PreparableStatement;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database_Admin {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Admin(Database_Connector database){
        this.database = database;
    }

    /**
     * Logging admin to trackAPI
     * @param login
     * @param password
     * @return boolean
     * @throws NoSuchAlgorithmException
     */
    public boolean log_admin(String login,String password) throws NoSuchAlgorithmException, SQLException {
        Password_Validator pv = new Password_Validator(password);
        String query = "SELECT * FROM USER_DATA where user_login = ?;";

        try{

            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,login);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                if ( pv.hash().equals(rs.getString("user_password"))){
                    if ( rs.getString("user_category").equals("ADMIN")){
                        database.admin_id = rs.getInt("user_id");
                        database.log("Admin "+login+" logged to trackAPI","ADM_LOGIN");
                        return true;
                    }
                    else{
                        database.log("Admin "+login+" - not elevated","ADM_ERRELV");
                        return false;
                    }
                }
                database.admin_id = -1;
                return false;
            }
            database.log("Admin "+login+" not found","ADM_NOTFOUND");
            return false;
        } catch (SQLException e) {
            database.log("Failed to log admin ("+e.toString()+")","ERROR-LOGIN");
            return false;
        }
    }
}
