/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameplat;

import java.io.IOException;

/**
 *
 * @author Rory
 */
public class GamePlat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            PlatformServer ps = new PlatformServer();
            ps.AcceptConnection();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
}
