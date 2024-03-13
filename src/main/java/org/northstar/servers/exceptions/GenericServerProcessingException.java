package org.northstar.servers.exceptions;


public class GenericServerProcessingException extends RuntimeException {
    public GenericServerProcessingException(String message){
        super(message);
    }
    public GenericServerProcessingException(Exception e){
        super(e);
    }
}
