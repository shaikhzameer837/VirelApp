package com.intelj.y_ral_gaming.model;

public class PaymentHistoryModel {
    private String date;
    private String transaction;
    private String amount;
    private String img_url;
    private String ticket_id;
    private int type;

    public PaymentHistoryModel(String date, String transaction, String amount, String img_url, String ticket_id, int type) {
        this.date = date;
        this.transaction = transaction;
        this.amount = amount;
        this.img_url = img_url;
        this.ticket_id = ticket_id;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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
