package com.sixpistols.carshare.messages;

public class Decision implements java.io.Serializable {
    private String decisionId;
    private TravelOffer travelOffer;
    private String passengerId;
    private Coordinate startCoordinate;
    private Coordinate endCoordinate;

    public Decision(TravelOffer travelOffer, String passengerId, Coordinate startCoordinate, Coordinate endCoordinate) {
        this.travelOffer = travelOffer;
        this.passengerId = passengerId;
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
    }

    @Override
    public String toString() {
        return "Decision{" +
                "travelOffer=" + travelOffer +
                ", passengerId='" + passengerId + '\'' +
                ", startCoordinate=" + startCoordinate +
                ", endCoordinate=" + endCoordinate +
                '}';
    }

    public String getDecisionId() {
        return decisionId;
    }

    public TravelOffer getTravelOffer() {
        return travelOffer;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return endCoordinate;
    }

    public String getDriverId() {
        return travelOffer.getDriverId();
    }

    public String getOfferId() {
        return travelOffer.getTravelOfferId();
    }

    public String getOfferDirectorId() {
        return travelOffer.getOfferDirectorId();
    }
}
