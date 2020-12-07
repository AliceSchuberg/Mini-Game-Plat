/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Rory
 */
public class PublicRoomListMessage implements Serializable{
    private HashMap <String,String> roomMap;

    /**
     * @return the roomMap
     */
    public HashMap <String,String> getRoomMap() {
        return roomMap;
    }

    /**
     * @param roomMap the roomMap to set
     */
    public void setRoomMap(HashMap <String,String> roomMap) {
        this.roomMap = roomMap;
    }
}
