package com.sixpistols.carshare.behaviors;

import com.sixpistols.carshare.messages.Error;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class HandleRequestMessage extends OneShotBehaviour {
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private ACLMessage msgRequest;
    List<AID> notifyAgents;
    Error error;

    public HandleRequestMessage(Agent agent, ACLMessage msgRequest) {
        super(agent);
        this.msgRequest = msgRequest;
        notifyAgents = new LinkedList<>();
        error = new Error("Default error");
    }

    public ACLMessage getMsgRequest() {
        return msgRequest;
    }

    @Override
    public void action() {
        try {
            sendAgree();
            if (doRequestedWork()) {
                sendInform();
            } else {
                sendFailure();
            }
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

    protected abstract Boolean doRequestedWork() throws UnreadableException;

    private void sendInform() throws UnreadableException, IOException {
        log.debug("send respond: INFORM");
        final ACLMessage reply = getMsgRequest().createReply();
        reply.setPerformative(ACLMessage.INFORM);
        notifyAgents.forEach(reply::addReceiver);
        reply.setContentObject(getInformContentObject());
        myAgent.send(reply);

    }

    protected abstract Serializable getInformContentObject() throws UnreadableException;

    private void sendFailure() throws UnreadableException, IOException {
        log.debug("send respond: FAILURE");
        final ACLMessage reply = getMsgRequest().createReply();
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContentObject(error);
        myAgent.send(reply);
    }

    public void setError(Error error) {
        this.error = error;
    }
}
