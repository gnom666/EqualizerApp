package com.example.myapplication.Model;

import java.util.List;

/**
 * Created by jorgerios on 30/11/17.
 */

public class Utils {
    public static Entity result;

    public static <T extends Entity> T byId(List<? extends Entity> content, final long id, Class<T> type) {

        result = null;
        for (Entity e : content) {
            if (type == Person.class) {
                if (((Person)e).id == id) return (T) e;
            }
            if (type == Task.class) {
                if (((Task)e).id == id) return (T) e;
            }
            if (type == Payment.class) {
                if (((Payment)e).id == id) return (T) e;
            }
            if (type == Event.class) {
                if (((Event)e).id == id) return (T) e;
            }
        }
        return null;
    }
}
