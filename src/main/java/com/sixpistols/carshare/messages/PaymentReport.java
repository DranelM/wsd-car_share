package com.sixpistols.carshare.messages;

public class PaymentReport implements java.io.Serializable {
    private Payment payment;

    public PaymentReport(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PaymentReport{" +
                "payment=" + payment +
                '}';
    }

    public Payment getPayment() {
        return payment;
    }

    public String getPaymentId() {
        return payment.getPaymentId();
    }
}
