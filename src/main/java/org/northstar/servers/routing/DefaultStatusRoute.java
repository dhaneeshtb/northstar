package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Map;

public class DefaultStatusRoute extends AbstractRoute{
        @Override
        public String baseLayer() {
            return "/status";
        }
        @Override
        public boolean isAuthNeeded() {
            return true;
        }
        @Override
        public RequestRoutingResponse handle(HttpRequest request) throws Exception {
            return RequestRoutingResponse.response(HttpResponseStatus.OK,
                    new RouteMessage.RouteAttributeMessage(Map.of("status","up")));
        }
}

