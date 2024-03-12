package org.dtb.servers.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class SecurityException extends Exception{
    private final HttpResponseStatus status;

    public HttpResponseStatus getStatus() {
        return status;
    }

    public SecurityException(String message, HttpResponseStatus status){
        super(message);
        this.status=status;
    }

}
