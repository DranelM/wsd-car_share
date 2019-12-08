package com.sixpistols.carshare.messages;

public class UserCredentials implements java.io.Serializable {
    private String login;
    private String password;

    public UserCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
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
