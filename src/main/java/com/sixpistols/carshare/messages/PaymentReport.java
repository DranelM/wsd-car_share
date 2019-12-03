package com.sixpistols.carshare.messages;

public class PaymentReport implements java.io.Serializable {
    public Payment payment;

    @Override
    public String toString() {
        return "PaymentReport{" +
                "payment=" + payment +
                '}';
    }
}
