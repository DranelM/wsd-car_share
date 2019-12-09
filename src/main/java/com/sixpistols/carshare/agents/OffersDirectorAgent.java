package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.HandleRequestMessage;
import com.sixpistols.carshare.behaviors.HandleRequestRespond;
import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.Error;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

            travelOfferMap.put(travelOffer.getTravelOfferId(), travelOffer);
            travelOfferToAgreementMap.put(travelOffer.getTravelOfferId(), new LinkedList<>());
        }
    }

    private class HandleTravelRequest extends HandleRequestMessage {
        OffersList offersList;

        public HandleTravelRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected Boolean doRequestedWork() throws UnreadableException {
            TravelRequest travelRequest = (TravelRequest) getMsgRequest().getContentObject();
            offersList = getOffersList(travelRequest);
            return true;
        }

        @Override
        protected Serializable getInformContentObject() {
            return offersList;
        }
    }
    
    // Zwraca odleg³oœæ od punktu startowego i koñcowego
    private double getDistance(TravelOffer travelOffer,TravelRequest travelRequest) {
    	return Math.round(Math.sqrt(Math.pow(travelOffer.getCoordinateList().get(0).getX()-travelRequest.getCoordinateList().get(0).getX(),2)+Math.pow(travelOffer.getCoordinateList().get(0).getY()-travelRequest.getCoordinateList().get(0).getY(),2))+
    			Math.sqrt(Math.pow(travelOffer.getCoordinateList().get(1).getX()-travelRequest.getCoordinateList().get(1).getX(),2)+Math.pow(travelOffer.getCoordinateList().get(1).getY()-travelRequest.getCoordinateList().get(1).getY(),2)));
    }

    private OffersList getOffersList(TravelRequest travelRequest) {
        log.debug("prepare OffersList");
        OffersList offersList = new OffersList();
        offersList.getTravelOffers().addAll(
                travelOfferMap.values()
                        .stream()
                        .filter(travelOffer -> travelOffer.getStatus() == TravelOffer.Status.ACTIVE)
                        .collect(Collectors.toList())
        );
        Collections.sort(offersList.getTravelOffers(), (a,b)-> (getDistance(a, travelRequest)<getDistance(b, travelRequest)?-1:1));
        int maxReturnedOffers=10;
        if(offersList.getTravelOffers().size()>maxReturnedOffers)
        	offersList.getTravelOffers().subList(maxReturnedOffers, offersList.getTravelOffers().size()).clear();
        return offersList;
    }

    private class HandleDecisionRequest extends HandleRequestMessage {
        Agreement agreement;

        public HandleDecisionRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected Boolean doRequestedWork() throws UnreadableException {
            Decision decision = (Decision) getMsgRequest().getContentObject();
            if (!canAcceptDecision(decision)) {
                return false;
            }

            agreement = createAgreement(decision);

            String driverName = decision.getDriverId();
            AID driverAgent = new AID(driverName, AID.ISGUID);
            addNotifyAgent(driverAgent);
            // Oferta jest finalizowana na koniec przejazdu
            addBehaviour(new WakerBehaviour(OffersDirectorAgent.this,decision.getTravelOffer().getEndTime()) {
            	@Override
            	protected void onWake() {
            		switch(travelOfferMap.get(decision.getOfferId()).getStatus()) {
            		case ACTIVE:
                    case FULL:
                    	finalizeTravelOffer(decision.getTravelOffer().getTravelOfferId());
                        break;
                    case FINISHED:
                        setError(new Error("TravelOffer is FINISHED"));
                        break;
                    case CANCELED:
                        setError(new Error("TravelOffer is already canceled"));
                        break;
            		}
            	}
			});
            return true;
        }

        private boolean canAcceptDecision(Decision decision) {
            switch (travelOfferMap.get(decision.getOfferId()).getStatus()) {
                case ACTIVE:
                    return true;
                case FULL:
                    setError(new Error("TravelOffer is FULL"));
                    return false;
                case FINISHED:
                    setError(new Error("TravelOffer is FINISHED"));
                    return false;
                case CANCELED:
                    setError(new Error("TravelOffer is already canceled"));
                    return false;
            }
            return false;
        }

        @Override
        protected Serializable getInformContentObject() {
            return agreement;
        }
    }

    private Agreement createAgreement(Decision decision) {
        TravelOffer travelOffer = travelOfferMap.get(decision.getOfferId());
        travelOffer.changeCapacity(-1);
        if (travelOffer.getCapacity() == 0) {
            travelOffer.setStatus(TravelOffer.Status.FULL);
        }

        Agreement agreement = new Agreement(
                decision
        );
        agreementMap.put(agreement.getAgreementId(), agreement);
        travelOfferToAgreementMap.get(agreement.getOfferId()).add(agreement.getAgreementId());
        return agreement;
    }

    private class HandleCancelAgreementRequest extends HandleRequestMessage {
        CancelAgreementReport cancelAgreementReport;

        public HandleCancelAgreementRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected Boolean doRequestedWork() throws UnreadableException {
            CancelAgreement cancelAgreement = (CancelAgreement) getMsgRequest().getContentObject();
            if (!canCancelAgreement(cancelAgreement)) {
                return false;
            }

            cancelAgreementReport = createCancelAgreementReport(cancelAgreement);

            String driverName = cancelAgreementReport.getDriverId();
            AID driverAgent = new AID(driverName, AID.ISGUID);
            addNotifyAgent(driverAgent);
            return true;
        }

        private boolean canCancelAgreement(CancelAgreement cancelAgreement) {
            switch (travelOfferMap.get(cancelAgreement.getOfferId()).getStatus()) {
                case ACTIVE:
                case FULL:
                    return true;
                case FINISHED:
                    setError(new Error("TravelOffer is FINISHED"));
                    return false;
                case CANCELED:
                    setError(new Error("TravelOffer is already canceled"));
                    return false;
            }
            return false;
        }

        @Override
        protected Serializable getInformContentObject() {
            return cancelAgreementReport;
        }
    }

    private CancelAgreementReport createCancelAgreementReport(CancelAgreement cancelAgreement) {
        TravelOffer travelOffer = travelOfferMap.get(cancelAgreement.getOfferId());
        travelOffer.changeCapacity(1);
        if (travelOffer.getCapacity() > 0) {
            travelOffer.setStatus(TravelOffer.Status.ACTIVE);
        }

        travelOfferToAgreementMap.get(cancelAgreement.getOfferId()).remove(cancelAgreement.getAgreementId());

        return new CancelAgreementReport(
                cancelAgreement
        );
    }

    private class HandleCancelOfferRequest extends HandleRequestMessage {
        CancelOfferReport cancelOfferReport;

        public HandleCancelOfferRequest(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected Boolean doRequestedWork() throws UnreadableException {
            CancelOffer cancelOffer = (CancelOffer) getMsgRequest().getContentObject();
            if (!canCancelOffer(cancelOffer)) {
                return false;
            }

            cancelOfferReport = createCancelOfferReport(cancelOffer);

            LinkedList<String> agreementIdList = travelOfferToAgreementMap.get(cancelOfferReport.getOfferId());
            for (String agreementId : agreementIdList) {
                Agreement agreement = agreementMap.get(agreementId);
                String passengerName = agreement.getPassengerId();
                AID passengerAgent = new AID(passengerName, AID.ISGUID);
                addNotifyAgent(passengerAgent);
            }
            return true;
        }

        private boolean canCancelOffer(CancelOffer cancelOffer) {
            switch (travelOfferMap.get(cancelOffer.getOfferId()).getStatus()) {
                case ACTIVE:
                case FULL:
                    return true;
                case FINISHED:
                    setError(new Error("TravelOffer is FINISHED"));
                    return false;
                case CANCELED:
                    setError(new Error("TravelOffer is already canceled."));
                    return false;
            }
            return false;
        }

        @Override
        protected Serializable getInformContentObject() {
            return cancelOfferReport;
        }
    }

    private CancelOfferReport createCancelOfferReport(CancelOffer cancelOffer) {
        TravelOffer travelOffer = travelOfferMap.get(cancelOffer.getOfferId());
        travelOffer.setStatus(TravelOffer.Status.CANCELED);
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
