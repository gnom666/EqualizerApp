package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payment extends Entity{

    @JsonProperty
    public long from;
    @JsonProperty
    public long to;
    @JsonProperty
    public long activity;
    @JsonProperty
    public String modified;
    @JsonProperty
    public double amount;
    @JsonProperty
    public Constants.PaymentStatus status;
    @JsonProperty
    public Error error;

    public Payment (long id, long from, long to, long activity, String modified, double amount, Constants.PaymentStatus status, Error error) {
        super.id = id;
        this.from = from;
        this.to = to;
        this.activity = activity;
        this.modified = modified;
        this.amount = amount;
        this.status = status;
        this.error = error;
    }

    public Payment (Payment payments) {
        if (payments != null) {
            super.id = payments.id;
            this.from = payments.from;
            this.to = payments.to;
            this.activity = payments.activity;
            this.modified = payments.modified;
            this.amount = payments.amount;
            this.status = payments.status;
            this.error = payments.error;
        }
    }

    public Payment () {
        super.id = 0;
        this.from = 0;
        this.to = 0;
        this.activity = 0;
        this.modified = "";
        this.amount = 0.0;
        this.status = Constants.PaymentStatus.CONFLICT;
        this.error = error;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", activity=" + activity +
                ", modified='" + modified + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", error=" + error +
                '}';
    }
}
