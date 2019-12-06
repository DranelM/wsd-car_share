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
import java.util.HashMap;
import java.util.List;

public class DriverAgent extends UserAgent {
    HashMap<String, Agreement> agreementMap;
    List<PaymentReport> paymentReports;

    @Override
    protected void setup() {
        super.setup();
        agreementMap = new HashMap<>();
    }

    @Override
    protected void afterLoginSucceeded() {
        addBehaviour(new ReceiveMessages(this));
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
                log.info("post TravelOffer: {}", travelOffer.offerId);
                AID offerDirectorAgent = ServiceUtils.findAgent(myAgent, ServiceType.OfferDirector);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(offerDirectorAgent);
                travelOffer.offerDirectorId = offerDirectorAgent.getName();
                try {
                    msg.setContentObject(travelOffer);
                    send(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ReceiveMessages extends ReceiveMessageBehaviour {
        public ReceiveMessages(Agent a) {
            super(a);
        }

        @Override
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
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

            String agreementId = agreement.agreementId;
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

            String agreementId = cancelAgreementReport.cancelAgreement.agreement.agreementId;
            agreementMap.remove(agreementId);
            log.info("cancel agreement {}", agreementId);
        }
    }

    private class HandlePaymentReport extends OneShotBehaviour {
        ACLMessage request;

        public HandlePaymentReport(Agent a, ACLMessage request) {
            super(a);
            this.request = request;
        }

        public void action() {
            PaymentReport paymentReport;
            try {
                paymentReport = (PaymentReport) request.getContentObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }

            String paymentId = paymentReport.payment.paymentId;
            log.info("get paymentReport {}", paymentId);
            paymentReports.add(paymentReport);
        }
    }
}
