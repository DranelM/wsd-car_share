package com.sixpistols.carshare.messages;

public class CancelOfferReport implements java.io.Serializable {
    private CancelOffer cancelOffer;

    public CancelOfferReport(CancelOffer cancelOffer) {
        this.cancelOffer = cancelOffer;
    }

    @Override
    public String toString() {
        return "CancelOfferReport{" +
                "cancelOffer=" + cancelOffer +
                '}';
    }

    public CancelOffer getCancelOffer() {
        return cancelOffer;
    }

    public String getOfferId() {
        return cancelOffer.getOfferId();
    }
}
