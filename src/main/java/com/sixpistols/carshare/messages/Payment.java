package com.sixpistols.carshare.messages;

public class Payment implements java.io.Serializable {
    public static final int payment = 0;
    public static final int salary = 1;

    private String paymentId;
    private String userId;
    private double price;
    private int type;

    public Payment(String userId, double price, int type) {
        this.paymentId = MessagesUtils.generateRandomStringByUUIDNoDash();
        this.userId = userId;
        this.price = price;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", userId='" + userId + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public double getPrice() {
        return price;
    }

    public int getType() {
        return type;
    }
}
