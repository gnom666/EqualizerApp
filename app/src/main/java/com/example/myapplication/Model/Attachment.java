package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jorgerios on 3/01/18.
 */

public class Attachment  extends Entity {
    @JsonProperty
    public String name;
    @JsonProperty
    public String modified;
    @JsonProperty
    public String contentB64;
    @JsonProperty
    public long task;
    @JsonProperty
    public Error error;

    public Attachment(long id, String name, String modified, String contentB64, long task, Error error) {
        super(id);
        this.name = name;
        this.modified = modified;
        this.contentB64 = contentB64;
        this.task = task;
        this.error = error;
    }

    public Attachment(Attachment attachment) {
        super(attachment.id);
        this.name = attachment.name;
        this.modified = attachment.modified;
        this.contentB64 = attachment.contentB64;
        this.task = attachment.task;
        this.error = attachment.error;
    }

    public Attachment() {
        super(0);
        this.name = "";
        this.modified = "";
        this.contentB64 = "";
        this.task = 0;
        this.error = null;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", modified='" + modified + '\'' +
                ", contentB64='" + contentB64 + '\'' +
                ", task=" + task +
                ", error=" + error +
                '}';
    }
}
