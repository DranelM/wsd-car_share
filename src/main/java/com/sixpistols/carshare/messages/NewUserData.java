package com.sixpistols.carshare.messages;

public class NewUserData implements java.io.Serializable {
    private String login;
    private String password;

    public NewUserData(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "NewUserData{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
