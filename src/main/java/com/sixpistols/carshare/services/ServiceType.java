package com.sixpistols.carshare.services;

public enum ServiceType {
    AccountVerifier("account-verifier");

    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
