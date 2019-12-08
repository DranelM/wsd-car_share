package com.sixpistols.carshare.messages;

public class CancelAgreement implements java.io.Serializable {
    private Agreement agreement;

    public CancelAgreement(Agreement agreement) {
        this.agreement = agreement;
    }

    @Override
    public String toString() {
        return "CancelAgreement{" +
                "agreement=" + agreement +
                '}';
    }

    public Agreement getAgreement() {
        return agreement;
    }

    public String getAgreementId() {
        return agreement.getAgreementId();
    }

    public String getDriverId() {
        return agreement.getDriverId();
    }

    public String getOfferDirectorId() {
        return agreement.getOfferDirectorId();
    }
}
