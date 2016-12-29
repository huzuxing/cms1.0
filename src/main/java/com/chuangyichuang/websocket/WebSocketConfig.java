package com.chuangyichuang.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * Created by huzuxing on 16/12/28.
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter
        implements WebSocketConfigurer{

//    @Resource
//    ChuangChuangWebSocketHandler handler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        System.out.println("handler adapter ...");
        webSocketHandlerRegistry.addHandler(new ChuangChuangWebSocketHandler(), "/ws").addInterceptors(new HandShake());
        webSocketHandlerRegistry.addHandler(new ChuangChuangWebSocketHandler(), "/ws/sockjs")
               .addInterceptors(new HandShake()).withSockJS();
    }
}
