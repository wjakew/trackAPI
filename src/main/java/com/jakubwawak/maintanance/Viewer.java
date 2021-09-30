/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.administrator.Session_Validator;

import java.util.ArrayList;

public class Viewer {

    public int flag;
    public ArrayList<String> view;
    public Session_Validator sv;

    public Viewer(){
        flag = 0;
        view = new ArrayList<>();
    }
}
