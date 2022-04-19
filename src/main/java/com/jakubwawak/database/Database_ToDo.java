/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.todo.ToDo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for maintaining To Do objects on database
 */
public class Database_ToDo {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_ToDo(Database_Connector database){

        this.database = database;
    }

    /**
     * Function for showing list of todos
     * @param user_id
     * @param mode
     * @return ArrayList
     * modes:
     * 0 - all todos (with done and archive)
     * 1 - all not done todos
     * 2 - done todos
     */
    public ArrayList<String> glance_todo(int user_id, int mode) throws SQLException {
        ArrayList<String> data = new ArrayList<>();
        String query = "";
        switch(mode){
            case 0:
                query = "SELECT * from TODO where user_id = ?;";
                break;
            case 1:
                query = "SELECT * from TODO where user_id = ? and todo_state = 1 or todo_state = 0;";
                break;
            case 2:
                query = "SELECT * from TODO where user_id = ? and todo_state = 2;";
                break;
            default:
                query = "SELECT * from TODO where user_id = ?;";
                break;
        }
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,user_id);

            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                ToDo object = new ToDo(rs);
                data.add(object.get_glance());
            }
            if ( data.size() == 0 ){
                data.add("Empty");
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to get list of todos ("+e.toString(),"TODO-LIST-FAILED");
            return null;
        }
    }

    /**
     * Function for getting todo data
     * @param todo_id
     * @param user_id
     * @return
     */
    public ToDo get_todo(int todo_id,int user_id) throws SQLException {
        String query = "SELECT * FROM TODO WHERE todo_id = ? and user_id = ?;";
        ToDo todo = new ToDo();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,todo_id);
            ppst.setInt(2,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                database.log("Loaded todo object (todo_id:"+todo_id+")","TODO-GET");
                todo = new ToDo(rs);
            }
        }catch(SQLException e){
            database.log("Failed to get todo object ("+e.toString()+")","TODO-GET-FAILED");
        }
        return todo;
    }

    /**
     * Function for adding todo object to database
     * @param user_id
     * @param todo_title
     * @param todo_desc
     * @param todo_impor
     * @param todo_colour
     * @param todo_state
     * @return Integer
     * return codes:
     * 1 - added
     * -1 - database error
     */
    public int add_todo(int user_id,String todo_title,String todo_desc,int todo_impor,int todo_colour,
                    int todo_state) throws SQLException {
        String query = "INSERT INTO TODO (user_id,todo_title,todo_desc,todo_impor,todo_colour,todo_state)" +
                "\nVALUES (?,?,?,?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,user_id);
            ppst.setString(2,todo_title);
            ppst.setString(3,todo_desc);
            ppst.setInt(4,todo_impor);
            ppst.setInt(5,todo_colour);
            ppst.setInt(6,todo_state);

            ppst.execute();
            database.log("Added new todo! ","TODO-ADD");
            return 1;

        }catch(SQLException e){
            database.log("Failed to add todo ("+e.toString(),"TODO-ADD-FAILED");
            return -1;
        }
    }

    /**
     * Function for adding to the database
     * @param to_add
     * @return
     */
    public ToDo add_todo(ToDo to_add) throws SQLException {
        String query = "INSERT INTO TODO (user_id,todo_title,todo_desc,todo_impor,todo_colour,todo_state)" +
                "\nVALUES (?,?,?,?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,to_add.user_id);
            ppst.setString(2,to_add.todo_title);
            ppst.setString(3,to_add.todo_desc);
            ppst.setInt(4,to_add.todo_impor);
            ppst.setInt(5,to_add.todo_colour);
            ppst.setInt(6,to_add.todo_state);

            ppst.execute();
            database.log("Added new todo! ","TODO-ADD");
            to_add.flag = 1;
            return to_add;

        }catch(SQLException e){
            database.log("Failed to add todo ("+e.toString(),"TODO-ADD-FAILED");
            to_add.flag = -1;
            return to_add;
        }
    }

    /**
     * Function for setting to do object state
     * @param todo_id
     * @param user_id
     * @param todo_state
     * @return Integer
     * return codes:
     * 1 - state updated
     * -1 - database error
     */
    public int set_state(int todo_id, int user_id, int todo_state) throws SQLException {
        String query = "UPDATE TODO SET todo_state = ? where todo_id = ? and user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,todo_state);
            ppst.setInt(2,todo_id);
            ppst.setInt(3,user_id);
            ppst.execute();
            database.log("New state set for todo_id:"+todo_id,"TODO-UPDATE");
            return 1;
        } catch (SQLException e) {
            database.log("Failed to set state for todo_id:"+todo_id+" ("+e.toString()+")","TODO-UPDATE-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing todos
     * @param todo_id
     * @param user_id
     * @return Integer
     * @throws SQLException
     * return codes:
     * 1  - object removed
     * -1 - failed to remove object
     */
    public int remove_todo(int todo_id,int user_id) throws SQLException {
        String query = "DELETE FROM TODO where todo_id = ? and user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,todo_id);
            ppst.setInt(2,user_id);
            ppst.execute();
            database.log("Todo removed! (todo_id:"+todo_id+")","TODO-REMOVE");
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove todo_id:"+todo_id+" ("+e.toString()+")","TODO-REMOVE-FAILED");
            return -1;
        }
    }
}
