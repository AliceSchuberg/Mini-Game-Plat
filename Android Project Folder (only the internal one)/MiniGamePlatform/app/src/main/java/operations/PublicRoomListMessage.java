package operations;

import java.io.Serializable;
import java.util.HashMap;

public class PublicRoomListMessage implements Serializable {
    private HashMap<String,String> roomMap;


    public HashMap<String, String> getRoomMap() {
        return roomMap;
    }

    public void setRoomMap(HashMap<String, String> roomMap) {
        this.roomMap = roomMap;
    }
}
