package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class OffersList implements java.io.Serializable {
    public List<TravelOffer> travelOffers;

    public OffersList() {
        this.travelOffers = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "OffersList{" +
                "travelOffers=" + travelOffers +
                '}';
    }
}
