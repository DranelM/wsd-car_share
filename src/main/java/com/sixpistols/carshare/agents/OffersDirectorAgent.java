package com.sixpistols.carshare.agents;

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

import java.io.IOException;
import java.util.HashMap;

public class OffersDirectorAgent extends LoggerAgent {
    HashMap<String, TravelOffer> travelOfferMap;

    @Override
    protected void setup() {
        log.info("start");
        registerServices();
        travelOfferMap = new HashMap<>();
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
            } else if (content instanceof CancelAgreement) {
                log.debug("get message CancelAgreement");
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

            travelOfferMap.put(travelOffer.offerId, travelOffer);
        }
    }

    private class HandleTravelRequest extends OneShotBehaviour {
        ACLMessage request;

        public HandleTravelRequest(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            TravelRequest travelRequest;
            try {
                travelRequest = (TravelRequest) request.getContentObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            ACLMessage agree = request.createReply();
            agree.setPerformative(ACLMessage.AGREE);
            send(agree);

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            try {
                reply.setContentObject(getOffersList(travelRequest));
                send(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OffersList getOffersList(TravelRequest travelRequest) {
        log.debug("prepare OffersList");
        OffersList offersList = new OffersList();
        offersList.travelOffers.addAll(travelOfferMap.values());
        return offersList;
    }
}
