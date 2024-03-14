package org.northstar.servers.auth;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import org.northstar.servers.jwt.AuthRequest;

import java.util.List;

public interface CookieHandler {

    Cookie onSetCookie(HttpRequest request,AuthRequest.LoginResponse loginResponse);

    AuthRequest.AuthInfo onReadCookie(HttpRequest request,List<Cookie> cookies);

    String cookieName();

}
