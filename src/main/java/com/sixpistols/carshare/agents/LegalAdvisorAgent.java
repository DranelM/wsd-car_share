package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.behaviors.ReceiveMessageBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class LegalAdvisorAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Cześć, tu LegalAdvisorAgent: " + getAID().getName() + " się zalogował.");
        registerServices();

        addBehaviour(new ReceiveMessageBehaviour(this, 2000));
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceCreateAccount());
        dfd.addServices(getServiceAuthorize());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceCreateAccount() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AccountVerifierCreateAccount.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    private ServiceDescription getServiceAuthorize() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AccountVerifierAuthorize.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    @Override
    protected void takeDown() {
        deregisterServices();
        System.out.println(getAID().getName() + " : zawijam sie stad, narka");
    }

    private void deregisterServices() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
