package com.sixpistols.carshare.messages;

public class Agreement implements java.io.Serializable {
    private String agreementId;
    private Decision decision;

    public Agreement(Decision decision) {
        this.agreementId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.decision = decision;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "agreementId='" + agreementId + '\'' +
                ", decision=" + decision +
                '}';
    }

    public String getAgreementId() {
        return agreementId;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getDriverId() {
        return decision.getDriverId();
    }

    public String getOfferDirectorId() {
        return decision.getOfferDirectorId();
    }

    public String getOfferId() {
        return decision.getOfferId();
    }

    public String getPassengerId() {
        return decision.getPassengerId();
    }

    public TravelOffer getTravelOffer() {
        return decision.getTravelOffer();
    }
}
