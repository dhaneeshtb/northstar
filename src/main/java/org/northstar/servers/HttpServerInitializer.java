package org.northstar.servers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SSLContext sslCtx;

    public HttpServerInitializer(SSLContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if(sslCtx!=null) {
            SSLEngine engine = sslCtx.createSSLEngine();
            engine.setUseClientMode(false);
            ch.pipeline().addLast(
                    new SslHandler(engine)
            );
        }
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpContentCompressor((CompressionOptions[]) null));
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpRequestDecoder()); // Decodes the ByteBuf into a HttpMessage and HttpContent (1)
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpServerHandler());
    }
}
