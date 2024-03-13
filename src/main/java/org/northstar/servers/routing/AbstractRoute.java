package org.northstar.servers.routing;

import org.northstar.servers.jwt.AuthRequest;

import java.util.regex.Pattern;

public abstract class AbstractRoute implements RequestRoute{

    protected PatternExtractor patternExtractor;// Pattern.compile("/auth/self", Pattern.CASE_INSENSITIVE);

    protected String normalizedLayer;
    @Override
    public PatternExtractor getPattern(){
        if(patternExtractor==null){
            patternExtractor = new PatternExtractor(baseLayer());
        }
        return patternExtractor;
    }





    @Override
    public AuthRequest.AuthInfo getAuthInfo() {
        return RequestRoutingContexts.getInstance().getAuthInfo();
    }

    @Override
    public PatternExtractor.Match getURIMatch() {
        return RequestRoutingContexts.getInstance().getMatch();
    }
}
