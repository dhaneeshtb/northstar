package org.northstar.servers.auth;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.routing.RequestRoutingContexts;
import java.security.NoSuchAlgorithmException;
public class LoginHandler {

    private UserStore userStore;
    public LoginHandler(UserStore userStore){
        this.userStore=userStore;
    }

    public AuthRequest.LoginResponse login(AuthRequest.LoginInput userInfo) throws  NoSuchAlgorithmException {
        AuthRequest.User dbUser= userStore.getUser(userInfo.getUsername());
        if(dbUser.getPassword().equalsIgnoreCase(AuthUtils.onewayHash(userInfo.getPassword()))){
            return createResponse(dbUser);
        }else{
            return new AuthRequest.LoginResponse(null,null);
        }
    }


    public AuthRequest.LoginResponse createResponse(AuthRequest.User user) throws SecurityException {
        return new AuthRequest.LoginResponse(RequestRoutingContexts.getParser().createToken(user),user);
    }

    public static String getDomainFromSubdomain(String host){
        String[] tups = host.split("[.]");
        if(tups.length>2){
            return "."+tups[tups.length-2]+"."+tups[tups.length-1];
        }else{
            return "."+host;
        }

    }
    public static String getDomain(String origin){
        String domain;
        if(origin!=null){
            domain = origin.split("://")[1];
            if(domain.startsWith("www")){
                domain=domain.replace("www","");
            }else{
                domain=getDomainFromSubdomain(domain);
            }
        }else{
            domain="";
        }
        return domain;

    }


}
