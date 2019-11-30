package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import com.sixpistols.carshare.messages.LoginToken;
import com.sixpistols.carshare.messages.UserCredentials;
import com.sixpistols.carshare.services.ServiceType;
import com.sixpistols.carshare.services.ServiceUtils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public abstract class UserAgent extends Agent {
    private AID accountVerifier;
    protected LoginToken loginToken;

    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
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
            accountVerifier = ServiceUtils.findAgent(myAgent, ServiceType.AccountVerifier);

            if (accountVerifier == null) {
                return;
            }
            removeBehaviour(this);

            UserCredentials userCredentials = new UserCredentials();
            userCredentials.login = "login";
            userCredentials.password = "password";
            login(userCredentials);
        }
    }

    private void login(final UserCredentials userCredentials) {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getName() + ": Try to login");
                ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
                msg.addReceiver(accountVerifier);
                try {
                    msg.setContentObject(userCredentials);
                    send(msg);
                    addBehaviour(new HandleLoginRespond(myAgent));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class HandleLoginRespond extends ReceiveMessageBehaviour {
        public HandleLoginRespond(Agent a) {
            super(a);
        }

        @Override
        protected void parseMessage(ACLMessage msg) throws UnreadableException {
            Object content = msg.getContentObject();

            if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                System.out.println(getAID().getName() + ": Login succeeded");
                loginToken = (LoginToken) content;
                afterLoginSucceeded();
            } else if (msg.getPerformative() == ACLMessage.FAILURE) {
                System.out.println(getAID().getName() + ": Login failed");
            }

            removeBehaviour(this);
        }
    }

    protected abstract void afterLoginSucceeded();
}
