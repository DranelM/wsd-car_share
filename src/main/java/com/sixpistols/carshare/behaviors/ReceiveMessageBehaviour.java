package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class ReceiveMessageBehaviour extends CyclicBehaviour {
    public ReceiveMessageBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
        try {
            parseMessage(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void parseMessage(ACLMessage msg) throws UnreadableException {
        System.out.println(myAgent.getAID().getName() +
                ": received " + msg.getContent() +
                " from " + msg.getSender().getName());
    }

    protected void replyNotUnderstood(ACLMessage msg) {
        try {
            java.io.Serializable content = msg.getContentObject();
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            reply.setContentObject(content);
            myAgent.send(reply);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
