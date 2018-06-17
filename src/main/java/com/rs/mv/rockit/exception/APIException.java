package com.rs.mv.rockit.exception;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIException {

    @JsonProperty("innerErrorData")
    private APIException innerErrorData;

    @JsonProperty("exception")
    private String exception;

    @JsonProperty("stack")
    private List<String> stack;

    public APIException(Throwable t) {
        exception = t.getClass().getCanonicalName() + ":" + t.getMessage();
        stack = new ArrayList<>();
        if (t.getStackTrace() != null) {
            for (StackTraceElement next : t.getStackTrace()) {
                stack.add(next.getClassName() + "." + next.getMethodName() + " at " + next.getFileName() + ", line " + next.getLineNumber());
            }
        }
        if (t.getCause() != null) {
            innerErrorData = new APIException(t.getCause());
        }
    }
}