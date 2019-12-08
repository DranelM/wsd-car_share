package com.sixpistols.carshare.messages;

public class CancelAgreementReport implements java.io.Serializable {
    private CancelAgreement cancelAgreement;

    public CancelAgreementReport(CancelAgreement cancelAgreement) {
        this.cancelAgreement = cancelAgreement;
    }

    @Override
    public String toString() {
        return "CancelAgreementReport{" +
                "cancelAgreement=" + cancelAgreement +
                '}';
    }

    public CancelAgreement getCancelAgreement() {
        return cancelAgreement;
    }

    public String getAgreementId() {
        return cancelAgreement.getAgreementId();
    }

    public String getDriverId() {
        return cancelAgreement.getDriverId();
    }
}
