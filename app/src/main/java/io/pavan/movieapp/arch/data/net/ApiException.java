package io.pavan.movieapp.arch.data.net;

/**
 * Created by pavan on 02/12/18
 */
public class ApiException extends Exception {

    public final Severity severity;

    public enum Severity {
        CRITICAL, CONTENT, CONTENT_UNDEFINED, NON_FATAL, NO_NETWORK
    }

    public ApiException(Severity severity, String message) {
        super(message);
        this.severity = severity;
    }
}
