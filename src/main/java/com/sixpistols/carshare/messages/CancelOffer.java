package com.sixpistols.carshare.messages;

public class CancelOffer implements java.io.Serializable {
    public TravelOffer travelOffer;

    @Override
    public String toString() {
        return "CancelOffer{" +
                "travelOffer=" + travelOffer +
                '}';
    }
}
