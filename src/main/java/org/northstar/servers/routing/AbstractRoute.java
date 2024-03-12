package org.northstar.servers.routing;

import org.northstar.servers.jwt.AuthRequest;

import java.util.regex.Pattern;

public abstract class AbstractRoute implements RequestRoute{

    protected Pattern selfURLPattern;// Pattern.compile("/auth/self", Pattern.CASE_INSENSITIVE);
    @Override
    public Pattern getPattern(){
        if(selfURLPattern==null){
            selfURLPattern =  Pattern.compile(baseLayer(), Pattern.CASE_INSENSITIVE);
        }
        return selfURLPattern;
    }



    @Override
    public AuthRequest.AuthInfo getAuthInfo() {
        return RequestRoutingContexts.getInstance().getAuthInfo();
    }
}
