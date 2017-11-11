package com.example.myapplication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorgerios on 10/11/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "owns",
        "activities",
        "tasks",
        "paid",
        "received",
        "firstName",
        "lastName",
        "modified",
        "email",
        "password",
        "numpers",
        "enabled",
        "error"
})
public class Person {
    @JsonProperty
    public long id;
    @JsonProperty
    public List<Long> owns;
    @JsonProperty
    public List<Long> activities;
    @JsonProperty
    public List<Long> tasks;
    @JsonProperty
    public List<Long> paid;
    @JsonProperty
    public List<Long> received;
    @JsonProperty
    public String firstName;
    @JsonProperty
    public String lastName;
    @JsonProperty
    public String modified;
    @JsonProperty
    public String email;
    @JsonProperty
    public String password;
    @JsonProperty
    public int numpers;
    @JsonProperty
    public boolean enabled;
    @JsonProperty
    public Error error;

    public Person(long id, List<Long> owns, List<Long> activities, List<Long> tasks, List<Long> paid,
                     List<Long> received, String firstName, String lastName, String modified, String email,
                     String password, int numpers, boolean enabled, Error error) {
        this.id = id;
        this.owns = owns;
        this.activities = activities;
        this.tasks = tasks;
        this.paid = paid;
        this.received = received;
        this.firstName = firstName;
        this.lastName = lastName;
        this.modified = modified;
        this.email = email;
        this.password = password;
        this.numpers = numpers;
        this.enabled = enabled;
        this.error = error;
    }

    public Person() {
        this.id = 0;
        this.owns = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.paid = new ArrayList<>();
        this.received = new ArrayList<>();
        this.firstName = "";
        this.lastName = "";
        this.modified = "";
        this.email = "";
        this.password = "";
        this.numpers = 1;
        this.enabled = true;
        this.error = new Error();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", owns=" + owns +
                ", activities=" + activities +
                ", tasks=" + tasks +
                ", paid=" + paid +
                ", received=" + received +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", modified='" + modified + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", numpers=" + numpers +
                ", enabled=" + enabled +
                ", error=" + error +
                '}';
    }
}
