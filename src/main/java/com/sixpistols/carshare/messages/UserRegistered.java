package com.sixpistols.carshare.messages;

public class UserRegistered implements java.io.Serializable {
    private String message;

    public UserRegistered(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserRegistered{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }
}
