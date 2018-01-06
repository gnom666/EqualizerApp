package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Task extends Entity {

    @JsonProperty
    public long owner;
    @JsonProperty
    public long activity;
    @JsonProperty
    public long attachment;
    @JsonProperty
    public String name;
    @JsonProperty
    public String modified;
    @JsonProperty
    public String description;
    @JsonProperty
    public boolean calculated;
    @JsonProperty
    public double amount;
    @JsonProperty
    public Error error;

    public Task(long id, long owner, long activity, long attachment, String name, String modified, String description,
                   boolean calculated,	double amount, Error error) {
        super(id);
        this.owner = owner;
        this.activity = activity;
        this.attachment = attachment;
        this.name = name;
        this.modified = modified;
        this.description = description;
        this.calculated = calculated;
        this.amount = amount;
        this.error = error;
    }

    public Task(Task task) {
        super(task.id);
        if (task != null) {
            this.owner = task.owner;
            this.activity = task.activity;
            this.attachment = task.attachment;
            this.name = task.name;
            this.modified = task.modified;
            this.description = task.description;
            this.calculated = task.calculated;
            this.amount = task.amount;
            this.error = task.error;
        }
    }

    public Task() {
        super(0);
        this.owner = 0;
        this.activity = 0;
        this.attachment = 0;
        this.name = "";
        this.modified = "";
        this.description = "";
        this.calculated = false;
        this.amount = 0.0;
        this.error = null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", owner=" + owner +
                ", activity=" + activity +
                ", attachment=" + attachment +
                ", name='" + name + '\'' +
                ", modified='" + modified + '\'' +
                ", description='" + description + '\'' +
                ", calculated=" + calculated +
                ", amount=" + amount +
                ", error=" + error +
                '}';
    }
}
