package com.rs.mv.rockit.exception;

import org.springframework.stereotype.Component;

@Component
public class APIErrorFactory {

    private boolean printStacktrace = true;

    public APIErrorRepresentation buildError(int errorCode, String errorMessage, Throwable exception) {
        final APIErrorRepresentation error = new APIErrorRepresentation();
        error.setErrorCode(errorCode);
        error.setErrorMessage(errorMessage);
        if (exception != null && printStacktrace) {
            error.setErrorData(exception);
        }
        return error;
    }

    public void setPrintStacktrace(boolean printStacktrace) {
        this.printStacktrace = printStacktrace;
    }
}
