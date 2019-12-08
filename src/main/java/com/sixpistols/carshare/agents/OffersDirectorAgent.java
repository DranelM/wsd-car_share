package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.BasicHandleRequestMessage;
import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.Serializable;
import java.util.HashMap;

public class OffersDirectorAgent extends LoggerAgent {
    HashMap<String, TravelOffer> travelOfferMap;
    HashMap<String, Agreement> agreementMap;

    @Override
    protected void setup() {
        log.info("start");
        registerServices();
        travelOfferMap = new HashMap<>();
        agreementMap = new HashMap<>();
        addBehaviour(new ReceiveMessages(this));
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceOfferMatcher());
        dfd.addServices(getServiceAgreementManager());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceOfferMatcher() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.OfferDirector.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    private ServiceDescription getServiceAgreementManager() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AgreementManager.getType();
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
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
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
        return new CancelAgreementReport(
                cancelAgreement
        );
    }
}
