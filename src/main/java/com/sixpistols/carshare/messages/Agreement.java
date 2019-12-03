package com.sixpistols.carshare.messages;

public class Agreement implements java.io.Serializable {
    public String agreementId;
    public Decision decision;

    @Override
    public String toString() {
        return "Agreement{" +
                "agreementId='" + agreementId + '\'' +
                ", decision=" + decision +
                '}';
    }
}
