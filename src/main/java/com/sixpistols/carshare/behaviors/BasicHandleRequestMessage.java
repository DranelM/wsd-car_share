package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;

public abstract class BasicHandleRequestMessage extends HandleRequestMessage {
    public BasicHandleRequestMessage(Agent agent, ACLMessage msgRequest) {
        super(agent, msgRequest);
    }

    @Override
    public void action() {
        sendAgree();
        sendInform();
    }

    private void sendAgree() {
        log.debug("send respond: AGREE");
        ACLMessage agree = getMsgRequest().createReply();
        agree.setPerformative(ACLMessage.AGREE);
        myAgent.send(agree);
    }

    private void sendInform() {
        log.debug("send respond: INFORM");
        ACLMessage reply = getMsgRequest().createReply();
        reply.setPerformative(ACLMessage.INFORM);
        try {
            reply.setContentObject(getContentObject());
            myAgent.send(reply);
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Serializable getContentObject() throws UnreadableException;
}
