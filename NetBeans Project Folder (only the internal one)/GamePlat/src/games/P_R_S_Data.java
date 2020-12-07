/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.io.Serializable;

/**
 *
 * @author Rory
 */
public class P_R_S_Data implements gameDataInterface, Serializable{
    private String userID;
    private String move;
    

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

    /**
     * @return the move
     */
    public String getMove() {
        return move;
    }

    /**
     * @param move the move to set
     */
    public void setMove(String move) {
        this.move = move;
    }
    
    
}
