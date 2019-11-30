package com.sixpistols.carshare.messages;

public class Coordinate implements java.io.Serializable {
    public int x;
    public int y;

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
