package com.sixpistols.carshare.services;

public enum ServiceType {
    AccountVerifier("account-verifier"),
    OfferDirector("offer-director"),
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
