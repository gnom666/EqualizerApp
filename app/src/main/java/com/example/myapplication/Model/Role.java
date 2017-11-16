package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Role {

    @JsonProperty
    public long id;
    @JsonProperty
    public Constants.RoleType roleType;
    @JsonProperty
    public List<Long> members;
    @JsonProperty
    public Error error;

    public Role(long id, Constants.RoleType roleType, List<Long> members, Error error) {
        this.id = id;
        this.roleType = roleType;
        this.members = members;
        this.error = error;
    }

    public Role(Role role) {
        this.members = new ArrayList<>();

        if (role != null) {
            this.id = role.id;
            this.roleType = role.roleType;
            for (long p : role.members) {
                this.members.add(p);
            }
            this.error = role.error;
        }
    }

    public Role() {
        this.id = 0;
        this.roleType = Constants.RoleType.GUEST;
        this.members = new ArrayList<>();
        this.error = null;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleType=" + roleType +
                ", members=" + members +
                ", error=" + error +
                '}';
    }
}
