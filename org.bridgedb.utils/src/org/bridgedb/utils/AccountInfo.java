/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.utils;

/**
 *
 * @author Christian
 */
public class AccountInfo {
    
    private String uri;
    private String login;
    private String password;
    private String propertyPart1;
    
    public AccountInfo(String propertyPart1){
        this.propertyPart1 = propertyPart1; 
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
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
     * @return the propertyPart1
     */
    public String getPropertyPart1() {
        return propertyPart1;
    }
    
    public String toString(){
        String result = "property:" + propertyPart1;
        if (uri != null){
            result+= "\n\tUri: " + uri;
        }
        if (login != null){
            result+= "\n\tlogin: " + login;
        }
        if (password != null){
            result+= "\n\tlpassword: " + password;
        }
        return result;
    }
}
