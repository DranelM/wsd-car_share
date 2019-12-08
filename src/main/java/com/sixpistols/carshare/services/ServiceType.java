package com.sixpistols.carshare.services;

public enum ServiceType {
    LegalAdvisor("legal-advisor"),
    OfferDirector("offer-director");

    String type;

    ServiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
