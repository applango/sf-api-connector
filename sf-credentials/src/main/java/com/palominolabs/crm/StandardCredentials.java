package com.palominolabs.crm;

/**
 * Created with IntelliJ IDEA. User: bjhlista Date: 12/11/13 Time: 10:32 AM
 */
public class StandardCredentials implements Credentials {

    String username;
    String password;

    public StandardCredentials() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
