package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

public class RequestRoutingResponse {

    private HttpResponseStatus status;
    private String body;

    private Cookie cookie;

    private boolean enableE2EEncryption;

    private Map<String,Object> headers=new HashMap<>();

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public boolean isEnableE2EEncryption() {
        return enableE2EEncryption;
    }

    public void setEnableE2EEncryption(boolean enableE2EEncryption) {
        this.enableE2EEncryption = enableE2EEncryption;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public void setCookie(Cookie cookie) {
        this.cookie = cookie;
    }

    public static RequestRoutingResponse response(HttpResponseStatus status, RouteMessage routeMessage) {
        return new RequestRoutingResponse(status,routeMessage.toString());
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    private String contentType="application/json";

    public RequestRoutingResponse(HttpResponseStatus status,String body){
        this.status=status;
        this.body=body;
    }
    public RequestRoutingResponse(String body){
        this.body=body;
    }
    public RequestRoutingResponse(HttpResponseStatus status,String body,String contentType){
       this(status,body);
       this.contentType=contentType;
    }
    public static RequestRoutingResponse succes(String body){
        return new RequestRoutingResponse(body);
    }
    public static RequestRoutingResponse response(HttpResponseStatus status,String body){
        return new RequestRoutingResponse(status,body);
    }

}
