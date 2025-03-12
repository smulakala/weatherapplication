package com.apple.assignment.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;

@Slf4j
public class WebclientContextUtil {

    public static SslContext getSslContext(){
        SslContext sslContext = null;
        try{
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        }catch (SSLException sslException){
            log.error(sslException.getMessage());
        }
        return sslContext;
    }
}
