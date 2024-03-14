package org.northstar.servers.auth;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.routing.RequestRoutingContexts;

import java.util.List;

public class DefaultCookieHandler implements CookieHandler{


    private final String cookieName;

    private long maxAge=24*60*60l;

    public DefaultCookieHandler(String cookieName,long maxAge){
        this.cookieName=cookieName;
        this.maxAge=maxAge;
    }

    public DefaultCookieHandler(String cookieName){
        this.cookieName=cookieName;
    }


    @Override
    public Cookie onSetCookie(HttpRequest request, AuthRequest.LoginResponse loginResponse) {
        Cookie cookie = new DefaultCookie(cookieName(), loginResponse.getToken());
        if(!RequestRoutingContexts.getServerDomain().contains("localhost")) {
            cookie.setDomain(LoginHandler.getDomainFromSubdomain(RequestRoutingContexts.getServerDomain()));
        }else{
            cookie.setHttpOnly(true);
        }
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    @Override
    public AuthRequest.AuthInfo onReadCookie(HttpRequest request, List<Cookie> cookies) {
        return null;
    }

    @Override
    public String cookieName() {
        return cookieName;
    }
}
