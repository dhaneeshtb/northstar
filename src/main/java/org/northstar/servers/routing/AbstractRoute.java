package org.northstar.servers.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.utils.TriParameterFunction;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class AbstractRoute implements RequestRoute{

    protected PatternExtractor patternExtractor;// Pattern.compile("/auth/self", Pattern.CASE_INSENSITIVE);

    protected String baseLayer;
    protected String normalizedLayer;

    private TriParameterFunction<HttpRequest, AuthRequest.AuthInfo, PatternExtractor.Match,RequestRoutingResponse> handler;

    protected boolean authNeeded;
    @Override
    public PatternExtractor getPattern(){
        if(patternExtractor==null){
            patternExtractor = new PatternExtractor(baseLayer());
        }
        return patternExtractor;
    }

    public AbstractRoute(){

    }

    public AbstractRoute(String baseLayer,boolean authNeeded){
        this.baseLayer=baseLayer;
        this.authNeeded=authNeeded;
        getPattern();
    }
    public AbstractRoute(String baseLayer,boolean authNeeded,TriParameterFunction<HttpRequest, AuthRequest.AuthInfo, PatternExtractor.Match,RequestRoutingResponse> handler){
        this(baseLayer,authNeeded);
        this.handler=handler;

    }

    @Override
    public String baseLayer(){
        return this.baseLayer;
    }


    @Override
    public AuthRequest.AuthInfo getAuthInfo() {
        return RequestRoutingContexts.getInstance().getAuthInfo();
    }

    @Override
    public PatternExtractor.Match getURIMatch() {
        return RequestRoutingContexts.getInstance().getMatch();
    }

    @Override
    public boolean isAuthNeeded() {
        return authNeeded;
    }

    @Override
    public RequestRoutingResponse handle(HttpRequest request) throws Exception {
        if(handler!=null) {
            return handler.handle(request,RequestRoutingContexts.getInstance().getAuthInfo(),RequestRoutingContexts.getInstance().getMatch());
        }else{
            return RequestRoutingResponse.response(HttpResponseStatus.NOT_IMPLEMENTED,new RouteMessage.RouteErrorMessage("handler not implemented"));
        }
    }
}
