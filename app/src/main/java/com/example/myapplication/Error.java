package com.example.myapplication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.example.myapplication.Constants.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "type",
        "description",
        "timestamp"
})
public class Error {

    @JsonProperty
    public ErrorCode code;
    @JsonProperty
    public ErrorType type;
    @JsonProperty
    public String description;
    @JsonProperty
    public String timestamp;

    public Error(Error error) {
        this.code = error.code;
        this.type = error.type;
        this.description = error.description;
        this.timestamp = error.timestamp;
    }

    public Error() {
        this.code = ErrorCode.UNKNOWN;
        this.type = ErrorType.UNKNOWN;
        this.description = "";
        this.timestamp = "";
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
