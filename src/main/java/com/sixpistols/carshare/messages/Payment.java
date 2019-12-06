package com.sixpistols.carshare.messages;

public class Payment implements java.io.Serializable {
    public static final int payment = 0;
    public static final int salary = 1;

    public String paymentId;
    public String userId;
    public double price;
    public int type;

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", userId='" + userId + '\'' +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
