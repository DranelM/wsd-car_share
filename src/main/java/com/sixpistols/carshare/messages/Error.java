package com.sixpistols.carshare.messages;

public class Error implements java.io.Serializable {
    public String message;

    @Override
    public String toString() {
        return "Error{" +
                "message='" + message + '\'' +
                '}';
    }
}
