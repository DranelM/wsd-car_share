package com.sixpistols.carshare.messages;

public class NewUserData implements java.io.Serializable {
    public String login;
    public String password;

    @Override
    public String toString() {
        return "NewUserData{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
