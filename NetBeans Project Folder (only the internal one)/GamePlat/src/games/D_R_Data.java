/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Rory
 */
public class D_R_Data implements gameDataInterface, Serializable{
    private String userID;
    private HashMap<String, Integer> progress;
    
    public D_R_Data(){}
    
    public D_R_Data(HashMap<String, Integer> pro){
        this.progress = pro;
    }
    
    public HashMap<String, Integer> getProgress(){
        return this.progress;
    }
    
    public void setProgress(HashMap<String, Integer> pro){
        this.progress = pro;
    }

    /**
     * @return the userID
     */
    @Override
    public String getPlayerID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    @Override
    public void setPlayerID(String userID) {
        this.userID = userID;
    }
}
