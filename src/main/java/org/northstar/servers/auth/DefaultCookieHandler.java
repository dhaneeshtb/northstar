package org.northstar.servers.auth;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.routing.RequestRoutingContexts;

import java.util.List;
import java.util.Set;

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
    public String onReadToken(HttpRequest request, Set<Cookie> cookies) {
        Cookie cookie = cookies.stream().filter(c->cookieName().equals(c.name())).findFirst().orElse(null);
        return cookie!=null?cookie.value():null;
    }

    @Override
    public String cookieName() {
        return cookieName;
    }
}
