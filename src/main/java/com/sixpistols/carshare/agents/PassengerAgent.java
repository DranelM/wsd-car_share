package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.Coordinate;
import com.sixpistols.carshare.messages.MessagesUtils;
import com.sixpistols.carshare.messages.OffersList;
import com.sixpistols.carshare.messages.TravelRequest;
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

public class PassengerAgent extends UserAgent {

    @Override
    protected void afterLoginSucceeded() {
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
        travelRequest.coordinateList = new LinkedList<>();
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
        }
    }
}
