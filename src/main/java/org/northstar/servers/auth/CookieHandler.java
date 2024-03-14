package org.northstar.servers.auth;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import org.northstar.servers.jwt.AuthRequest;

import java.util.List;
import java.util.Set;

public interface CookieHandler {

    Cookie onSetCookie(HttpRequest request,AuthRequest.LoginResponse loginResponse);

    String onReadToken(HttpRequest request, Set<Cookie> cookies);

    String cookieName();

}
