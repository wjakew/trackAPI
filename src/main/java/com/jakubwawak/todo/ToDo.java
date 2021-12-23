/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.todo;

import java.sql.ResultSet;

/**
 * Object for creating user to do
 */
public class ToDo {
    /**
     * CREATE TABLE TO DO
     * (
     *     todo_id INT PRIMARY KEY AUTO_INCREMENT,
     *     user_id INT,
     *     todo_title VARCHAR(100),
     *     todo_desc VARCHAR(350),
     *     todo_impor INT,  -- 0 - normal, 1- important
     *     todo_colour INT, -- 1 - red, 2 - yellow, 3 - green, 4 - blue
     *     todo_state INT,  -- 0 - not done, 1 - in work, 2 - done
     *
     *     CONSTRAINT fk_todo FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
     * );
     */

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

    public int todo_id;
    public int user_id;
    public String todo_title;
    public String todo_desc;

    public int todo_impor;
    public int todo_colour;
    public int todo_state;

    /**
     * Constructor
     */
    public ToDo(){
        flag = 0;

        todo_id = -1;
        user_id = -1;
        todo_title = "";
        todo_desc = "";

        todo_impor = 0;
        todo_colour = 4;
        todo_state = 0;
    }

    /**
     * Constructor with database support
     * @param to_add
     */
    public ToDo(ResultSet to_add){
        try {
            todo_id = to_add.getInt("todo_id");
            user_id = to_add.getInt("user_id");
            todo_title = to_add.getString("todo_title");
            todo_desc = to_add.getString("todo_desc");

            todo_impor = to_add.getInt("todo_impor");
            todo_colour = to_add.getInt("todo_colour");
            todo_state = to_add.getInt("todo_state");

        }catch(Exception e){
            flag = -1;
            todo_id = -1;
            user_id = -1;
            todo_title = "";
            todo_desc = "";
            todo_impor = 0;
            todo_colour = 4;
            todo_state = 0;
        }
    }

    /**
     * Function for returning glance
     * @return
     */
    public String get_glance(){
        return todo_id+": "+todo_title;
    }

}
