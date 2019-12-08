package com.sixpistols.carshare.messages;

import jade.lang.acl.ACLMessage;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MessagesUtils {
    static public ACLMessage createMessage(int perf) {
        ACLMessage msg = new ACLMessage(perf);
        msg.setConversationId(generateRandomStringByUUIDNoDash());
        return msg;
    }

    static public String generateRandomStringByUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static public Coordinate generateRandomCoordinate() {
        return new Coordinate(
                MessagesUtils.generateRandomInt(0, 5),
                MessagesUtils.generateRandomInt(0, 5)
        );
    }

    static public int generateRandomInt(int min, int max) {
        int bound = (max - min) + 1;
        return ThreadLocalRandom.current().nextInt(bound) % bound + min;
    }
}
