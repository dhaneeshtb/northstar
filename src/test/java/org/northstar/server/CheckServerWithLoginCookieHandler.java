package org.northstar.server;

import com.auth0.jwt.algorithms.Algorithm;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.northstar.servers.HttpServer;
import org.northstar.servers.auth.CookieHandler;
import org.northstar.servers.auth.UserFileStore;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTKeyImpl;
import org.northstar.servers.routing.DefaultStatusRoute;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class CheckServerWithLoginCookieHandler {
    public static void main(String[] args) {
        start(8080);
    }

    public static void start(int port) {
        HttpServer.HttpServerBuilder builder= HttpServer.HttpServerBuilder.createBuilder();
        builder.withPort(port).
                 withUserLoginConf(new UserFileStore("sampleusers.json"))
                .withRoute(new DefaultStatusRoute())
                .withCookieHandle(new CookieHandler() {
                    @Override
                    public Cookie onSetCookie(HttpRequest request, AuthRequest.LoginResponse loginResponse) {
                        DefaultCookie cookie= new DefaultCookie("test","tes");
                        cookie.setPath("/");
                        cookie.setMaxAge(100);//Seconds
                        return cookie;
                    }

                    @Override
                    public String onReadToken(HttpRequest request, Set<Cookie> cookies) {
                        Cookie cookie=  cookies.stream().filter(c->c.name().equalsIgnoreCase(cookieName())).findFirst().orElse(null);
                        return cookie!=null?cookie.value():null;
                    }

                    @Override
                    public String cookieName() {
                        return "token";
                    }
                })
                .withDomain("localhost")
                .withRoute("/test/{id}/{yy}/testing",false,(request,authInfo,match)->
                        RequestRoutingResponse.response(HttpResponseStatus.OK, new RouteMessage.RouteAttributeMessage(match.getAttributes()))).withJWTParser(new JWTKeyImpl("", Algorithm.HMAC512("test".getBytes(StandardCharsets.UTF_8))));
        HttpServer server=builder.build();
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
