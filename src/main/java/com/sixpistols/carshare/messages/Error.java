package com.sixpistols.carshare.messages;

public class Error implements java.io.Serializable {
    private String message;

    public Error(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "message='" + message + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }
}
