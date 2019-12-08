package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelOffer implements java.io.Serializable {
    private String offerId;
    private String driverId;
    private String offerDirectorId;
    private List<Coordinate> coordinateList;
    private long startTime;
    private long endTime;
    private int capacity;
    private double price;

    public TravelOffer(String driverId, String offerDirectorId, long startTime, long endTime, int capacity, double price) {
        this.offerId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.driverId = driverId;
        this.offerDirectorId = offerDirectorId;
        this.coordinateList = new LinkedList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "TravelOffer{" +
                "offerId='" + offerId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", offerDirectorId='" + offerDirectorId + '\'' +
                ", coordinateList=" + coordinateList +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", capacity=" + capacity +
                ", price=" + price +
                '}';
    }

    public String getOfferId() {
        return offerId;
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
}
