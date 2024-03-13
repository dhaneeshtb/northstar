package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.northstar.servers.HttpServer;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.AbstractRoute;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CheckServerWithCustom {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).withRoute(new DefaultStatusRoute())
                .withRoute(new AbstractRoute() {
                    @Override
                    public String baseLayer() {
                        return "/test/{id}/{yy}";
                    }

                    @Override
                    public boolean isAuthNeeded() {
                        return false;
                    }

                    @Override
                    public RequestRoutingResponse handle(HttpRequest request) throws Exception {
                        System.out.println(getURIMatch().getAttributes());
                        return RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(Map.of("name", "value")));
                    }
                })
                .withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
