package com.intelj.yral_gaming.model;

public class PaymentHistoryModel {
    private String date;
    private String transaction;
    private String amount;

    public PaymentHistoryModel(String date, String transaction, String amount) {
        this.date = date;
        this.transaction = transaction;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
