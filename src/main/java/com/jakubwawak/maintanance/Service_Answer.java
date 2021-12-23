/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.trackAPI.TrackApiApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing Service answers for Service functionality
 */
public class Service_Answer {

    public int flag;
    public String answer;
    private String service_code;
    private boolean error;
    /**
     * Constructor
     * @param service_code
     */
    public Service_Answer(String service_code) throws SQLException {
        this.service_code = service_code;
        error = check_service_code();
        answer = "blank";

    }

    /**
     * Function for checking service codes
     * @return boolean
     */
    boolean check_service_code() throws SQLException {
        String query = "SELECT * FROM PROGRAMCODES WHERE programcodes_key = 'service_tag';";
        try{
            PreparedStatement ppst = TrackApiApplication.database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                if (rs.getString("programcodes_values").equals(service_code)){
                    flag = 1;
                    return false;
                }
            }
            flag = 0;
            return false;
        } catch (SQLException e) {
            TrackApiApplication.database.log("Failed to check service code","SERVICE-CODE-FAILED");
            flag = -1;
            return true;
        }
    }
}
