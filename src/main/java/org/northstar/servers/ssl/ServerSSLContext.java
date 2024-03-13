package org.northstar.servers.ssl;


import io.netty.handler.codec.http.HttpResponseStatus;
import org.northstar.servers.exceptions.SecurityException;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ServerSSLContext {

    private ServerSSLContext(){}
    public static SSLContext get() throws SecurityException {
        SSLContext context=null;
        try {
            context = create();
        }catch (Exception e){
            throw new SecurityException(e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        return context;
    }
    public static SSLContext create() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext context;
        ServerKeyManager sniKeyManager = new ServerKeyManager();

        context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[]{
                sniKeyManager
        }, null, null);

        return context;
    }
}
