package com.sixpistols.carshare.messages;

public class CancelOfferReport implements java.io.Serializable {
    public CancelOffer cancelOffer;

    @Override
    public String toString() {
        return "CancelOfferReport{" +
                "cancelOffer=" + cancelOffer +
                '}';
    }
}
