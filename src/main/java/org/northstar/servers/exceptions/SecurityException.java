package org.northstar.servers.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class SecurityException extends Exception{
    private final transient HttpResponseStatus status;

    public HttpResponseStatus getStatus() {
        return status;
    }

    public SecurityException(String message, HttpResponseStatus status){
        super(message);
        this.status=status;
    }

    public SecurityException(Exception e, HttpResponseStatus status){
        super(e);
        this.status=status;
    }

}
