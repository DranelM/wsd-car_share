package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HandleRespond {
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    protected Agent myAgent;
    private ACLMessage sendMessage;
    private Boolean isFinished;

    public HandleRespond(Agent agent, ACLMessage sendMessage) {
        this.myAgent = agent;
        this.sendMessage = sendMessage;
        isFinished = false;
    }

    protected abstract void action(ACLMessage msg) throws UnreadableException;

    public ACLMessage getSendMessage() {
        return sendMessage;
    }

    public void finished() {
        isFinished = true;
    }

    public Boolean isFinished() {
        return isFinished;
    }
}
