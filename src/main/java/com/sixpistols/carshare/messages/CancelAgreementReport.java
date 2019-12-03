package com.sixpistols.carshare.messages;

public class CancelAgreementReport implements java.io.Serializable {
    public CancelAgreement cancelAgreement;

    @Override
    public String toString() {
        return "CancelAgreementReport{" +
                "cancelAgreement=" + cancelAgreement +
                '}';
    }
}
