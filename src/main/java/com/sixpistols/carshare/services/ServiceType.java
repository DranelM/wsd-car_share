package com.sixpistols.carshare.services;

public enum ServiceType {
    AccountVerifier("account-verifier"),
    OfferMatcher("offer-matcher"),
    AgreementManager("agreement-manager"),
    PaymentExecutor("payment-executor");

    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
