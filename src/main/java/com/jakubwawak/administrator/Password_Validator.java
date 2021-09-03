/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 *Object for creating password
 * @author x
 */
public class Password_Validator {

    private String given_string;
    private MessageDigest hash;

    ArrayList<String> pv_log;
    /**
     * Constructor
     * @param password
     */
    public Password_Validator(String password) throws NoSuchAlgorithmException{
        given_string = password;
        hash = MessageDigest.getInstance("MD5");
        pv_log = new ArrayList<>();
        pv_log.add("Created object with given ("+password+")");
    }

    /**
     * Function for hashing password
     * @return String
     * @throws NoSuchAlgorithmException
     */
    public String hash() throws NoSuchAlgorithmException{
        pv_log.add("Trying to hash data");
        try{
            byte[] messageD = hash.digest(given_string.getBytes());

            BigInteger number = new BigInteger(1,messageD);

            String hashtext = number.toString(16);

            while ( hashtext.length() < 32 ){
                hashtext = "0" + hashtext;
            }
            pv_log.add("Hash method exited successfull");
            return hashtext;

        }catch(Exception e){
            pv_log.add("Hash method failed ("+e.toString()+")");
            System.out.println(hash);
            return null;
        }
    }

    /**
     * Function for comparing data
     * @param data_to_compare
     * @return Integer
     * @throws NoSuchAlgorithmException
     */
    public int compare(String data_to_compare) throws NoSuchAlgorithmException{
        String hash_data = hash();
        if ( hash_data != null){

            if ( hash_data.equals(data_to_compare)){
                return 1;
            }
            else{
                return 0;
            }
        }
        else{
            return -1;
        }
    }

}
