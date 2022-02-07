/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.administrator.Session_Validator;
import com.jakubwawak.room.Room;

import java.util.ArrayList;

public class Viewer {

    public int flag;
    public ArrayList<String> view;
    public ArrayList<Room> view2;
    public Session_Validator sv;

    public Viewer(){
        flag = 0;
        view = new ArrayList<>();
        view2 = new ArrayList<>();
    }
}
