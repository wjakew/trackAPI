/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.room.Room;
import com.jakubwawak.room.Room_Message;

import java.util.ArrayList;

/**
 * Object for storing app views
 */
public class Viewer {

    public int flag;
    public ArrayList<String> view;
    public ArrayList<Room> view2;
    public ArrayList<Room_Message> view3;
    public int field;
    public Session_Validator sv;

    /**
     * Constructor
     */
    public Viewer(){
        flag = 0;
        field = -1;
        view = new ArrayList<>();
        view2 = new ArrayList<>();
        view3 = new ArrayList<>();
    }
}
