package com.sixpistols.carshare.messages;

public class CancelAgreement implements java.io.Serializable {
    public Agreement agreement;

    @Override
    public String toString() {
        return "CancelAgreement{" +
                "agreement=" + agreement +
                '}';
    }
}
