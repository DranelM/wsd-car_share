package com.sixpistols.carshare.agents;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class StartAgent extends LoggerAgent {
    AgentContainer agentContainer;

    @Override
    protected void setup() {
        log.info("start");
        agentContainer = getContainerController();
        createNewAgents(LegalAdvisorAgent.class, 1);
        createNewAgents(OffersDirectorAgent.class, 1);
        createNewAgents(DriverAgent.class, 2);
        createNewAgents(PassengerAgent.class, 1);
    }

    private void createNewAgents(Class c, int size) {
        try {
            for (int i = 0; i < size; ++i) {
                String name = c.getSimpleName() + '-' + i;
                String path = c.getName();
                log.info("create agent: " + name + " from " + path);
                AgentController agentController = agentContainer.createNewAgent(name, path, null);
                agentController.start();
            }
        } catch (StaleProxyException ex) {
            log.error(ex.getMessage());
        }
    }
}
