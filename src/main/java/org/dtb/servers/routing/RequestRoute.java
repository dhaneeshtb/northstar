package org.dtb.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import org.dtb.servers.jwt.AuthRequest;
import org.dtb.servers.jwt.JWTParser;

import java.util.regex.Pattern;

public interface RequestRoute {

    AuthRequest.AuthInfo getAuthInfo();

    String baseLayer();

    Pattern getPattern();


    boolean isAuthNeeded();
    RequestRoutingResponse handle(HttpRequest request) throws Exception;

}
