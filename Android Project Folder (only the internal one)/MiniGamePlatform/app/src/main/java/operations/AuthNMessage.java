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
//Message class used to authenticate the user 
public class AuthNMessage implements Serializable{
    private String id;
    private String password;
    private boolean authorizedResult;

    public AuthNMessage(String i, String p){
        this.id = i;
        this.password = p;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the authorizedResult
     */
    public boolean isAuthorizedResult() {
        return authorizedResult;
    }

    /**
     * @param authorizedResult the authorizedResult to set
     */
    public void setAuthorizedResult(boolean authorizedResult) {
        this.authorizedResult = authorizedResult;
    }
    
}
