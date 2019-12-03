package com.sixpistols.carshare.messages;

public class LoginToken implements java.io.Serializable {
    public String tokenId;
    public String userId;

    @Override
    public String toString() {
        return "LoginToken{" +
                "tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
