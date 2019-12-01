package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class TravelOffer implements java.io.Serializable {
    public String id;
    public List<Coordinate> coordinateList;
    public int startTime;
    public int endTime;
    public int capacity;

    public TravelOffer() {
        this.coordinateList = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "TravelOffer{" +
                "id='" + id + '\'' +
                ", coordinateList=" + coordinateList +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", capacity=" + capacity +
                '}';
    }
}
