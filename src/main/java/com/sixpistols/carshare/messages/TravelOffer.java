package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelOffer implements java.io.Serializable {
    public String offerId;
    public String driverId;
    public String offerDirectorId;
    public List<Coordinate> coordinateList;
    public long startTime;
    public long endTime;
    public int capacity;
    public double price;

    public TravelOffer() {
        this.coordinateList = new LinkedList<>();
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
}
