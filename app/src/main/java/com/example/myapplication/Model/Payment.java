package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payment {

    @JsonProperty
    public long id;
    @JsonProperty
    public long from;
    @JsonProperty
    public long to;
    @JsonProperty
    public long activity;
    @JsonProperty
    public String modified;
    @JsonProperty
    public double ammount;
    @JsonProperty
    public Constants.PaymentStatus status;
    @JsonProperty
    public Error error;

    public Payment (long id, long from, long to, long activity, String modified, double ammount, Constants.PaymentStatus status, Error error) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.activity = activity;
        this.modified = modified;
        this.ammount = ammount;
        this.status = status;
        this.error = error;
    }

    public Payment (Payment payments) {
        if (payments != null) {
            this.id = payments.id;
            this.from = payments.from;
            this.to = payments.to;
            this.activity = payments.activity;
            this.modified = payments.modified;
            this.ammount = payments.ammount;
            this.status = payments.status;
            this.error = payments.error;
        }
    }

    public Payment () {
        this.id = 0;
        this.from = 0;
        this.to = 0;
        this.activity = 0;
        this.modified = "";
        this.ammount = 0.0;
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
                ", ammount=" + ammount +
                ", status=" + status +
                ", error=" + error +
                '}';
    }
}
