package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.northstar.servers.HttpServer;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.AbstractRoute;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;
import org.northstar.servers.utils.Constants;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestWithCustomRouteServer {


    HttpServer server;

    @Before
    public void setup() {
        HttpServer.HttpServerBuilder builder = HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(8080).withRoute(new DefaultStatusRoute())
                .withRoute(new AbstractRoute() {
                    @Override
                    public String baseLayer() {
                        return "/test";
                    }

                    @Override
                    public boolean isAuthNeeded() {
                        return false;
                    }

                    @Override
                    public RequestRoutingResponse handle(HttpRequest request) throws GenericServerProcessingException {
                        return RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(Map.of("name", "value")));
                    }
                });
        server = builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void checkStatus() {
        try {
            JsonNode content = Constants.OBJECT_MAPPER.readTree(new URL("http://127.0.0.1:8080/test").openConnection().getInputStream());
            assert content.has("name");
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
