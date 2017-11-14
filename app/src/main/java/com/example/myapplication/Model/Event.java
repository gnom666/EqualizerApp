package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Event {
    @JsonProperty
    public long id;
    @JsonProperty
    public long owner;
    @JsonProperty
    public List<Long> participants;
    @JsonProperty
    public List<Long> tasks;
    @JsonProperty
    public List<Long> payments;
    @JsonProperty
    public String name;
    @JsonProperty
    public String modified;
    @JsonProperty
    public String date;
    @JsonProperty
    public String description;
    @JsonProperty
    public boolean calculated;
    @JsonProperty
    public double total = 0.0;
    @JsonProperty
    public Error error;

    public Event(long id, long owner, List<Long> participants, List<Long> tasks, List<Long> payments, String name,
                       String modified, String date, String description, boolean calculated, double total, Error error) {
        this.id = id;
        this.owner = owner;
        this.participants = participants;
        this.tasks = tasks;
        this.payments = payments;
        this.name = name;
        this.modified = modified;
        this.date = date;
        this.description = description;
        this.calculated = calculated;
        this.total = total;
        this.error = error;
    }

    public Event() {
        this.participants = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.id = 0;
        this.owner = 0;
        this.name = "";
        this.modified = "";
        this.date = "";
        this.description = "";
        this.calculated = false;
        this.total = 0.0;
        this.error = null;
    }

    public Event(Event activity) {
        this.participants = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.payments = new ArrayList<>();

        if (activity != null) {
            for (Long p : activity.participants) { this.participants.add(p); }
            for (Long t : activity.tasks) { this.tasks.add(t); }
            for (Long p : activity.payments) { this.payments.add(p); }

            this.id = activity.id;
            this.owner = activity.owner;
            this.name = activity.name;
            this.modified = activity.modified;
            this.date = activity.date;
            this.description = activity.description;
            this.calculated = activity.calculated;
            this.total = activity.total;
            this.error = activity.error;
        }
    }
}
