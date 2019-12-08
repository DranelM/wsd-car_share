package com.sixpistols.carshare.messages;

public class LoginToken implements java.io.Serializable {
    private String tokenId;
    private String userId;

    public LoginToken(String userId) {
        this.tokenId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginToken{" +
                "tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getUserId() {
        return userId;
    }
}
