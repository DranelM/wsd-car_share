package com.sixpistols.carshare.agents;

public enum ServiceType {
    AccountVerifierCreateAccount("account-verifier-create-account"),
    AccountVerifierAuthorize("account-verifier-authorize");

    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
