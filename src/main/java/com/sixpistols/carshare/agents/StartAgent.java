package com.sixpistols.carshare.agents;

import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class StartAgent extends Agent {
    AgentContainer agentContainer;

    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
        agentContainer = getContainerController();
        createNewAgents(LegalAdvisorAgent.class, 1);
        createNewAgents(OffersDirectorAgent.class, 1);
        createNewAgents(DriverAgent.class, 1);
        createNewAgents(PassengerAgent.class, 1);
    }

    private void createNewAgents(Class c, int size) {
        try {
            for (int i = 0; i < size; ++i) {
                String name = c.getSimpleName() + '-' + i;
                String path = c.getName();
                System.out.println(getAID().getName() + ": create agent: " + name + ", from " + path);
                AgentController agentController = agentContainer.createNewAgent(name, path, null);
                agentController.start();
            }
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
