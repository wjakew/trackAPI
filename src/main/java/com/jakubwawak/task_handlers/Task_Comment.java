/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.task_handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing comment data
 */
public class Task_Comment {

    /**
     * CREATE TABLE TASK_COMMENT
     * (
     *     task_comment_id INT PRIMARY KEY AUTO_INCREMENT,
     *     user_id INT,
     *     task_id INT,
     *     task_comment_content TEXT,
     *
     *     CONSTRAINT fk_task_comment FOREIGN KEY(user_id) REFERENCES USER_DATA(user_id),
     *     CONSTRAINT fk_task_comment2 FOREIGN KEY(task_id) REFERENCES TASK(task_id)
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

    public int task_comment_id;
    public int user_id;
    public int task_id;
    public String task_comment_content;

    /**
     * Constructor
     */
    public Task_Comment(){
        task_comment_id = -1;
        user_id = -1;
        task_id = -1;
        task_comment_content = "";
    }

    /**
     * Constructor with database support
     * @param to_add
     */
    public Task_Comment(ResultSet to_add) throws SQLException {
        task_comment_id = to_add.getInt("task_comment_id");
        user_id = to_add.getInt("user_id");
        task_id = to_add.getInt("task_id");
        task_comment_content = to_add.getString("task_comment_content");
    }

    /**
     * Function for getting glance
     * @return String
     */
    public String get_glance(){
        return task_comment_id+"\n"+task_comment_content;
    }

}
