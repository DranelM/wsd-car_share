package com.sixpistols.carshare.agents;

import com.sixpistols.carshare.services.ServiceType;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class OffersDirectorAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println(getAID().getName() + ": Start");
        registerServices();
    }

    private void registerServices() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(getServiceOfferMatcher());
        dfd.addServices(getServiceAgreementManager());
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private ServiceDescription getServiceOfferMatcher() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.OfferMatcher.getType();
        sd.setType(type);
        sd.setName("Warsaw-" + type);
        return sd;
    }

    private ServiceDescription getServiceAgreementManager() {
        ServiceDescription sd = new ServiceDescription();
        String type = ServiceType.AgreementManager.getType();
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
