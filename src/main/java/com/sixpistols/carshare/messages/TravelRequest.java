package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelRequest implements java.io.Serializable {
    private String requestId;
    private String passengerId;
    private List<Coordinate> coordinateList;
    private long startTime;
    private long endTime;

    public TravelRequest() {
        this.coordinateList = new LinkedList<>();
    }

    public TravelRequest(String passengerId, long startTime, long endTime) {
        this.requestId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.passengerId = passengerId;
        this.coordinateList = new LinkedList<>();
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getRequestId() {
        return requestId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public List<Coordinate> getCoordinateList() {
        return coordinateList;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
