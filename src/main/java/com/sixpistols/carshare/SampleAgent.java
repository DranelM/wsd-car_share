package com.sixpistols.carshare;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;

public class SampleAgent extends Agent {
    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hello! Sample Agent "+getAID().getName()+" is ready.");
        addBehaviour(new TickerBehaviour(this, 1000) {
            protected void onTick() {
                System.out.println(getAID().getName()+" heartbeat!");
            }
        }
        );
    }
}
