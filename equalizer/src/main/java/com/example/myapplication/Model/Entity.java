package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jorgerios on 30/11/17.
 */

public class Entity {
    @JsonProperty
    public long id;

    public Entity () {
        this.id = 0;
    }

    public Entity (Entity e) {
        if (e != null) {
            this.id = e.id;
        }
    }

    public Entity (long id) {
        this.id = id;
    }

}
