package org.dtb.servers.routing;

import org.dtb.servers.utils.JSON;

import java.util.Map;

public interface  RouteMessage{
    class RouteErrorMessage implements RouteMessage {
        private String message;
        public RouteErrorMessage(String message){
            this.message=message;
        }
        @Override
        public String toString() {
            return "{\"message\":\""+message+"\"}";
        }
    }

    class RouteAttributeMessage implements RouteMessage {
        private Map<String,String> attributes;
        public RouteAttributeMessage(Map<String,String> attributes){
            this.attributes=attributes;
        }
        @Override
        public String toString() {
            return JSON.toString(attributes);
        }
    }

}

