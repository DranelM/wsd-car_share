package com.sixpistols.carshare.agents;

import jade.core.Agent;

public class StartAgent extends Agent {

    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Cześć, tu agent startowy: " + getAID().getName() + " się zalogował.");

    }

}
