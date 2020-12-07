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
// A class to record room information, primary for creating-room purpose
public class CreateRoomMsg implements Serializable {
    private String gameType;
    private String roomID;
    private String creatorID;

    public void setCreatorID(String id){
        creatorID = id;
    }

    public String getCreatorID(){
        return this.creatorID;
    }

    public CreateRoomMsg(String gt){
        this.gameType = gt;
    }

    /**
     * @return the gameType
     */
    public String getGameType() {
        return gameType;
    }

    /**
     * @param gameType the gameType to set
     */
    public void setGameType(String gameType) {
        this.gameType = gameType;
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
}
