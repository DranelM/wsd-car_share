package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.BasicHandleRequestMessage;
import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.Serializable;

public class LegalAdvisorAgent extends LoggerAgent {
    @Override
    protected void setup() {
        log.info("start");
        registerServices();

        addBehaviour(new ReceiveMessages(this));
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceLegalAdvisor());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceLegalAdvisor() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.LegalAdvisor.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    @Override
    protected void takeDown() {
        deregisterServices();
        log.info("stop");
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
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (content instanceof NewUserData) {
                addBehaviour(new HandleCreateUser(myAgent, msg));
            } else if (content instanceof UserCredentials) {
                addBehaviour(new HandleLogInUser(myAgent, msg));
            } else if (content instanceof AgreementData) {
                addBehaviour(new HandleAgreementData(myAgent, msg));
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
                reply.setContentObject(createLoginToken(request));
                send(reply);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private LoginToken createLoginToken(ACLMessage msg) {
            return new LoginToken(
                    msg.getSender().getName()
            );
        }
    }

    private class HandleAgreementData extends BasicHandleRequestMessage {
        PaymentReportList paymentReportList;

        public HandleAgreementData(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void beforeInform() throws UnreadableException {
            AgreementData agreementData = (AgreementData) getMsgRequest().getContentObject();
            paymentReportList = createPaymentReportList(agreementData);
        }

        @Override
        protected Serializable getContentObject() {
            return paymentReportList;
        }
    }

    private PaymentReportList createPaymentReportList(AgreementData agreementData) {
        PaymentReportList paymentReportList = new PaymentReportList();
        for (Payment payment : agreementData.getPayments()) {
            paymentReportList.getPaymentReports().add(createPaymentReport(payment));
        }
        return paymentReportList;
    }

    private PaymentReport createPaymentReport(Payment payment) {
        return new PaymentReport(
                payment
        );
    }
}
