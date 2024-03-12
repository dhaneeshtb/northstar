package org.dtb.server;

import com.auth0.jwt.algorithms.Algorithm;
import org.dtb.servers.HttpServer;
import org.dtb.servers.jwt.JWTKeyImpl;
import org.dtb.servers.routing.DefaultStatusRoute;

import java.nio.charset.StandardCharsets;

public class CheckServer {
    public static void main(String[] args) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(8080).withRoute(new DefaultStatusRoute())
                .withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
