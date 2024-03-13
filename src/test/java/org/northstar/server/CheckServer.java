package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import org.northstar.servers.HttpServer;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.DefaultStatusRoute;

import java.nio.charset.StandardCharsets;

public class CheckServer {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).withRoute(new DefaultStatusRoute())
                .withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
