package com.sixpistols.carshare.messages;

public class Decision implements java.io.Serializable {
    public TravelOffer travelOffer;
    public String passengerId;
    public Coordinate startCoordinate;
    public Coordinate endCoordinate;

    @Override
    public String toString() {
        return "Decision{" +
                "travelOffer=" + travelOffer +
                ", passengerId='" + passengerId + '\'' +
                ", startCoordinate=" + startCoordinate +
                ", endCoordinate=" + endCoordinate +
                '}';
    }
}
