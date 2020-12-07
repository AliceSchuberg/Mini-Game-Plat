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

//A class to tell what operation the user request
public class OperationType implements Serializable {
    private int type;
    /*  
        1 -> sign-in /authentication
        2 -> sign-up
        3 -> create room
        4 -> find public room
        5 -> join room
        6 -> start game
        7 -> gaming
        8 -> result
    */
    public OperationType(){

    }
    public OperationType(int i){
        this.type = i;
    }
    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
    
}
