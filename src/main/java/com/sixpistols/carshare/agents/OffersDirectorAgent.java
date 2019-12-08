package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.BasicHandleRequestMessage;
import com.sixpistols.carshare.behaviors.HandleRequestRespond;
import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class OffersDirectorAgent extends LoggerAgent {
    ReceiveMessages receiveMessages;
    HashMap<String, TravelOffer> travelOfferMap;
    HashMap<String, Agreement> agreementMap;
    HashMap<String, LinkedList<String>> travelOfferToAgreementMap;

    @Override
    protected void setup() {
        log.info("start");
        registerServices();
        receiveMessages = new ReceiveMessages(this);
        travelOfferMap = new HashMap<>();
        agreementMap = new HashMap<>();
        travelOfferToAgreementMap = new HashMap<>();
        addBehaviour(receiveMessages);
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceOfferDirector());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceOfferDirector() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.OfferDirector.getType();
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

            if (content instanceof TravelOffer) {
                log.debug("get message TravelOffer");
                addBehaviour(new HandleTravelOffer(myAgent, msg));
            } else if (content instanceof TravelRequest) {
                log.debug("get message TravelRequest");
                addBehaviour(new HandleTravelRequest(myAgent, msg));
            } else if (content instanceof Decision) {
                log.debug("get message Decision");
                addBehaviour(new HandleDecisionRequest(myAgent, msg));
            } else if (content instanceof CancelAgreement) {
                log.debug("get message CancelAgreement");
                addBehaviour(new HandleCancelAgreementRequest(myAgent, msg));
            } else if (content instanceof CancelOffer) {
                log.debug("get message CancelOffer");
                addBehaviour(new HandleCancelOfferRequest(myAgent, msg));
            } else {
                replyNotUnderstood(msg);
            }
        }
    }

    private class HandleTravelOffer extends OneShotBehaviour {
        ACLMessage request;

        public HandleTravelOffer(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            TravelOffer travelOffer;
            try {
                travelOffer = (TravelOffer) request.getContentObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            travelOfferMap.put(travelOffer.getOfferId(), travelOffer);
            travelOfferToAgreementMap.put(travelOffer.getOfferId(), new LinkedList<>());
        }
    }

    private class HandleTravelRequest extends BasicHandleRequestMessage {
        OffersList offersList;

        public HandleTravelRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void beforeInform() throws UnreadableException {
            TravelRequest travelRequest = (TravelRequest) getMsgRequest().getContentObject();
            offersList = getOffersList(travelRequest);
        }

        @Override
        protected Serializable getContentObject() {
            return offersList;
        }
    }

    private OffersList getOffersList(TravelRequest travelRequest) {
        log.debug("prepare OffersList");
        OffersList offersList = new OffersList();
        offersList.getTravelOffers().addAll(travelOfferMap.values());
        return offersList;
    }

    private class HandleDecisionRequest extends BasicHandleRequestMessage {
        Agreement agreement;

        public HandleDecisionRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void beforeInform() throws UnreadableException {
            Decision decision = (Decision) getMsgRequest().getContentObject();
            agreement = createAgreement(decision);

            String driverName = decision.getDriverId();
            AID driverAgent = new AID(driverName, AID.ISGUID);
            addNotifyAgent(driverAgent);
        }

        @Override
        protected Serializable getContentObject() {
            return agreement;
        }
    }

    private Agreement createAgreement(Decision decision) {
        Agreement agreement = new Agreement(
                decision
        );
        agreementMap.put(agreement.getAgreementId(), agreement);
        travelOfferToAgreementMap.get(agreement.getOfferId()).add(agreement.getAgreementId());
        return agreement;
    }

    private class HandleCancelAgreementRequest extends BasicHandleRequestMessage {
        CancelAgreementReport cancelAgreementReport;

        public HandleCancelAgreementRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void beforeInform() throws UnreadableException {
            CancelAgreement cancelAgreement = (CancelAgreement) getMsgRequest().getContentObject();
            cancelAgreementReport = createCancelAgreementReport(cancelAgreement);

            String driverName = cancelAgreementReport.getDriverId();
            AID driverAgent = new AID(driverName, AID.ISGUID);
            addNotifyAgent(driverAgent);
        }

        @Override
        protected Serializable getContentObject() {
            return cancelAgreementReport;
        }
    }

    private CancelAgreementReport createCancelAgreementReport(CancelAgreement cancelAgreement) {
        travelOfferToAgreementMap.get(cancelAgreement.getOfferId()).remove(cancelAgreement.getAgreementId());
        return new CancelAgreementReport(
                cancelAgreement
        );
    }

    private class HandleCancelOfferRequest extends BasicHandleRequestMessage {
        CancelOfferReport cancelOfferReport;

        public HandleCancelOfferRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void beforeInform() throws UnreadableException {
            CancelOffer cancelOffer = (CancelOffer) getMsgRequest().getContentObject();
            cancelOfferReport = createCancelOfferReport(cancelOffer);

            LinkedList<String> agreementIdList = travelOfferToAgreementMap.get(cancelOfferReport.getOfferId());
            for (String agreementId : agreementIdList) {
                Agreement agreement = agreementMap.get(agreementId);
                String passengerName = agreement.getPassengerId();
                AID passengerAgent = new AID(passengerName, AID.ISGUID);
                addNotifyAgent(passengerAgent);
            }
        }

        @Override
        protected Serializable getContentObject() {
            return cancelOfferReport;
        }
    }

    private CancelOfferReport createCancelOfferReport(CancelOffer cancelOffer) {
        travelOfferMap.remove(cancelOffer.getOfferId());
        return new CancelOfferReport(
                cancelOffer
        );
    }

    public void finalizeTravelOffer(String travelOfferId) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("finalize TravelOffer: {}", travelOfferId);

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
                AID legalAdvisorAgent = ServiceUtils.findAgent(myAgent, ServiceType.LegalAdvisor);
                msg.addReceiver(legalAdvisorAgent);
                try {
                    msg.setContentObject(createAgreementData(travelOfferId));
                    send(msg);
                    HandleRequestRespond handleCancelAgreementRespond = new HandleAgreementDataRespond(myAgent, msg);
                    receiveMessages.registerRespond(handleCancelAgreementRespond);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private AgreementData createAgreementData(String offerId) {
        AgreementData agreementData = new AgreementData();
        for (String agreementId : travelOfferToAgreementMap.get(offerId)) {
            Agreement agreement = agreementMap.get(agreementId);
            agreementData.getPayments().add(createPayment(agreement));
        }
        return agreementData;
    }

    private Payment createPayment(Agreement agreement) {
        return new Payment(
                agreement.getPassengerId(),
                1,
                Payment.payment
        );
    }

    private class HandleAgreementDataRespond extends HandleRequestRespond {
        PaymentReportList paymentReportList;

        public HandleAgreementDataRespond(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void afterInform(ACLMessage msg) throws UnreadableException {
            paymentReportList = (PaymentReportList) msg.getContentObject();
            log.debug("get PaymentReportList: {}", paymentReportList);
        }

        @Override
        protected void afterReceivingExpectedRequestResponds(ACLMessage msg) {
            super.afterReceivingExpectedRequestResponds(msg);
            for (PaymentReport paymentReport : paymentReportList.getPaymentReports()) {
                sendPaymentReport(paymentReport);
            }
        }
    }

    private void sendPaymentReport(final PaymentReport paymentReport) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("send PaymentReport {} to: {}", paymentReport.getPaymentId(), paymentReport.getUserId());

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.INFORM);
                AID userAgent = new AID(paymentReport.getUserId(), AID.ISGUID);
                msg.addReceiver(userAgent);
                try {
                    msg.setContentObject(paymentReport);
                    send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
