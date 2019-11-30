package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.OffersList;
import com.sixpistols.carshare.messages.TravelOffer;
import com.sixpistols.carshare.messages.TravelRequest;
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

public class OffersDirectorAgent extends Agent {
    HashMap<String, TravelOffer> travelOfferMap;

    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
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
        String type = ServiceType.OfferMatcher.getType();
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

            if (content instanceof TravelOffer) {
                addBehaviour(new HandleTravelOffer(myAgent, msg));
            } else if (content instanceof TravelRequest) {
                addBehaviour(new HandleTravelRequest(myAgent, msg));
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

            switch (request.getPerformative()) {
                case ACLMessage.INFORM:
                    travelOfferMap.put(travelOffer.id, travelOffer);
                case ACLMessage.CANCEL:
                    travelOfferMap.remove(travelOffer.id);
            }
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

            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);
            try {
                reply.setContentObject(getOffersList(travelRequest));
                send(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private OffersList getOffersList(TravelRequest travelRequest) {
        OffersList offersList = new OffersList();
        offersList.travelOffers = travelOfferMap.values();
        return offersList;
    }
}
