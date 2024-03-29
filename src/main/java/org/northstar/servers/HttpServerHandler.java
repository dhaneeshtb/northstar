package org.northstar.servers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.northstar.security.ContentEncryption;
import org.northstar.servers.exceptions.SecurityException;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTParser;
import org.northstar.servers.routing.RequestRoute;
import org.northstar.servers.routing.RequestRoutingContexts;
import org.northstar.servers.routing.RequestRoutingResponse;
import org.northstar.servers.routing.RouteMessage;
import io.netty.handler.codec.http.cookie.Cookie;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.function.Function;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final End2EndEncryption end2EndEncryption;

    public HttpServerHandler(End2EndEncryption end2EndEncryption) {
        this.end2EndEncryption=end2EndEncryption;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private RequestRoutingResponse withAuthInfo(HttpRequest req, Function<AuthRequest.AuthInfo,RequestRoutingResponse> authInfoConsumer) throws SecurityException {
        AuthRequest.AuthInfo authInfo = null;
        RequestRoute route = getRoute(req);
        if(route==null){
           return RequestRoutingResponse.response(HttpResponseStatus.NOT_FOUND, new RouteMessage.RouteErrorMessage("missing resource "+req.uri()) );
        }
        if (getRoute(req).isAuthNeeded()) {
            String token = getToken(req);
            JWTParser parser = RequestRoutingContexts.getParser();
            if(parser==null){
                return RequestRoutingResponse.response(HttpResponseStatus.UNAUTHORIZED, new RouteMessage.RouteErrorMessage("missing auth token parser configuration") );
            }
            authInfo=toAuthInfo(token);
        }
        return authInfoConsumer.apply(authInfo);
    }

    private AuthRequest.AuthInfo toAuthInfo(String token) throws SecurityException {
        try {
            AuthRequest.AuthInfo authInfo = RequestRoutingContexts.getParser().verify(token);
            RequestRoutingContexts.setAuthInfo(authInfo);
            return authInfo;
        }catch (Exception e){
            throw new SecurityException("invalid auth token",HttpResponseStatus.FORBIDDEN);
        }
    }
    private RequestRoute getRoute(HttpRequest req){
        return RequestRoutingContexts.getRouter(req.uri());
    }

    private String getToken(HttpRequest req) throws SecurityException {
        String token = req.headers().get("Authorization");
        if (token != null && RequestRoutingContexts.getParser() != null && (token.contains("Bearer"))) {
                token = token.substring(7);
        }

        if(token==null){
            String cookieString = req.headers().get(COOKIE);
            Set<Cookie> cookies =  ServerCookieDecoder.LAX.decode(cookieString);
            token= RequestRoutingContexts.getCookieHandler().onReadToken(req, cookies);
        }

        if(token==null){
            throw new SecurityException("missing auth token",HttpResponseStatus.FORBIDDEN);
        }
        return token;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            RequestRoute route = RequestRoutingContexts.getRouter(req.uri());
            RequestRoutingResponse response;
            try {
                response = withAuthInfo(req,authInfo->{
                    try {
                        return route.handle(req);
                    } catch (Exception e) {
                        return RequestRoutingResponse.response(HttpResponseStatus.INTERNAL_SERVER_ERROR, new RouteMessage.RouteErrorMessage(e.getMessage()));
                    }
                });
            } catch (SecurityException e) {
                response = RequestRoutingResponse.response(e.getStatus(), new RouteMessage.RouteErrorMessage(e.getMessage()));
            }finally {
                RequestRoutingContexts.removeContext();
            }
            if(route!=null && route.isEnableE2EEncryption() && end2EndEncryption!=null){
                ContentEncryption contentEncryption = getContentEncryptionObject(req);
                if(contentEncryption!=null) {
                    response.setBody(Base64.getEncoder().encodeToString(contentEncryption.encrypt2Buffer(response.getBody()).array()));
                }
            }
            handleResponse(ctx, req, response);
        }
    }

    private ContentEncryption getContentEncryptionObject(HttpRequest req){
        try {
            String clientKey = req.headers().get("X-Client-Token");
            String clientCert = req.headers().get("X-Client-Cert");
            ContentEncryption.ContentEncryptionBuilder ce = new ContentEncryption.ContentEncryptionBuilder(end2EndEncryption.getEncodedKeyPair(), end2EndEncryption.getDecodeDKeyPair())
                    .withClientKey(clientKey);
            if (clientCert != null) {
                ce.withClientCert(new String(Base64.getDecoder().decode(clientCert.getBytes(StandardCharsets.UTF_8))));
            }else{
                ce.withInverted(true);
            }
            return ce.build();
        }catch (Exception e){
            return null;
        }

    }

    private void handleResponse(ChannelHandlerContext ctx, HttpRequest req, RequestRoutingResponse routeResponse) {
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), routeResponse.getStatus(),
                Unpooled.wrappedBuffer(routeResponse.getBody().getBytes(StandardCharsets.UTF_8)));
        response.headers()
                .set(CONTENT_TYPE, routeResponse.getContentType())
                .setInt(CONTENT_LENGTH, response.content().readableBytes());

        if(routeResponse.getCookie()!=null){
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(routeResponse.getCookie()));
        }
        if (keepAlive) {
            if (!req.protocolVersion().isKeepAliveDefault()) {
                response.headers().set(CONNECTION, KEEP_ALIVE);
            }
        } else {
            response.headers().set(CONNECTION, CLOSE);
        }

        ChannelFuture f = ctx.write(response);

        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
