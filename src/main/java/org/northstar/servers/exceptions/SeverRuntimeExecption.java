package org.northstar.servers.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;

public class SeverRuntimeExecption extends Exception{
    private final HttpResponseStatus status;

    public HttpResponseStatus getStatus() {
        return status;
    }

    public SeverRuntimeExecption(String message, HttpResponseStatus status){
        super(message);
        this.status=status;
    }
}
