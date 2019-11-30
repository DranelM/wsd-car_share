package com.sixpistols.carshare.messages;

public class UserCredentials implements java.io.Serializable {
    public String login;
    public String password;

    @Override
    public String toString() {
        return "UserCredentials{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
