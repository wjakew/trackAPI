/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

/**
 * Class for storing standard answers
 */
public class Standard_Answer {
    /**
     * flag return codes:
     *  0 - nothing done
     *  1 - object loaded to database
     * -1 - database error
     * -2 - user not owner
     * -5 - user not found
     * -6 - issue not found
     * -99 - session has expired
     * -11 - invalid app token
     * -22 - failed to parse date
     * -88 - database error when checking session token
     */
    public int flag;
    public String data;
    /**
     * Constructor
     */
    public Standard_Answer(){
        flag = 0;
        data = "blank";
    }
}
