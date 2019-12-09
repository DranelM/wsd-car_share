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
import java.util.HashMap;

public class DriverAgent extends UserAgent {
    ReceiveMessages receiveMessages;
    HashMap<String, Agreement> agreementMap;
    
    // Obecna oferta kierowcy - dodane napotrzeby testowania anulowania oferty
    private TravelOffer currentTravelOffer;

    @Override
    protected void setup() {
        super.setup();
        receiveMessages = new ReceiveMessages(this);
        agreementMap = new HashMap<>();
    }

    @Override
    protected void afterLoginSucceeded() {
        addBehaviour(receiveMessages);
        // Zachowanie emuluj¹ce tworzenie oferty przez u¿ytkownika
        addBehaviour(new WakerBehaviour(this, 1000) {
            @Override
            protected void onWake() {
                postTravelOffer(createTestingTravelOffer());
            }
        });
        // Zachowanie emuluj¹ce anulowanie oferty przez u¿ytkownika
        addBehaviour(new WakerBehaviour(this, 10000) {
            @Override
            protected void onWake() {
            	cancelOffer(createCancelOffer(currentTravelOffer));
            }
        });
    }

    @Override
    protected void afterLoginFailed(Error error) {
        log.error("Login failed: {}", error);
    }

    private TravelOffer createTestingTravelOffer() {
        AID offerDirectorAgent = ServiceUtils.findAgent(this, ServiceType.OfferDirector);
        TravelOffer travelOffer = new TravelOffer(
                getName(),
                offerDirectorAgent.getName(),
                1,
                4,
                4,
                1
        );
        travelOffer.getCoordinateList().add(MessagesUtils.generateRandomCoordinate());
        travelOffer.getCoordinateList().add(MessagesUtils.generateRandomCoordinate());
        currentTravelOffer=travelOffer;
        return travelOffer;
    }

    private class ReceiveMessages extends ReceiveMessageBehaviour {
        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (content instanceof Agreement) {
                log.debug("get message Agreement");
                addBehaviour(new HandleAgreement(myAgent, msg));
            } else if (content instanceof CancelAgreementReport) {
                log.debug("get message CancelAgreementReport");
                addBehaviour(new HandleCancelAgreementReport(myAgent, msg));
            } else if (content instanceof PaymentReport) {
                log.debug("get message PaymentReport");
                addBehaviour(new HandlePaymentReport(myAgent, msg));
            } else {
                replyNotUnderstood(msg);
            }
        }
    }

    private class HandleAgreement extends OneShotBehaviour {
        ACLMessage request;

        public HandleAgreement(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            Agreement agreement;
            try {
                agreement = (Agreement) request.getContentObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            String agreementId = agreement.getAgreementId();
            agreementMap.put(agreementId, agreement);
            log.info("get agreement {}", agreementId);
        }
    }

    private class HandleCancelAgreementReport extends OneShotBehaviour {
        ACLMessage request;

        public HandleCancelAgreementReport(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            CancelAgreementReport cancelAgreementReport;
            try {
                cancelAgreementReport = (CancelAgreementReport) request.getContentObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            String agreementId = cancelAgreementReport.getAgreementId();
            agreementMap.remove(agreementId);
            log.info("cancel agreement {} by passenger", agreementId);
        }
    }

    public CancelOffer createCancelOffer(final TravelOffer travelOffer) {
        return new CancelOffer(
                travelOffer
        );
    }

    private void cancelOffer(final CancelOffer cancelOffer) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("cancel TravelOffer: {}", cancelOffer.getOfferId());
                String offerDirectorName = cancelOffer.getOfferDirectorId();
                AID offerDirectorAgent = new AID(offerDirectorName, AID.ISGUID);

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.REQUEST);
                msg.addReceiver(offerDirectorAgent);
                try {
                    msg.setContentObject(cancelOffer);
                    send(msg);
                    HandleRequestRespond handleCancelOfferRespond = new HandleCancelOfferRespond(myAgent, msg);
                    receiveMessages.registerRespond(handleCancelOfferRespond);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class HandleCancelOfferRespond extends HandleRequestRespond {
        public HandleCancelOfferRespond(Agent agent, ACLMessage msgRequest) {
            super(agent, msgRequest);
        }

        @Override
        protected void afterInform(ACLMessage msg) throws UnreadableException {
            CancelOfferReport cancelOfferReport = (CancelOfferReport) msg.getContentObject();
            log.debug("get CancelOfferReport: {}", cancelOfferReport.getOfferId());
        }
    }

    private void postTravelOffer(final TravelOffer travelOffer) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("post TravelOffer: {}", travelOffer.getTravelOfferId());

                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.INFORM);
                AID offerDirectorAgent = new AID(travelOffer.getOfferDirectorId(), AID.ISGUID);
                msg.addReceiver(offerDirectorAgent);
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
