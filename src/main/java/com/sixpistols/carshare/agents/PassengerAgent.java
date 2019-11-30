package com.sixpistols.carshare.agents;

import jade.core.Agent;

public class PassengerAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
    }
}
