package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import org.northstar.servers.jwt.AuthRequest;

import java.util.regex.Pattern;

public interface RequestRoute {

    AuthRequest.AuthInfo getAuthInfo();

    String baseLayer();

    Pattern getPattern();


    boolean isAuthNeeded();
    RequestRoutingResponse handle(HttpRequest request) throws Exception;

}
