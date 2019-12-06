package com.sixpistols.carshare.behaviors;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class BasicHandleRequestMessage extends HandleRequestMessage {
    List<AID> notifyAgents;

    public BasicHandleRequestMessage(Agent agent, ACLMessage msgRequest) {
        super(agent, msgRequest);
        notifyAgents = new LinkedList<>();
    }

    @Override
    public void action() {
        try {
            sendAgree();
            beforeInform();
            sendInform();
        } catch (UnreadableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addNotifyAgent(AID agent) {
        notifyAgents.add(agent);
    }

    private void sendAgree() {
        log.debug("send respond: AGREE");
        ACLMessage agree = getMsgRequest().createReply();
        agree.setPerformative(ACLMessage.AGREE);
        myAgent.send(agree);
    }

    protected abstract void beforeInform() throws UnreadableException;

    private void sendInform() throws UnreadableException, IOException {
        log.debug("send respond: INFORM");
        final ACLMessage reply = getMsgRequest().createReply();
        reply.setPerformative(ACLMessage.INFORM);
        notifyAgents.forEach(reply::addReceiver);
        reply.setContentObject(getContentObject());
        myAgent.send(reply);

    }

    protected abstract Serializable getContentObject() throws UnreadableException;
}
