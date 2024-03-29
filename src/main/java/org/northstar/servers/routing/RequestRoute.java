package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.jwt.AuthRequest;


public interface RequestRoute {

    AuthRequest.AuthInfo getAuthInfo();

    String baseLayer();

    PatternExtractor getPattern();

    boolean isEnableE2EEncryption();


    boolean isAuthNeeded();
    RequestRoutingResponse handle(HttpRequest request) throws GenericServerProcessingException;

    PatternExtractor.Match getURIMatch();

}
