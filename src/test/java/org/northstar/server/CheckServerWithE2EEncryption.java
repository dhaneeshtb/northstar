package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.northstar.servers.End2EndEncryption;
import org.northstar.servers.HttpServer;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;

import java.nio.charset.StandardCharsets;

public class CheckServerWithE2EEncryption {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {

        try {
            End2EndEncryption e2ee= new End2EndEncryption();
            HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
            builder.withEnd2EndEncryption(e2ee);
            builder.withPort(port).withRoute(new DefaultStatusRoute())
                    .withRoute("/test/{id}/{yy}/testing",false,true,(request,authInfo,match)->
                            RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
            HttpServer server=builder.build();

            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
