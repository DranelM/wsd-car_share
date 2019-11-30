package com.sixpistols.carshare.services;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ServiceUtils {

    public static AID findAgent(Agent agent, ServiceType type) {
        List<AID> agentList = findAgentList(agent, type);
        int size = agentList.size();
        int randomElementIndex = ThreadLocalRandom.current().nextInt(size) % size;
        return agentList.get(randomElementIndex);
    }

    public static List<AID> findAgentList(Agent agent, ServiceType type) {
        System.out.println(agent.getAID().getName() + " seeks for " + type);
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type.getType());
        template.addServices(sd);
        List<AID> agents = new LinkedList<AID>();
        try {
            DFAgentDescription[] result = DFService.search(agent, template);
            for (DFAgentDescription dfAgentDescription : result) {
                AID aid = dfAgentDescription.getName();
                System.out.println(agent.getAID().getName() + " found: " + aid.getName());
                agents.add(aid);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return agents;
    }
}
