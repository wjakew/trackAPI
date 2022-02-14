/**
 Jakub Wawak
 kubawawak@gmail.com
 all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.room.Room;

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
     * Function for checking if user is room admin
     * @param room_id
     * @param user_id
     * @return Boolean
     */
     int check_room_admin(int room_id,int user_id) throws SQLException {
        String query = "SELECT room_member_id FROM ROOM_MEMBER WHERE room_id = ? and user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.setInt(2,user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return 1;
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
                data.add(database.get_userlogin_byid(user_id));
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
                database.log("Created room admin! User (user_id:"+user_id+") is now admin of room_id:"+room_id,"ROOM-ADMIN-CRT");
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
            database.log("Created room admin! User (user_id:"+user_id+") is now admin of room_id:"+room_id,"ROOM-ADMIN-CRT");
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
     * -1 - database error
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
     * Function for removing room
     * @param room_id
     * @return Integer
     */
    public int remove_room(int room_id,String room_password) throws SQLException {
        String query = "DELETE FROM ROOM WHERE room_id = ? && room_password = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,room_id);
            ppst.setString(2,room_password);
            ppst.execute();
            database.log("Room removed!","ROOM_REMOVE");
            return 1;
        }catch(SQLException e){
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
