package com.example.myapplication.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "code",
        "type",
        "description",
        "timeStamp",
        "status",
        "message",
        "trace"
})
public class Error {

    @JsonProperty
    public Constants.ErrorCode code;
    @JsonProperty
    public Constants.ErrorType type;
    @JsonProperty
    public String description;
    @JsonProperty
    public Integer status;
    @JsonProperty
    public String error;
    @JsonProperty
    public String message;
    @JsonProperty
    public String timeStamp;
    @JsonProperty
    public String trace;

    public Error(Error er) {
        this.code = er.code;
        this.type = er.type;
        this.description = er.description;
        this.timeStamp = er.timeStamp;
        this.status = er.status;
        this.error = er.error;
        this.message = er.message;
        this.trace = er.trace;
    }

    public Error() {
        this.code = Constants.ErrorCode.UNKNOWN;
        this.type = Constants.ErrorType.UNKNOWN;
        this.description = "";
        this.timeStamp = "";
    }

    public Error(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");
    }

    @Override
    public String toString() {
        return "Error [code=" + code + ", type=" + type + ", description=" + description + ", status=" + status
                + ", error=" + error + ", message=" + message + ", timeStamp=" + timeStamp + ", trace=" + trace + "]";
    }
}
