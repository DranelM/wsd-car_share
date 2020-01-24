package com.sixpistols.carshare.agents;

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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PassengerAgent extends UserAgent {
    ReceiveMessages receiveMessages;
    private List<OffersList> offersLists;
    Agreement agreement;

    @Override
    protected void setup() {
        super.setup();
        receiveMessages = new ReceiveMessages(this);
        offersLists = new LinkedList<>();
    }

    @Override
    protected void afterLoginSucceeded() {
        addBehaviour(receiveMessages);
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
        TravelRequest travelRequest = new TravelRequest(
                getName(),
                1,
                4
        );
        travelRequest.getCoordinateList().add(MessagesUtils.generateRandomCoordinate());
        travelRequest.getCoordinateList().add(MessagesUtils.generateRandomCoordinate());
        return travelRequest;
    }

    private void postTravelRequest(final TravelRequest travelRequest) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("post TravelRequest: {}", travelRequest.getRequestId());
                List<AID> offerMatcherAgents = ServiceUtils.findAgentList(myAgent, ServiceType.OfferDirector);

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
                offerMatcherAgents.forEach(msg::addReceiver);
                try {
                    msg.setContentObject(travelRequest);
                    send(msg);
                    HandleRequestRespond handleTravelRequestRespond = new HandleTravelRequestRespond(myAgent, msg, offerMatcherAgents.size());
                    receiveMessages.registerRespond(handleTravelRequestRespond);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }

    private class HandleTravelRequestRespond extends HandleRequestRespond {
        public HandleTravelRequestRespond(Agent agent, ACLMessage msgRequest, int expectedRequestResponds) {
            super(agent, msgRequest, expectedRequestResponds);
        }

        @Override
        protected void afterInform(ACLMessage msg) throws UnreadableException {
            OffersList offersList = (OffersList) msg.getContentObject();
            log.debug("get offersList: {}", offersList.toString());
            offersLists.add(offersList);
        }

        @Override
        protected void afterReceivingExpectedRequestResponds(ACLMessage msg) {
            super.afterReceivingExpectedRequestResponds(msg);
            selectRandomTravelOffer();
        }

        private void selectRandomTravelOffer() {
            // after 1s select random offersList
            addBehaviour(new WakerBehaviour(myAgent, 1000) {
                @Override
                protected void onWake() {
                    int randomInt = MessagesUtils.generateRandomInt(0, offersLists.size() - 1);
                    OffersList offersList = offersLists.get(randomInt);
                    randomInt = MessagesUtils.generateRandomInt(0, offersList.getTravelOffers().size() - 1);
                    TravelOffer travelOffer = offersList.getTravelOffers().get(randomInt);
                    acceptTravelOffer(createDecision(travelOffer));
                }
            });
        }
    }

    public Decision createDecision(final TravelOffer travelOffer) {
        return new Decision(
                travelOffer,
                getName(),
                travelOffer.getCoordinateList().get(0),
                travelOffer.getCoordinateList().get(travelOffer.getCoordinateList().size() - 1)
        );
    }

    private void acceptTravelOffer(final Decision decision) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("accept TravelOffer: {}", decision.getOfferId());
                String name = decision.getOfferDirectorId();
                AID offerDirectorAgent = new AID(name, AID.ISGUID);

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
                msg.addReceiver(offerDirectorAgent);
                try {
                    msg.setContentObject(decision);
                    send(msg);
                    HandleRequestRespond handleAcceptTravelOfferRespond = new HandleAcceptTravelOfferRespond(myAgent, msg);
                    receiveMessages.registerRespond(handleAcceptTravelOfferRespond);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }

    private class HandleAcceptTravelOfferRespond extends HandleRequestRespond {
        public HandleAcceptTravelOfferRespond(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void afterInform(ACLMessage msg) throws UnreadableException {
            agreement = (Agreement) msg.getContentObject();
            log.debug("get agreement: {}", agreement.toString());
            log.info("get Agreement {} for TravelOffer: {}", agreement.getAgreementId(), agreement.getOfferId());
            randomlyCancelAgreement();
        }

        @Override
        protected void afterFailure(ACLMessage msg) {
            try {
                Error error = (Error) msg.getContentObject();
                log.info("accept TravelOffer respond failed with message: {}", error.getMessage());
            } catch (UnreadableException e) {
                log.debug("accept TravelOffer respond failed with message that cannot be read", e);
            }
        }
    }

    private void randomlyCancelAgreement() {
        // Zachowanie emulujące anulowanie oferty przez użytkownika
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                // Losowe anulowanie
                if (ThreadLocalRandom.current().nextInt() % 4 == 0 && agreement != null) {
                    cancelAgreement(createCancelAgreement(agreement));
                }
            }
        });
    }

    private class ReceiveMessages extends ReceiveMessageBehaviour {
        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (content instanceof CancelOfferReport) {
                log.debug("get message CancelOfferReport");
                addBehaviour(new HandleCancelOfferReport(myAgent, msg));
            } else if (content instanceof PaymentReport) {
                log.debug("get message PaymentReport");
                addBehaviour(new HandlePaymentReport(myAgent, msg));
            } else {
                replyNotUnderstood(msg);
            }
        }
    }

    private void cancelAgreement(final CancelAgreement cancelAgreement) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("cancel Agreement for TravelOffer: {}", cancelAgreement.getOfferId());
                String offerDirectorName = cancelAgreement.getOfferDirectorId();
                AID offerDirectorAgent = new AID(offerDirectorName, AID.ISGUID);

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
                msg.addReceiver(offerDirectorAgent);
                try {
                    msg.setContentObject(cancelAgreement);
                    send(msg);
                    HandleRequestRespond handleCancelAgreementRespond = new HandleCancelAgreementRespond(myAgent, msg);
                    receiveMessages.registerRespond(handleCancelAgreementRespond);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }

    public CancelAgreement createCancelAgreement(final Agreement agreement) {
        return new CancelAgreement(
                agreement
        );
    }

    private class HandleCancelOfferReport extends OneShotBehaviour {
        ACLMessage request;

        public HandleCancelOfferReport(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            CancelOfferReport cancelOfferReport;
            try {
                cancelOfferReport = (CancelOfferReport) request.getContentObject();
            } catch (Exception ex) {
                log.error(ex.getMessage());
                return;
            }

            log.info("cancel TravelOffer {} by driver", cancelOfferReport.getOfferId());
            agreement = null;
        }
    }

    private class HandleCancelAgreementRespond extends HandleRequestRespond {
        public HandleCancelAgreementRespond(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void afterInform(ACLMessage msg) throws UnreadableException {
            CancelAgreementReport cancelAgreementReport = (CancelAgreementReport) msg.getContentObject();
            log.debug("get cancelAgreementReport: {}", cancelAgreementReport.getAgreementId());
            agreement = null;
        }
    }
}