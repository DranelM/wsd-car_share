package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;

public class PassengerAgent extends UserAgent {
    OffersListRespond offersListRespond;
    int remainingOfferMatcherAgentsNumber;

    @Override
    protected void afterLoginSucceeded() {
        offersListRespond = new OffersListRespond();
        remainingOfferMatcherAgentsNumber = 0;

        addBehaviour(new WakerBehaviour(this, 2000) {
            @Override
            protected void onWake() {
                postTravelRequest(createTestingTravelRequest());
            }
        });
    }

    private TravelRequest createTestingTravelRequest() {
        TravelRequest travelRequest = new TravelRequest();
        travelRequest.id = MessagesUtils.generateRandomStringByUUIDNoDash();
        travelRequest.coordinateList.add(createTestingCoordinate());
        travelRequest.coordinateList.add(createTestingCoordinate());
        travelRequest.startTime = 1;
        travelRequest.endTime = 4;
        travelRequest.capacity = 4;
        return travelRequest;
    }

    private Coordinate createTestingCoordinate() {
        Coordinate coordinate = new Coordinate();
        coordinate.x = 1;
        coordinate.y = 2;
        return coordinate;
    }

    private void postTravelRequest(final TravelRequest travelRequest) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getName() + ": post TravelRequest: " + travelRequest.id);
                List<AID> offerMatcherAgents = ServiceUtils.findAgentList(myAgent, ServiceType.OfferMatcher);

                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                remainingOfferMatcherAgentsNumber = offerMatcherAgents.size();
                for (AID agent : offerMatcherAgents) {
                    msg.addReceiver(agent);
                }
                try {
                    msg.setContentObject(travelRequest);
                    send(msg);
                    addBehaviour(new HandleTravelRequestRespond(myAgent));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class HandleTravelRequestRespond extends ReceiveMessageBehaviour {
        public HandleTravelRequestRespond(Agent a) {
            super(a);
        }

        @Override
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();
            OffersList offersList = (OffersList) content;
            System.out.println(getAID().getName() + ": OffersList: " + offersList.toString());
            offersListRespond.addTravelOffer(offersList, msg);
            remainingOfferMatcherAgentsNumber--;

            // get respond from all OfferMatcherAgents
            if (remainingOfferMatcherAgentsNumber <= 0) {
                removeBehaviour(this);
                // after 1s select random offersList
                addBehaviour(new WakerBehaviour(myAgent, 1000) {
                    @Override
                    protected void onWake() {
                        int randomInt = MessagesUtils.generateRandomInt(0, offersListRespond.travelOffers.size() - 1);
                        TravelOfferRespond travelOfferRespond = (TravelOfferRespond) offersListRespond.travelOffers.get(randomInt);
                        acceptTravelOffer(createDecision(travelOfferRespond), travelOfferRespond.msg);
                    }
                });
            }
        }
    }

    private class TravelOfferRespond extends TravelOffer {
        ACLMessage msg;

        public TravelOfferRespond(TravelOffer travelOffer, ACLMessage msg) {
            this.id = travelOffer.id;
            this.coordinateList = travelOffer.coordinateList;
            this.startTime = travelOffer.startTime;
            this.endTime = travelOffer.endTime;
            this.capacity = travelOffer.capacity;
            this.msg = msg;
        }
    }

    private class OffersListRespond extends OffersList {
        public void addTravelOffer(OffersList offersList, ACLMessage msg) {
            for (TravelOffer travelOffer : offersList.travelOffers) {
                this.travelOffers.add(new TravelOfferRespond(travelOffer, msg));
            }
        }
    }

    public Decision createDecision(final TravelOffer travelOffer) {
        Decision decision = new Decision();
        decision.travelOfferId = travelOffer.id;
        decision.startCoordinate = travelOffer.coordinateList.get(0);
        decision.endCoordinate = travelOffer.coordinateList.get(travelOffer.coordinateList.size() - 1);
        decision.space = 1;
        return decision;
    }

    private void acceptTravelOffer(final Decision decision, final ACLMessage msg) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getName() + ": accept TravelRequest: " + decision.travelOfferId);
//                ACLMessage reply = msg.createReply();
//                reply.setPerformative(ACLMessage.PROPOSE);
//                try {
//                    reply.setContentObject(decision);
//                    send(reply);
////                    addBehaviour(new HandleTravelRequestRespond(myAgent));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
