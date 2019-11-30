package com.sixpistols.carshare.messages;

import java.util.Collection;

public class OffersList implements java.io.Serializable {
    public Collection<TravelOffer> travelOffers;

    @Override
    public String toString() {
        return "OffersList{" +
                "travelOffers=" + travelOffers +
                '}';
    }
}
