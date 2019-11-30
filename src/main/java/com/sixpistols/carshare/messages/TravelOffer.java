package com.sixpistols.carshare.messages;

import java.util.Collection;

public class TravelOffer implements java.io.Serializable {
    public String id;
    public Collection<Coordinate> coordinateList;
    public int startTime;
    public int endTime;
    public int capacity;
}
