package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelRequest implements java.io.Serializable {
    public String requestId;
    public String passengerId;
    public List<Coordinate> coordinateList;
    public long startTime;
    public long endTime;

    public TravelRequest() {
        this.coordinateList = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "TravelRequest{" +
                "requestId='" + requestId + '\'' +
                ", passengerId='" + passengerId + '\'' +
                ", coordinateList=" + coordinateList +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
