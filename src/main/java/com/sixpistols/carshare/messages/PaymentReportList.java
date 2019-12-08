package com.sixpistols.carshare.messages;

import java.util.LinkedList;
import java.util.List;

public class PaymentReportList implements java.io.Serializable {
    private List<PaymentReport> paymentReports;

    public PaymentReportList() {
        this.paymentReports = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "PaymentReportList{" +
                "paymentReports=" + paymentReports +
                '}';
    }

    public List<PaymentReport> getPaymentReports() {
        return paymentReports;
    }
}
