package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.Coordinate;
import com.sixpistols.carshare.messages.Error;
import com.sixpistols.carshare.messages.MessagesUtils;
import com.sixpistols.carshare.messages.TravelOffer;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

public class DriverAgent extends UserAgent {
    @Override
    protected void afterLoginSucceeded() {
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                postTravelOffer(createTestingTravelOffer());
            }
        });
    }

    @Override
    protected void afterLoginFailed(Error error) {
        log.error("Login failed: {}", error);
    }

    private TravelOffer createTestingTravelOffer() {
        TravelOffer travelOffer = new TravelOffer();
        travelOffer.offerId = MessagesUtils.generateRandomStringByUUIDNoDash();
        travelOffer.driverId = getName();
        travelOffer.coordinateList.add(createTestingCoordinate());
        travelOffer.coordinateList.add(createTestingCoordinate());
        travelOffer.startTime = 1;
        travelOffer.endTime = 4;
        travelOffer.capacity = 4;
        travelOffer.price = 1;
        return travelOffer;
    }

    private Coordinate createTestingCoordinate() {
        Coordinate coordinate = new Coordinate();
        coordinate.x = MessagesUtils.generateRandomInt(0, 5);
        coordinate.y = MessagesUtils.generateRandomInt(0, 5);
        return coordinate;
    }

    private void postTravelOffer(final TravelOffer travelOffer) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("post TravelOffer: {}", travelOffer.offerId);
                List<AID> offerMatcherAgents = ServiceUtils.findAgentList(myAgent, ServiceType.OfferMatcher);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                for (AID agent : offerMatcherAgents) {
                    msg.addReceiver(agent);
                }
                try {
                    msg.setContentObject(travelOffer);
                    send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
