package com.sixpistols.carshare.messages;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MessagesUtils {
    static public String generateRandomStringByUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static public int generateRandomInt(int min, int max) {
        int bound = (max - min) + 1;
        return ThreadLocalRandom.current().nextInt(bound) % bound + min;
    }
}
