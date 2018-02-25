package com.rs.mv.rockit.exception;

public class SSHException extends Exception {
    public SSHException(String message) {
        super(message);
    }

    public SSHException(String message, Throwable cause) {
        super(message, cause);
    }
}
