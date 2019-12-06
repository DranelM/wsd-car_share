package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class HandleRequestMessageRespond extends ReceiveMessageBehaviour {
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private ACLMessage msgRequest;
    private int expectedRequestResponds;
    private int requestRespondsCounter;

    public HandleRequestMessageRespond(Agent agent, ACLMessage msgRequest) {
        this(agent, msgRequest, 1);
        this.msgRequest = msgRequest;
    }

    public HandleRequestMessageRespond(Agent agent, ACLMessage msgRequest, int expectedRequestResponds) {
        super(agent);
        this.msgRequest = msgRequest;
        this.expectedRequestResponds = expectedRequestResponds;
        requestRespondsCounter = 0;
    }

    @Override
    protected void parseMessage(ACLMessage msg) throws UnreadableException {
        if (!isCorrectConversationId(msg)) {
            return;
        }

        parseMessagePerformative(msg);
        increaseRequestRespondsCounter(msg);

        if (isReceivedExpectedRequestResponds()) {
            afterReceivingExpectedRequestResponds(msg);
        }
    }

    private boolean isCorrectConversationId(ACLMessage msg) {
        if (Objects.equals(msgRequest.getConversationId(), msg.getConversationId())) {
            return true;
        } else {
            log.debug("conversationId not matched. {} != {}", msgRequest.getConversationId(), msg.getConversationId());
            return false;
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
        myAgent.removeBehaviour(this);
    }
}
