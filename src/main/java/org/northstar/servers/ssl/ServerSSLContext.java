package org.northstar.servers.ssl;


import javax.net.ssl.*;

public class ServerSSLContext {

    private ServerSSLContext(){}
    public static SSLContext get(){
        SSLContext context=null;
        try {
            context = create();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }
    public static SSLContext create() throws Exception {
        final SSLContext context;
        ServerKeyManager sniKeyManager = new ServerKeyManager();

        context = SSLContext.getInstance("TLS");
        context.init(new KeyManager[]{
                sniKeyManager
        }, null, null);

        return context;
    }
}
