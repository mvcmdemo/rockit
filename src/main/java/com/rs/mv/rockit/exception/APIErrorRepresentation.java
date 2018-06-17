package com.rs.mv.rockit.exception;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIErrorRepresentation {

    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR_DETAIL_MESSAGE = "errorDetailMessage";
    public static final String ERROR_DATA = "innerErrorData";
    public static final String EXCEPTION = "exception";
    public static final String STACK = "stack";

    @JsonProperty(value = "severity")
    private String severity = "Error";
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("errorCode")
    private int errorCode;
    @JsonProperty("errorDetailMessage")
    private String errorDetailMessage;
    @JsonProperty("innerErrorData")
    private APIException innerErrorData;
    @JsonProperty("exception")
    private String exception;
    @JsonProperty("stack")
    private String stack;

    public APIErrorRepresentation() {
    }

    public APIErrorRepresentation(Throwable throwable) {
        innerErrorData = new APIException(throwable);
        errorMessage = throwable.getMessage();
    }

    public void setErrorMessage(String message) {
        errorMessage = message;
    }

    public String getSeverity() {
        return severity;
    }

    public String getErrorDetailMessage() {
        return errorDetailMessage;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setErrorCode(int code) {
        errorCode = code;
    }

    public void setErrorDetailMessage(String detailMessage) {
        errorDetailMessage = detailMessage;
    }

    public void setErrorData(Throwable tt) {
        innerErrorData = new APIException(tt);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @JsonIgnore
    public String getAsJSON() throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public APIException getInnerErrorData() {
        return innerErrorData;
    }
}