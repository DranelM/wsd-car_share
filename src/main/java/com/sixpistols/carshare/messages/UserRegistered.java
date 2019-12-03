package com.sixpistols.carshare.messages;

public class UserRegistered implements java.io.Serializable {
    public String message;

    @Override
    public String toString() {
        return "UserRegistered{" +
                "message='" + message + '\'' +
                '}';
    }
}
