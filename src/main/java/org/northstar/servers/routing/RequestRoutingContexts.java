package org.northstar.servers.routing;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTParser;

import java.util.HashMap;
import java.util.Map;
 public class RequestRoutingContexts {
    private RequestRoutingContexts(){
    }

    private static   final Map<String,RequestRoute> routesMap=new HashMap<>();

    private static   final ThreadLocal<AuthRequest.AuthInfo> authInfoContext=new ThreadLocal<>();

    private  static   final ThreadLocal<PatternExtractor.Match> uriContext=new ThreadLocal<>();


    private static JWTParser jwtParser;

    public static void setJwtParser(JWTParser jwtParser) {
        RequestRoutingContexts.jwtParser = jwtParser;
    }

     public static JWTParser getParser(){
         return jwtParser;
     }



    public static void register(RequestRoute route){
        routesMap.put(route.baseLayer(),route);
    }

    private static boolean isMatched(PatternExtractor pattern,String uri){
        PatternExtractor.Match match = pattern.match(uri);
        if(match.isMatched()){
            uriContext.set(match);
        }
        return match.isMatched();
    }



    public static RequestRoute getRouter(String uri){
       return routesMap.values().stream().filter(r->isMatched(r.getPattern(),uri)).findFirst().orElse(null);
    }

    public static AuthRequest.AuthInfo getAuthInfo(){
        return authInfoContext.get();
    }

    public static PatternExtractor.Match getMatch(){
        return uriContext.get();
    }

    public static void setAuthInfo(AuthRequest.AuthInfo authInfo){
         authInfoContext.set(authInfo);
    }

    public static void removeContext(){
        uriContext.remove();
        authInfoContext.remove();
    }






}
