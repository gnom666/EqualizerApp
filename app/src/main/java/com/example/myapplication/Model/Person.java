package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "owns",
        "activities",
        "tasks",
        "contacts",
        "contactOf",
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
    public List<Long> contacts;
    @JsonProperty
    public List<Long> contactOf;
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
        this.error = null;
    }

    public Person (Person person) {
        this.id = person.id;
        this.owns = person.owns;
        this.activities = person.activities;
        this.tasks = person.tasks;
        this.paid = person.paid;
        this.received = person.received;
        this.firstName = person.firstName;
        this.lastName = person.lastName;
        this.modified = person.modified;
        this.email = person.email;
        this.password = person.password;
        this.numpers = person.numpers;
        this.enabled = person.enabled;
        this.error = person.error;
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
