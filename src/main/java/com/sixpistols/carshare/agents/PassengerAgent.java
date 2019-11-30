package com.sixpistols.carshare.agents;

public class PassengerAgent extends UserAgent {

    @Override
    protected void afterLoginSucceeded() {
        System.out.println(getAID().getName() + ": Start");
    }
}
