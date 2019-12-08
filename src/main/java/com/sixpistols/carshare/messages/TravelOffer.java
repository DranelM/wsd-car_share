package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelOffer implements java.io.Serializable {
    public enum Status {
        ACTIVE,
        FULL,
        FINISHED,
        CANCELED
    }

    private String travelOfferId;
    private String driverId;
    private String offerDirectorId;
    private List<Coordinate> coordinateList;
    private long startTime;
    private long endTime;
    private int capacity;
    private double price;
    private Status status;

    public TravelOffer(String driverId, String offerDirectorId, long startTime, long endTime, int capacity, double price) {
        this.travelOfferId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.driverId = driverId;
        this.offerDirectorId = offerDirectorId;
        this.coordinateList = new LinkedList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.price = price;
        this.status = Status.ACTIVE;
    }

    @Override
    public String toString() {
        return "TravelOffer{" +
                "travelOfferId='" + travelOfferId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", offerDirectorId='" + offerDirectorId + '\'' +
                ", coordinateList=" + coordinateList +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }

    public String getTravelOfferId() {
        return travelOfferId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getOfferDirectorId() {
        return offerDirectorId;
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

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void changeCapacity(int space) {
        capacity = capacity + space;
    }
}
