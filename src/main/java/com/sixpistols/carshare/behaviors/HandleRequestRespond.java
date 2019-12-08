package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HandleRequestRespond extends HandleRespond {
    private int expectedRequestResponds;
    private int requestRespondsCounter;

    public HandleRequestRespond(Agent agent, ACLMessage msgRequest) {
        this(agent, msgRequest, 1);
    }

    public HandleRequestRespond(Agent agent, ACLMessage msgRequest, int expectedRequestResponds) {
        super(agent, msgRequest);
        this.expectedRequestResponds = expectedRequestResponds;
        requestRespondsCounter = 0;
    }

    @Override
    protected void action(ACLMessage msg) throws UnreadableException {
        parseMessagePerformative(msg);
        increaseRequestRespondsCounter(msg);

        if (isReceivedExpectedRequestResponds()) {
            afterReceivingExpectedRequestResponds(msg);
        }
    }

    private void parseMessagePerformative(ACLMessage msg) throws UnreadableException {
        switch (msg.getPerformative()) {
            case ACLMessage.REFUSE:
                log.debug("get respond: REFUSE");
                afterRefuse(msg);
                break;
            case ACLMessage.AGREE:
                log.debug("get respond: AGREE");
                afterAgree(msg);
                break;
            case ACLMessage.FAILURE:
                log.debug("get respond: FAILURE");
                afterFailure(msg);
                break;
            case ACLMessage.INFORM:
                log.debug("get respond: INFORM");
                afterInform(msg);
                break;
            case ACLMessage.NOT_UNDERSTOOD:
                log.debug("get respond: INFORM");
        }
    }

    protected void afterRefuse(ACLMessage msg) throws UnreadableException {
    }

    protected void afterAgree(ACLMessage msg) throws UnreadableException {
    }

    protected void afterFailure(ACLMessage msg) throws UnreadableException {
    }

    protected void afterInform(ACLMessage msg) throws UnreadableException {
    }

    private void increaseRequestRespondsCounter(ACLMessage msg) {
        switch (msg.getPerformative()) {
            case ACLMessage.REFUSE:
            case ACLMessage.FAILURE:
            case ACLMessage.INFORM:
            case ACLMessage.NOT_UNDERSTOOD:
                log.debug("increaseRequestRespondsCounter");
                requestRespondsCounter++;
                break;
        }
    }

    private boolean isReceivedExpectedRequestResponds() {
        if (expectedRequestResponds == requestRespondsCounter) {
            log.debug("received expected request responds");
            return true;
        } else {
            return false;
        }
    }

    protected void afterReceivingExpectedRequestResponds(ACLMessage msg) {
        finished();
    }
}
