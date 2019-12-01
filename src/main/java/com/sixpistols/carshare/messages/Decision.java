package com.sixpistols.carshare.messages;

public class Decision implements java.io.Serializable {
    public String travelOfferId;
    public Coordinate startCoordinate;
    public Coordinate endCoordinate;
    public int space;
}
