/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games;

import java.util.HashMap;

/**
 *
 * @author Rory
 */
public interface gameInterface {
    public int hasEnded();//to check if the game has ended
    // -1 -> not ended
    // 0  -> need to wait
    // 1  -> has ended
    public boolean inputData(Object obj);
    public String[] getResult();
    public int joinRoom(String str);
    public void exitRoom(String str);
    public String[] getGamers();
    public void reStart();
    public gameDataInterface getGameData();
}
