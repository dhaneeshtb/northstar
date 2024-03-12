package org.dtb.server;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import org.dtb.servers.HttpServer;
import org.dtb.servers.jwt.JWTKeyImpl;
import org.dtb.servers.routing.DefaultStatusRoute;
import org.dtb.servers.utils.Constants;
import org.junit.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestServer {


    HttpServer server;

    @Before
    public void setup(){
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(8080).withRoute(new DefaultStatusRoute())
                .withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void checkStatus() {
        try {
          JsonNode content = Constants.OBJECT_MAPPER.readTree(new URL("http://127.0.0.1:8080/status").openConnection().getInputStream());
          assert content.has("status");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void stopServer() {
        try {
            server.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
