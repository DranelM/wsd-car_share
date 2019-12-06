package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HandleRequestMessage extends OneShotBehaviour {
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private ACLMessage msgRequest;

    public HandleRequestMessage(Agent agent, ACLMessage msgRequest) {
        super(agent);
        this.msgRequest = msgRequest;
    }

    public ACLMessage getMsgRequest() {
        return msgRequest;
    }
}
