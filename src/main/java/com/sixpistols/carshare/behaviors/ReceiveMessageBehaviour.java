package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class ReceiveMessageBehaviour extends TickerBehaviour {
    public ReceiveMessageBehaviour(Agent a, long period) {
        super(a, period);
    }

    public void onTick() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            System.out.println(myAgent.getAID().getName() + " received " + msg.getContent() + " from " + msg.getSender().getName());
        }
    }
}
