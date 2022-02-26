/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.room.Room;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Object for maintaining Room data on database
 */
public class Database_Room {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Room(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for checking room codes and passwords availability
     * @param code
     * @return Boolean
     */
    boolean check_room_codes(String code) throws SQLException {
        String query = "SELECT * FROM ROOM WHERE room_code = ? or room_password = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,code);
            ppst.setString(2,code);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return true;
            }
            return false;
        }catch(SQLException e){
            database.log("Failed to check room codes ("+e.toString()+")","ROOM-CDCH-FAILED");
            return false;
        }
    }

    /**
     * Function for checking invite data
     * @param room_code
     * @param room_password
     * @return Integer
     * Returns room_id, 0 if not found
     */
    public int check_invite_data(String room_code,String room_password) throws SQLException {
        String query = "SELECT room_id FROM ROOM WHERE room_code = ? and room_password = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,room_code);
            ppst.setString(2,room_password);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next()){
                database.log("Found room for given invite data (room_id:"+rs.getInt("room_id")+")","ROOM-INVITEC");
                return rs.getInt("room_id");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to check invite data ("+e.toString()+")","ROOM-INVITEC-FAILED");
            return -1;
        }
    }

    /**
     * Function for checking given password (if password correct)
     * @param password
     * @return Boolean
     */
    boolean check_password(String password) throws SQLException {
        String query = "SELECT room_id FROM ROOM WHERE room_password = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,password);
            ResultSet rs = ppst.executeQuery();
            if(rs.next()){
                database.log("Room password exists in the database","ROOM-PASS");
                return true;
            }
            database.log("Room password not found (room_password:"+password+")","ROOM-PASS");
            return false;
        } catch (SQLException e) {
            database.log("Failed to check room password ("+e.toString()+")","ROOM-PASS-FAILED");
            return false;
        }
    }

    /**
     * Function for checking if user is room admin
     * @param room_id
     * @param user_id
     * @return Boolean
     */
     int check_room_admin(int room_id,int user_id) throws SQLException {
        String query = "SELECT role FROM ROOM_MEMBER WHERE room_id = ? and user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.setInt(2,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                if ( rs.getInt("role") == 1){
                    return 1;
                }
            }
            return 0;
        } catch (SQLException e) {
            database.log("Failed to check room admin ("+e.toString()+")","ROOM-ADMIN-FAILED");
            return -1;
        }
    }

    /**
     * Function for creating random li
     * @return String
     */
    String create_random_string(int length) throws SQLException {
        boolean run = true;
        int targetStringLength = length;
        StringBuilder buffer = new StringBuilder(targetStringLength);;
        while(run){
            buffer = new StringBuilder(targetStringLength);
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            Random random = new Random();
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            if ( !check_room_codes(buffer.toString()) ){
                run = false;
            }
        }
        return buffer.toString();
    }

    /**
     * Function for getting room id by given room code
     * @param room_code
     * @return Integer
     */
    int get_room_id_byroomcode(String room_code) throws SQLException {
        String query = "SELECT room_id FROM ROOM WHERE room_code = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,room_code);
            ResultSet rs = ppst.executeQuery();
            if(rs.next()){
                return rs.getInt("room_id");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to get room id ("+e.toString()+")","ROOM-ID-FAILED");
            return -1;
        }
    }

    /**
     * Function for giving
     * @param room_id
     * @return String
     */
    public String get_room_data(int room_id,int user_id) throws SQLException {
        if ( check_room_admin(room_id,user_id) == 1){
            String query = "SELECT room_code,room_password FROM ROOM where room_id = ?";
            try{
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,room_id);
                ResultSet rs = ppst.executeQuery();
                if(rs.next()){
                    return "Code: "+rs.getString("room_code")+"\nPassword: "+rs.getString("room_password");
                }
                return "Error finding data";
            }catch(SQLException e){
                database.log("Failed to get room data ("+e.toString()+")","ROOM-DATA-FAILED");
                return e.toString();
            }
        }
        return "You are not an admin of this room!";
    }

    /**
     * Function for creating room object
     * @param room_name
     * @param room_desc
     * @return Integer
     */
    public String create_room(String room_name,String room_desc,int user_id) throws SQLException {
        String query = "INSERT INTO ROOM (room_name,room_desc,room_password,room_code)" +
                "\n VALUES (?,?,?,?);";

        String room_code = create_random_string(10);
        String room_password = create_random_string(10);
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,room_name);
            ppst.setString(2,room_desc);
            ppst.setString(3,room_password);
            ppst.setString(4,room_code);
            ppst.execute();
            database.log("Room created. Room code: "+room_code,"ROOM-CREATE");
            int room_id = get_room_id_byroomcode(room_code);
            if (room_id >0){
                create_room_member(room_id,user_id,1);
            }
            return room_code;
        }catch(SQLException e){
            database.log("Failed to create room ("+e.toString(),"ROOM-CREATE-FAILED");
            return "error";
        }
    }

    /**
     * Function for listing room members
     * @param room_id
     * @return ArrayList
     */
    ArrayList list_room_members(int room_id) throws SQLException {
        String query = "SELECT user_id FROM ROOM_MEMBER WHERE room_id = ?;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                int user_id = rs.getInt("user_id");
                data.add(user_id+":"+database.get_userlogin_byid(user_id));
            }

            if (data.size() == 0){
                data.add("Empty");
            }
        } catch (SQLException e) {
            database.log("Failed to list room members ("+e.toString()+")","ROOM-LIST-FAILED");
            data.add("error");
        }
        return data;
    }

    /**
     * Function for getting room admin data
     * @param room_id
     * @return String
     */
    String get_room_admin(int room_id) throws SQLException {
        String query = "SELECT user_id FROM ROOM_MEMBER WHERE room_id = ? and role = 1;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                return rs.getInt("user_id")+": "+database.get_userlogin_byid(rs.getInt("user_id"));
            }
            return "0: none";
        } catch (SQLException e) {
            database.log("Failed to get room admin ("+e.toString()+")","ROOM-ADM-FAILED");
            return "-1: error";
        }
    }

    /**
     * Function for getting string list of room members
     * @param room_id
     * @return String
     * @throws SQLException
     */
    public String get_room_members(int room_id) throws SQLException {
        String data = "";
        ArrayList<String> members = list_room_members(room_id);
        for(String member : members){
            data = data + member + ",";
        }
        return data;
    }
    /**
     * Function for creating room members
     * @param room_id
     * @param user_id
     * @return Integer
     */
    public int create_room_member(int room_id,int user_id,int role,int owner_id) throws SQLException {
        String query = "INSERT INTO ROOM_MEMBER (room_id,user_id,role) VALUES (?,?,?);";
        try{
            if (check_room_admin(room_id,owner_id) == 1){
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,room_id);
                ppst.setInt(2,user_id);
                ppst.setInt(3,role);
                ppst.execute();
                database.log("Created room user! User (user_id:"+user_id+") is now user of room_id:"+room_id+" with role: "+role,
                        "ROOM-ADMIN-CRT");
                return 1;
            }
            database.log("User not room admin - cannot add member","ROOM-ADMIN-NOAUTH");
            return 0;
        }catch(SQLException e){
            database.log("Failed to create room member ("+e.toString()+")","ROOM-ADMIN-FAILED");
            return -1;
        }
    }

    /**
     * Function for adding member to room by given room data
     * @param user_id
     * @param room_code
     * @param room_password
     * @return Integer
     * @throws SQLException
     */
    public int create_room_member_frominvite(int user_id,String room_code,String room_password) throws SQLException {
        int room_id = check_invite_data(room_code,room_password);
        if ( room_id > 0){
            return create_room_member(room_id,user_id,2);
        }
        else{
            return -1;
        }
    }

    /**
     * Function for creating room member
     * @param room_id
     * @param user_id
     * @param role
     * @return Integer
     * @throws SQLException
     */
    public int create_room_member(int room_id,int user_id,int role) throws SQLException {
        String query = "INSERT INTO ROOM_MEMBER (room_id,user_id,role) VALUES (?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.setInt(2,user_id);
            ppst.setInt(3,role);
            ppst.execute();
            database.log("Created room user! User (user_id:"+user_id+") is now user of room_id:"+room_id+" with role: "+role,
                    "ROOM-ADMIN-CRT");
            return 1;
        }catch(SQLException e){
            database.log("Failed to create room member ("+e.toString()+")","ROOM-ADMIN-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing members from rooms
     * @param room_id
     * @param user_id
     * @return Integer
     * return codes:
     *  1 - user removed
     *  2 - user is not an admin,
     * -1 - database error
     */
    public int remove_room_member(int room_id,int user_id,int owner_id) throws SQLException {
        String query = "DELETE FROM ROOM_MEMBER WHERE user_id = ? and room_id = ?;";
        try{
            if ( check_room_admin(room_id,owner_id) == 1 ){
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,room_id);
                ppst.setInt(2,user_id);
                ppst.executeQuery();
                database.log("Room (room_id:"+room_id+") member (user_id:"+user_id+") removed!","ROOM-MEMREM");
                return 1;
            }
            else{
                database.log("User is not an admin.","ROOM-MEMREM");
                return 2;
            }
        }catch(SQLException e){
            database.log("Failed to remove room member ("+e.toString()+")","ROOM-MEMREM-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing room member
     * @param room_id
     * @param user_id
     * @return
     */
    public int remove_room_member(int room_id,int user_id) throws SQLException {
        String query = "DELETE FROM ROOM_MEMBER WHERE user_id = ? and room_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.setInt(2,user_id);
            ppst.executeQuery();
            database.log("Room (room_id:"+room_id+") member (user_id:"+user_id+") removed!","ROOM-MEMREM");
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove room member ("+e.toString()+")","ROOM-MEMREM-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing all room members
     * @param room_id
     * @return Integer
     */
    int remove_members(int room_id) throws SQLException {
        String query = "DELETE FROM ROOM_MEMBER WHERE room_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.execute();
            database.log("Members of the room room_id:"+room_id+" removed!","ROOM-MBMREMOVE");
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove members ("+e.toString()+")","ROOM-MBMREMOVE-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing messages from room
     * @param room_id
     * @return Integer
     * @throws SQLException
     */
    int remove_messages(int room_id) throws SQLException, IOException {
        Database_Room_Message drm = new Database_Room_Message(database);
        drm.download_messages(room_id);
        String query = "DELETE FROM ROOM_MESSAGE WHERE room_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.execute();
            database.log("Removed all messages from room room_id:"+room_id,"ROOM-MSGREMOVE");
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove messages ("+e.toString()+")","ROOM-MSGREMOVE-FAILED");
            return -1;
        }
    }

    /**
     * Function for removing room
     * @param room_id
     * @return Integer
     */
    public int remove_room(int room_id,String room_password) throws SQLException {
        String query = "DELETE FROM ROOM WHERE room_id = ? and room_password = ?;";
        room_password = room_password.replaceAll(" ","");
        try{
            if ( check_password(room_password) ){
                database.log("Trying to remove room (room_id:"+room_id+") with password (room_password:"+room_password+")","ROOM_REMOVE");
                remove_members(room_id);
                remove_messages(room_id);
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,room_id);
                ppst.setString(2,room_password);
                ppst.execute();
                database.log("Room removed!","ROOM_REMOVE");
                return 1;
            }
            else{
                database.log("Password not match any room!","ROOM_REMOVE");
                return 3;
            }
        }catch(SQLException | IOException e){
            database.log("Failed to remove room ("+e.toString()+")","ROOM-REMOVE-FAILED");
            return -1;
        }
    }

    /**
     * Function for getting room data from database
     * @param room_id
     * @return Room
     */
    public Room get_room(int room_id) throws SQLException {
        String query = "SELECT * FROM ROOM where room_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ResultSet rs = ppst.executeQuery();
            if(rs.next()){
                try {
                    return new Room(rs);
                }catch(SQLException e){
                    database.log("Failed to get room by object ("+e.toString()+")","GET-ROOM-OBJECT");
                    return null;
                }
            }
            return null;
        } catch (SQLException e) {
            database.log("Failed to get room data ("+e.toString()+")","GET-ROOM-FAILED");
            return null;
        }
    }

    /**
     * Function for listing all rooms which user participate
     * @param user_id
     * @return
     */
    public ArrayList<Room> list_rooms(int user_id) throws SQLException {
        String query = "SELECT room_id FROM ROOM_MEMBER where user_id = ?;";
        ArrayList<Room> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                Room room = get_room(rs.getInt("room_id"));
                room.owner_login = this.get_room_admin(room.room_id);
                data.add(room);
            }
        } catch (SQLException e) {
            database.log("Failed to list rooms ("+e.toString()+")","ROOM-LIST-FAILED");
        }
        return data;
    }
}
