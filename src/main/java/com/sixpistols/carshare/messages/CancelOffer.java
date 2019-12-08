package com.sixpistols.carshare.messages;

public class CancelOffer implements java.io.Serializable {
    private TravelOffer travelOffer;

    public CancelOffer(TravelOffer travelOffer) {
        this.travelOffer = travelOffer;
    }

    @Override
    public String toString() {
        return "CancelOffer{" +
                "travelOffer=" + travelOffer +
                '}';
    }

    public TravelOffer getTravelOffer() {
        return travelOffer;
    }

    public String getOfferId() {
        return travelOffer.getOfferId();
    }

    public String getOfferDirectorId() {
        return travelOffer.getOfferDirectorId();
    }
}
