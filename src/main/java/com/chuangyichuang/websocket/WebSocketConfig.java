package com.chuangyichuang.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by huzuxing on 16/12/28.
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter
        implements WebSocketConfigurer{

    @Autowired
    private ChuangChuangWebSocketHandler handler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(handler, "/chuangchuang")
                .setAllowedOrigins("*").addInterceptors(new HandShake());
        webSocketHandlerRegistry.addHandler(handler, "/chuangchuang/sockjs")
                .setAllowedOrigins("*").addInterceptors(new HandShake());
    }
}
