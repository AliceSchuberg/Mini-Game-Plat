package operations;

import java.io.Serializable;
import java.util.HashMap;

public class StartMessage implements Serializable {
    private boolean ready;
    private HashMap<String,Boolean> readyList;
    private String gameType;
    
    public void setGameType(String str){
        this.gameType = str;
    }
    
    public String getGameType(){
        return this.gameType;
    }
    
    public StartMessage(){
        
    }
    
    public StartMessage(boolean r){
        setReady(r);
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * @return the readyList
     */
    public HashMap<String,Boolean> getReadyList() {
        return readyList;
    }

    /**
     * @param readyList the readyList to set
     */
    public void setReadyList(HashMap<String,Boolean> readyList) {
        this.readyList = readyList;
    }
}
