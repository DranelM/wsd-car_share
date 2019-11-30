package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.LoginToken;
import com.sixpistols.carshare.messages.NewUserData;
import com.sixpistols.carshare.messages.UserCredentials;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.UUID;

public class LegalAdvisorAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
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

            if (content instanceof NewUserData) {
                addBehaviour(new HandleCreateUser(myAgent, msg));
            } else if (content instanceof UserCredentials) {
                addBehaviour(new HandleLogInUser(myAgent, msg));
            } else {
                replyNotUnderstood(msg);
            }
        }
    }

    private class HandleCreateUser extends OneShotBehaviour {
        ACLMessage request;

        public HandleCreateUser(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            try {
                NewUserData newUserData = (NewUserData) request.getContentObject();
                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
//                reply.setPerformative(ACLMessage.FAILURE);
                send(reply);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class HandleLogInUser extends OneShotBehaviour {
        ACLMessage request;

        public HandleLogInUser(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            try {
                UserCredentials userCredentials = (UserCredentials) request.getContentObject();
                ACLMessage reply = request.createReply();
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
//                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContentObject(createLoginToken());
                send(reply);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private LoginToken createLoginToken() {
            LoginToken loginToken = new LoginToken();
            loginToken.id = generateRandomStringByUUIDNoDash();
            return loginToken;
        }

        public String generateRandomStringByUUIDNoDash() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}
