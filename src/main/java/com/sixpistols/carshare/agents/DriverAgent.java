package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class DriverAgent extends Agent {
    AID[] currentOffersDirectors;
    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Cześć, tu kierowca: "+getAID().getName()+" się zalogował.");

        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                System.out.println(myAgent.getAID().getName() + " seeks for offers directors");
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("offers-director");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    AID[] offersDirectors = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        offersDirectors[i] = result[i].getName();
                        System.out.println(myAgent.getAID().getName() + " found director: " + offersDirectors[i].getName());
                    }
                    currentOffersDirectors = offersDirectors;
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                if (currentOffersDirectors == null) return;

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                for(AID offerDirector : currentOffersDirectors){
                    msg.addReceiver(offerDirector);
                }
                msg.setContent("Jarzyna miał urodziny, jadę z Wawra na Młociny ;)");
                send(msg);
            }
        });

        addBehaviour(new ReceiveMessageBehaviour(this, 2000));
    }
}
