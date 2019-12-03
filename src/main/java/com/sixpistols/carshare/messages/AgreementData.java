package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class AgreementData implements java.io.Serializable {
    public List<Payment> payments;

    public AgreementData() {
        this.payments = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "AgreementData{" +
                "payments=" + payments +
                '}';
    }
}
