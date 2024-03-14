package org.northstar.servers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.northstar.servers.auth.CookieHandler;
import org.northstar.servers.auth.DefaultCookieHandler;
import org.northstar.servers.auth.UserStore;
import org.northstar.servers.exceptions.GenericServerProcessingException;
import org.northstar.servers.exceptions.SecurityException;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.jwt.JWTParser;
import org.northstar.servers.routing.*;
import org.northstar.servers.ssl.ServerSSLContext;
import org.northstar.servers.utils.TriParameterFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

public final class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);


    public static class HttpServerBuilder{
        private boolean isSSL;
        private int port;

        private int backLog=1024;
        private List<AbstractRoute> routes=new ArrayList<>();
        private JWTParser parser;

        private boolean enableLogging=false;

        UserStore userStore;
        private String loginLayer;
        private String domainName;
        private CookieHandler cookieHandler;


        private HttpServerBuilder(){
        }

        /**
         *
         * @return
         */
        public static HttpServerBuilder createBuilder(){
            return new HttpServerBuilder();
        }
        public  HttpServerBuilder withSSL(){
            this.isSSL=true;
            return this;
        }

        /**
         *
         * @param port
         * @return
         */
        public  HttpServerBuilder withPort(int port){
            this.port=port;
            return this;
        }

        /***
         *
         * @param route
         * @return
         */
        public  HttpServerBuilder withRoute(AbstractRoute route){
            this.routes.add(route);
            return this;
        }

        /***
         *
         * @param layerPattern
         * @param authNeeded
         * @param handler
         * @return
         */
        public  HttpServerBuilder withRoute(String layerPattern, boolean authNeeded, TriParameterFunction<HttpRequest,AuthRequest.AuthInfo,PatternExtractor.Match,RequestRoutingResponse> handler){
            this.routes.add(new AbstractRoute(layerPattern,authNeeded,handler) {
            });
            return this;
        }

        /***
         *
         * @param parser
         * @return
         */
        public HttpServerBuilder withJWTParser(JWTParser parser){
            this.parser=parser;
            return this;
        }


        public HttpServerBuilder withBacklog(int backLog){
            this.backLog=backLog;
            return this;
        }
        public HttpServerBuilder withRequestLogging(){
            enableLogging=true;
            return this;
        }
        public HttpServerBuilder withUserLoginConf(UserStore userStore){
            this.userStore=userStore;
            this.loginLayer="/login";
            return this;
        }

        public HttpServerBuilder withLoginLayer(String loginLayer){
            this.loginLayer=loginLayer;
            return this;
        }

        public HttpServerBuilder withDomain(String domainName){
            this.domainName=domainName;
            return this;
        }

        public HttpServerBuilder withCookieHandle(CookieHandler cookieHandler){
            this.cookieHandler=cookieHandler;
            return this;
        }



        /***
         *
         * @return
         */
        public HttpServer build(){
            HttpServer server=new HttpServer(port,isSSL);
            server.backLog=backLog;
            RequestRoutingContexts.setJwtParser(parser);
            if(userStore!=null){
                routes.add(new DefaultLoginRoute(loginLayer,userStore));
            }
            routes.forEach(RequestRoutingContexts::register);
            if(domainName!=null){
                RequestRoutingContexts.setServerDomain(domainName);
            }
            if(cookieHandler!=null){
                RequestRoutingContexts.setCookieHandler(cookieHandler);
            }else{
                RequestRoutingContexts.setCookieHandler(new DefaultCookieHandler("token"));
            }
            server.enableLogging=enableLogging;
            return server;
        }


    }



    private boolean isSSL;
    private int port;
    private int backLog=1024;

    private boolean enableLogging=false;

    private HttpServer(int port,boolean isSSL){
        this.isSSL=isSSL;
        this.port=port;
    }
    Channel channel;

    public void start()  {
            try {
                LOGGER.info("number of processors {}",Runtime.getRuntime().availableProcessors());
                final SSLContext sslCtx = (isSSL) ? ServerSSLContext.get() : null;
                EventLoopGroup bossGroup = new NioEventLoopGroup(5);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                    ServerBootstrap b = new ServerBootstrap();
                    b.option(ChannelOption.SO_BACKLOG, backLog);
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class);
                    if(enableLogging) {
                        b.handler(new LoggingHandler(LogLevel.INFO));
                    }
                    b.childHandler(new HttpServerInitializer(sslCtx));
                    channel = b.bind(port).sync().channel();
                    if(LOGGER.isInfoEnabled()) {
                        LOGGER.info("Open your web browser and navigate to {}://127.0.0.1:{}/",(isSSL ? "https" : "http"),port);
                    }
                    new Thread(()->{

                        try {
                            channel.closeFuture().sync();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        bossGroup.shutdownGracefully();
                            workerGroup.shutdownGracefully();

                    }).start();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                 LOGGER.error("error while starting server ",e);
            } catch (SecurityException e) {
                throw new GenericServerProcessingException(e);
            }
    }
    public void shutdown(){
        channel.close();
    }


}
