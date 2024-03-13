package org.northstar.servers.routing;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTParser;

import java.util.HashMap;
import java.util.Map;
 public class RequestRoutingContexts {

    private static RequestRoutingContexts routingContexts;
    private RequestRoutingContexts(){
    }
    public static synchronized   RequestRoutingContexts getInstance(){
        if(routingContexts==null){
            routingContexts=new RequestRoutingContexts();
        }
        return routingContexts;
    }

    protected static final Map<String,RequestRoute> routesMap=new HashMap<>();


    public static final ThreadLocal<AuthRequest.AuthInfo> authInfoContext=new ThreadLocal<>();

    public static final ThreadLocal<PatternExtractor.Match> uriContext=new ThreadLocal<>();


    private JWTParser jwtParser;

    public void setJwtParser(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }



    public void register(RequestRoute route){
        routesMap.put(route.baseLayer(),route);
    }

    private boolean isMatched(PatternExtractor pattern,String uri){
        PatternExtractor.Match match = pattern.match(uri);
        if(match.isMatched()){
            uriContext.set(match);
        }
        return match.isMatched();
    }

    public JWTParser getParser(){
        return this.jwtParser;
    }

    public RequestRoute getRouter(String uri){
       return routesMap.values().stream().filter(r->isMatched(r.getPattern(),uri)).findFirst().orElse(null);
    }

    public AuthRequest.AuthInfo getAuthInfo(){
        return authInfoContext.get();
    }

    public PatternExtractor.Match getMatch(){
        return uriContext.get();
    }

    public void setAuthInfo(AuthRequest.AuthInfo authInfo){
         authInfoContext.set(authInfo);
    }

    public void removeContext(){
        uriContext.remove();
        authInfoContext.remove();
    }






}
