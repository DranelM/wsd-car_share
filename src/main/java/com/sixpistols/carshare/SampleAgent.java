package com.sixpistols.carshare;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class SampleAgent extends Agent {
    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hello! Sample Agent " + getAID().getName() + " is ready.");
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                System.out.println(getAID().getName() + " heartbeat!");
            }
        });

        addBehaviour(new ReceiveMessageBehaviour(this) {
            @Override
            protected void parseMessage(ACLMessage msg) throws UnreadableException {
                System.out.println("Get message " + msg);
            }
        });
    }
}