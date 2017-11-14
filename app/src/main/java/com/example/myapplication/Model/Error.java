package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "type",
        "description",
        "timestamp"
})
public class Error {

    @JsonProperty
    public Constants.ErrorCode code;
    @JsonProperty
    public Constants.ErrorType type;
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
        this.code = Constants.ErrorCode.UNKNOWN;
        this.type = Constants.ErrorType.UNKNOWN;
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
