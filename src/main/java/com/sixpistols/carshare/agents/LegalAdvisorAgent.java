package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.Serializable;

public class LegalAdvisorAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Cześć, tu LegalAdvisorAgent: " + getAID().getName() + " się zalogował.");
        registerServices();

        addBehaviour(new ReceiveMessages(this));
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceCreateAccount());
        dfd.addServices(getServiceAuthorize());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceCreateAccount() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AccountVerifierCreateAccount.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    private ServiceDescription getServiceAuthorize() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AccountVerifierAuthorize.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    @Override
    protected void takeDown() {
        deregisterServices();
        System.out.println(getAID().getName() + " : zawijam sie stad, narka");
    }

    private void deregisterServices() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class ReceiveMessages extends ReceiveMessageBehaviour {
        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();
            replyNotUnderstood(msg);
        }
    }

    private class CreateAccount extends OneShotBehaviour {
        ACLMessage request;

        public CreateAccount(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            try {
                Serializable op = (Serializable) request.getContentObject();
                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContentObject(op);
                send(reply);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
