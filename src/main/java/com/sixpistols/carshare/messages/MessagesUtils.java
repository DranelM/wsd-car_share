package com.sixpistols.carshare.messages;

import java.util.UUID;

public class MessagesUtils {
    static public String generateRandomStringByUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
