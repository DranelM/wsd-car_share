package com.sixpistols.carshare.behaviors;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public abstract class ReceiveMessageBehaviour extends CyclicBehaviour {
    protected final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    private HashMap<String, HandleRespond> respondMap;

    public ReceiveMessageBehaviour(Agent a) {
        super(a);
        respondMap = new HashMap<>();
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg == null) {
            block();
            return;
        }
        parseMessage(msg);
    }

    private void parseMessage(ACLMessage msg) {
        try {
            if (isKnownMessage(msg)) {
                receivedKnownMessage(msg);
            } else if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                log.error("received NOT_UNDERSTOOD messaged with not know conversationId {}", msg.getConversationId());
            } else {
                receivedNewMessage(msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isKnownMessage(ACLMessage msg) {
        return respondMap.containsKey(msg.getConversationId());
    }

    private void receivedKnownMessage(ACLMessage msg) throws UnreadableException {
        HandleRespond handleRespond = respondMap.get(msg.getConversationId());
        handleRespond.action(msg);
        if (handleRespond.isFinished()) {
            unregisterRespond(msg.getConversationId());
        }
    }

    protected abstract void receivedNewMessage(ACLMessage msg) throws UnreadableException;

    public void registerRespond(HandleRespond handleRespond) {
        respondMap.put(handleRespond.getSendMessage().getConversationId(), handleRespond);
    }

    public void unregisterRespond(String conversationId) {
        respondMap.remove(conversationId);
    }

    protected void replyNotUnderstood(ACLMessage msg) {
        log.error("NotUnderstood message with conversationId: {}", msg.getConversationId());
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
