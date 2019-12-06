package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.Error;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PassengerAgent extends UserAgent {
    List<OffersList> offersLists;
    int remainingOfferMatcherAgentsNumber;

    @Override
    protected void afterLoginSucceeded() {
        offersLists = new LinkedList<>();
        remainingOfferMatcherAgentsNumber = 0;

        addBehaviour(new WakerBehaviour(this, 2000) {
            @Override
            protected void onWake() {
                postTravelRequest(createTestingTravelRequest());
            }
        });
    }

    @Override
    protected void afterLoginFailed(Error error) {
        log.error("Login failed: {}", error);
    }

    private TravelRequest createTestingTravelRequest() {
        TravelRequest travelRequest = new TravelRequest();
        travelRequest.requestId = MessagesUtils.generateRandomStringByUUIDNoDash();
        travelRequest.passengerId = getName();
        travelRequest.coordinateList.add(createTestingCoordinate());
        travelRequest.coordinateList.add(createTestingCoordinate());
        travelRequest.startTime = 1;
        travelRequest.endTime = 4;
        return travelRequest;
    }

    private Coordinate createTestingCoordinate() {
        Coordinate coordinate = new Coordinate();
        coordinate.x = MessagesUtils.generateRandomInt(0, 5);
        coordinate.y = MessagesUtils.generateRandomInt(0, 5);
        return coordinate;
    }

    private void postTravelRequest(final TravelRequest travelRequest) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("post TravelRequest: {}", travelRequest.requestId);
                List<AID> offerMatcherAgents = ServiceUtils.findAgentList(myAgent, ServiceType.OfferMatcher);
                remainingOfferMatcherAgentsNumber = offerMatcherAgents.size();

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setConversationId(MessagesUtils.generateRandomStringByUUIDNoDash());
                for (AID agent : offerMatcherAgents) {
                    msg.addReceiver(agent);
                }
                try {
                    msg.setContentObject(travelRequest);
                    send(msg);
                    addBehaviour(new HandleTravelRequestRespond(myAgent, msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class HandleTravelRequestRespond extends ReceiveMessageBehaviour {
        ACLMessage msgRequest;

        public HandleTravelRequestRespond(Agent a, ACLMessage msgRequest) {
            super(a);
            this.msgRequest = msgRequest;
        }

        @Override
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
            if (!Objects.equals(msgRequest.getConversationId(), msg.getConversationId())) {
                log.debug("conversationId not matched. {} != {}", msgRequest.getConversationId(), msg.getConversationId());
                return;
            }

            if (msg.getPerformative() == ACLMessage.REFUSE) {
                log.debug("TravelRequest: REFUSE");
                remainingOfferMatcherAgentsNumber--;
                removeBehaviour(this);
                return;
            } else if (msg.getPerformative() == ACLMessage.AGREE) {
                log.debug("TravelRequest: AGREE");
                return;
            } else if (msg.getPerformative() == ACLMessage.FAILURE) {
                log.debug("TravelRequest: FAILURE");
                remainingOfferMatcherAgentsNumber--;
                removeBehaviour(this);
                return;
            }

            Object content = msg.getContentObject();
            OffersList offersList = (OffersList) content;
            log.debug("OffersList: {}", offersList.toString());
            offersLists.add(offersList);
            remainingOfferMatcherAgentsNumber--;

            // get respond from all OfferMatcherAgents
            if (remainingOfferMatcherAgentsNumber <= 0) {
                removeBehaviour(this);
                selectRandomTravelOffer();
            }
        }

        private void selectRandomTravelOffer() {
            // after 1s select random offersList
            addBehaviour(new WakerBehaviour(myAgent, 1000) {
                @Override
                protected void onWake() {
                    int randomInt = MessagesUtils.generateRandomInt(0, offersLists.size() - 1);
                    OffersList offersList = offersLists.get(randomInt);
                    randomInt = MessagesUtils.generateRandomInt(0, offersList.travelOffers.size() - 1);
                    TravelOffer travelOffer = offersList.travelOffers.get(randomInt);
                    acceptTravelOffer(createDecision(travelOffer));
                }
            });
        }
    }

    public Decision createDecision(final TravelOffer travelOffer) {
        Decision decision = new Decision();
        decision.travelOffer = travelOffer;
        decision.passengerId = getName();
        decision.startCoordinate = travelOffer.coordinateList.get(0);
        decision.endCoordinate = travelOffer.coordinateList.get(travelOffer.coordinateList.size() - 1);
        return decision;
    }

    private void acceptTravelOffer(final Decision decision) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("accept travelOffer: {}", decision.travelOffer.offerId);
            }
        });
    }
}
