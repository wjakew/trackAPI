/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.room.Room_Message;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Database object for maintaining database room message
 */
public class Database_Room_Message {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Room_Message(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for getting room message
     * @param room_message_id
     * @return Room_Message
     */
    public Room_Message get_room_message(int room_message_id) throws SQLException {
        Room_Message rm = new Room_Message();
        String query = "SELECT * FROM ROOM_MESSAGE WHERE room_message_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_message_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                rm = new Room_Message(rs);
            }
        } catch (SQLException e) {
            database.log("Failed to get room message ("+e.toString()+")","ROOM-MESSAGE-FAILED");
        }
        return rm;
    }

    /**
     * Function for getting list of room messages
     * @param room_id
     * @return ArrayList
     */
    public ArrayList<Room_Message> get_room_messages(int room_id) throws SQLException {
        ArrayList<Room_Message> messages = new ArrayList<>();

        /**
         * CREATE TABLE ROOM_MESSAGE
         * (
         *     room_message_id INT PRIMARY KEY AUTO_INCREMENT,
         *     room_message_content TEXT,
         *     room_id INT,
         *     user_id INT,
         *     ping_id INT,
         *     content_id INT,
         *
         *     CONSTRAINT fk_roommessage1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
         *     CONSTRAINT fk_roommessage2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         * );
         */

        String query = "SELECT * FROM ROOM_MESSAGE WHERE room_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                Room_Message rm = new Room_Message(rs);
                rm.get_user_login();
                messages.add(rm);
            }

        }catch(SQLException e){
            database.log("Failed to load messages for room ("+e.toString()+")","ROOM-MESSAGES-FAILED");
        }

        if ( messages.size() == 0){
            messages.add(new Room_Message());
        }
        return messages;
    }

    /**
     * Function for sending room messages
     * @param to_send
     * @return Room_Message
     */
    public Room_Message send_message(Room_Message to_send) throws SQLException {
        /**
         CREATE TABLE ROOM_MESSAGE
         (
         room_message_id INT PRIMARY KEY AUTO_INCREMENT,
         room_message_content TEXT,
         room_time TIMESTAMP,
         room_id INT,
         user_id INT,
         ping_id INT,
         content_id INT,

         CONSTRAINT fk_roommessage1 FOREIGN KEY (room_id) REFERENCES ROOM(room_id),
         CONSTRAINT fk_roommessage2 FOREIGN KEY (user_id) REFERENCES USER_DATA(user_id)
         );
         */
        String query = "INSERT INTO ROOM_MESSAGE (room_id,room_time,room_message_content,user_id,ping_id,content_id)\n"
                +"VALUES (?,?,?,?,?,?);";

        Room_Message rm = to_send;
        rm.look_for_logins();
        try{
            database.log("Trying to set new message on database!","ROOM-SEND");
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,rm.room_id);
            ppst.setObject(2,rm.room_time);
            ppst.setString(3,rm.room_message_content);
            ppst.setInt(4,rm.user_id);
            ppst.setInt(5,rm.ping_id);
            ppst.setInt(6,rm.content_id);
            ppst.execute();
            database.log("Message from user_id:"+rm.user_id+" to room room_id:"+rm.room_id+" was sent!","ROOM-SEND");
            rm.flag = 1;
        }catch(SQLException e){
            database.log("Failed to send message to database ("+e.toString()+")","ROOM-SEND-FAILED");
            rm.flag = -1;
        }
        return rm;
    }
    /**
     * Function for downloading messages from rooms
     * @param room_id
     * @return Integer
     */
    public int download_messages(int room_id) throws IOException, SQLException {
        String filename = "room_messages_"+room_id+".txt";
        FileWriter fw = new FileWriter(filename);
        Database_Room dr = new Database_Room(database);
        ArrayList<String> members = dr.list_room_members(room_id);
        fw.write("Room members: \n");
        for(String line : members){
            fw.write(line+"\n");
        }
        fw.write("Messages: \n");
        String query = "SELECT * FROM ROOM_MESSAGE WHERE room_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ResultSet rs = ppst.executeQuery();
            while (rs.next()){
                Room_Message rm = new Room_Message(rs);
                fw.write(rm.room_time.toString()+" - user_id:"+rm.user_id+" > "+rm.room_message_content+"\n");
            }
            fw.write("END.");
            fw.close();
            return 1;
        }catch(SQLException e){
            database.log("Failed to download all messages ("+e.toString()+")","ROOM-DWNMESSA-FAILED");
            return -1;
        }

    }
}
