/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operations;

import games.gameDataInterface;
import java.io.Serializable;

/**
 *
 * @author Rory
 */
public class GameResultMsg implements Serializable{
    private int result;
    private String[] winners;
    private gameDataInterface progress;

    public GameResultMsg(int i){
        this.result = i;
    }

    public GameResultMsg(int i, String[] win){
        this.result = i;
        winners = win;
    }

    /**
     * @return the result
     */
    public int getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     * @return the winners
     */
    public String[] getWinners() {
        return winners;
    }

    /**
     * @param winners the winners to set
     */
    public void setWinners(String[] winners) {
        this.winners = winners;
    }

    /**
     * @return the progress
     */
    public gameDataInterface getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(gameDataInterface progress) {
        this.progress = progress;
    }
}