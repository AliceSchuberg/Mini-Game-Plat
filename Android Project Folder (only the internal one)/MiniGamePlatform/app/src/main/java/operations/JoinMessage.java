/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.io.Serializable;

/**
 *
 * @author Rory
 */
public class JoinMessage implements Serializable{
    private String roomID;
    private String userID;
    private int joinOrder;

    private boolean roomExist;

    public void setRoomExist(boolean e){
        this.roomExist = e;
    }

    public boolean isRoomExist(){
        return roomExist;
    }

    /**
     * @return the roomID
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * @param roomID the roomID to set
     */
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the joinOrder
     */
    public int getJoinOrder() {
        return joinOrder;
    }

    /**
     * @param joinOrder the joinOrder to set
     */
    public void setJoinOrder(int joinOrder) {
        this.joinOrder = joinOrder;
    }

}
