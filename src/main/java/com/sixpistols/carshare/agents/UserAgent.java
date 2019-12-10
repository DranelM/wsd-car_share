package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.Error;
import com.sixpistols.carshare.messages.*;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class UserAgent extends LoggerAgent {
    private AID accountVerifier;
    protected LoginToken loginToken;
    private List<PaymentReport> paymentReports;

    @Override
    protected void setup() {
        log.info("start");
        paymentReports = new LinkedList<>();
        addBehaviour(new FindAccountVerifierAgent(this, 1000));
    }

    public class FindAccountVerifierAgent extends TickerBehaviour {
        long timeout;

        public FindAccountVerifierAgent(Agent a, long timeout) {
            super(a, timeout);
            this.timeout = timeout;
        }

        @Override
        protected void onTick() {
            accountVerifier = ServiceUtils.findAgent(myAgent, ServiceType.LegalAdvisor);

            if (accountVerifier == null) {
                return;
            }
            removeBehaviour(this);
            login(createUserCredentials());
        }
    }

    @NotNull
    private UserCredentials createUserCredentials() {
        return new UserCredentials(
                "login",
                "password"
        );
    }

    private void login(final UserCredentials userCredentials) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                log.debug("Try to login");
                ACLMessage msg = MessagesUtils.createMessage(ACLMessage.PROPOSE);
                msg.addReceiver(accountVerifier);
                try {
                    msg.setContentObject(userCredentials);
                    send(msg);
                    addBehaviour(new HandleLoginRespond(myAgent, msg.getConversationId()));
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
            }
        });
    }

    private class HandleLoginRespond extends ReceiveMessageBehaviour {
        String conversationId;

        public HandleLoginRespond(Agent a, String conversationId) {
            super(a);
            this.conversationId = conversationId;
        }

        @Override
        protected void receivedNewMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (!Objects.equals(conversationId, msg.getConversationId())) {
                log.error("conversationId not matched. {} != {}", this.conversationId, msg.getConversationId());
                return;
            }

            if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                log.info("Login succeeded");
                loginToken = (LoginToken) content;
                afterLoginSucceeded();
            } else if (msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                log.info("Login failed");
                Error error = (Error) content;
                afterLoginFailed(error);
            } else {
                log.error("Not understand message: {}", msg);
            }

            removeBehaviour(this);
        }
    }

    protected abstract void afterLoginSucceeded();

    protected abstract void afterLoginFailed(Error error);

    protected class HandlePaymentReport extends OneShotBehaviour {
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
                log.error(ex.getMessage());
                return;
            }

            String paymentId = paymentReport.getPaymentId();
            log.info("get paymentReport {}", paymentId);
            paymentReports.add(paymentReport);
        }
    }
}
