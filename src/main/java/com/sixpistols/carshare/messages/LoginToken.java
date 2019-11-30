package com.sixpistols.carshare.messages;

public class LoginToken implements java.io.Serializable {
    public String id;

    @Override
    public String toString() {
        return "LoginToken{" +
                "id='" + id + '\'' +
                '}';
    }
}
