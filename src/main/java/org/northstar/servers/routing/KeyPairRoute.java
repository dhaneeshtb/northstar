package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import org.northstar.servers.End2EndEncryption;
import org.northstar.servers.auth.LoginHandler;
import org.northstar.servers.auth.UserStore;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.utils.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KeyPairRoute extends AbstractRoute{
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyPairRoute.class);

    private End2EndEncryption end2EndEncryption;

    public KeyPairRoute(String baseLayer, End2EndEncryption end2EndEncryption){
        this.baseLayer=baseLayer;
        this.end2EndEncryption=end2EndEncryption;
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
        try {
            return RequestRoutingResponse.response(HttpResponseStatus.OK,
                    new RouteMessage.RouteAttributeMessage(Map.of("publicKey", end2EndEncryption.getEncodedKeyPair().getPublicKey())));
        }catch (Exception e){
            return RequestRoutingResponse.response(HttpResponseStatus.FORBIDDEN,
                    new RouteMessage.RouteAttributeMessage(Map.of("message","invalid credentials")));
        }
    }

}
