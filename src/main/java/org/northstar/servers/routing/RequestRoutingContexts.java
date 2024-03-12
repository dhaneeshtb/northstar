package org.northstar.servers.routing;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTParser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestRoutingContexts {

    protected static final Map<String,RequestRoute> routesMap=new HashMap<>();

    private static RequestRoutingContexts instance;

    public static final ThreadLocal<AuthRequest.AuthInfo> authInfoContext=new ThreadLocal<>();

    private JWTParser jwtParser;

    public void setJwtParser(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    private RequestRoutingContexts(){
    }
    public static synchronized RequestRoutingContexts getInstance(){
        if(instance==null){
            instance=new RequestRoutingContexts();
        }
        return instance;
    }

    public void register(RequestRoute route){
        routesMap.put(route.baseLayer(),route);
    }

    private boolean isMatched(Pattern pattern,String uri){
        Matcher matcher = pattern.matcher(uri);
        return matcher.find();
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

    public void setAuthInfo(AuthRequest.AuthInfo authInfo){
         authInfoContext.set(authInfo);
    }

    public void removeAuthInfo(){
        authInfoContext.remove();
    }






}
