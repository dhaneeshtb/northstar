package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import org.northstar.servers.auth.LoginHandler;
import org.northstar.servers.auth.UserStore;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.utils.JSON;

import java.util.Map;

public class DefaultLoginRoute extends AbstractRoute{

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
                if(RequestRoutingContexts.getServerDomain()!=null) {
                    Cookie cookie = new DefaultCookie("token", response.getToken());
                    cookie.setDomain(LoginHandler.getDomainFromSubdomain(RequestRoutingContexts.getServerDomain()));
                    cookie.setPath("/");
                    cookie.setMaxAge(24*60*60l);
                    resp.setCookie(cookie);
                }
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
}
