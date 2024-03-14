package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import org.northstar.servers.auth.LoginHandler;
import org.northstar.servers.auth.UserStore;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultLoginRoute extends AbstractRoute{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLoginRoute.class);

    private final LoginHandler loginHandler;

    public DefaultLoginRoute(String baseLayer, UserStore store){
        this.baseLayer=baseLayer;
        this.loginHandler=new LoginHandler(store);
    }
    @Override
    public String baseLayer() {
        return baseLayer;
    }
    @Override
    public boolean isAuthNeeded() {
        return false;
    }
    @Override
    public RequestRoutingResponse handle(HttpRequest request) throws GenericServerProcessingException {
        AuthRequest.LoginInput input= JSON.toObject(AuthRequest.LoginInput.class,getRequestBody(request));
        try {
            AuthRequest.LoginResponse response = loginHandler.login(input);
            if(response.isStatus()) {
                RequestRoutingResponse resp =  RequestRoutingResponse.response(HttpResponseStatus.OK,
                        new RouteMessage.RouteAttributeMessage(Map.of("token", response.getToken())));
                setCookie(request,resp,response);
                return resp;
            }else{
                return RequestRoutingResponse.response(HttpResponseStatus.FORBIDDEN,
                        new RouteMessage.RouteAttributeMessage(Map.of("message","invalid credentials")));
            }
        }catch (Exception e){
            return RequestRoutingResponse.response(HttpResponseStatus.FORBIDDEN,
                    new RouteMessage.RouteAttributeMessage(Map.of("message","invalid credentials")));
        }
    }

    private void setCookie(HttpRequest request,RequestRoutingResponse resp,AuthRequest.LoginResponse response){
        if(RequestRoutingContexts.getServerDomain()!=null) {
            try {
                Cookie cookie = RequestRoutingContexts.getCookieHandler().onSetCookie(request, response);
                resp.setCookie(cookie);
            }catch (Exception e){
                LOGGER.error("error while setting cookie",e);
            }
        }
    }
}
