package com.chuangyichuang.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zzy on 16/12/28.
 */
@Component
public class ChuangChuangWebSocketHandler implements WebSocketHandler {

    public static final Map<Long, WebSocketSession> userSocketSessionMap;

    static {
        userSocketSessionMap = new HashMap<Long, WebSocketSession>();
    }

    // 建立连接之后
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        System.out.println("已经建立连接");
        Long uid = (Long)webSocketSession.getAttributes().get("uid");
        if (null == userSocketSessionMap.get(uid))
            userSocketSessionMap.put(uid, webSocketSession);
    }
    // 消息处理,接受客户端发送过来的消息
    public void handleMessage(WebSocketSession webSocketSession,
                              WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage.getPayloadLength() == 0)
            return;
        Message msg = new Gson().fromJson(webSocketMessage.getPayload().toString(), Message.class);
        msg.setDate(new Date());
        sendMessageToUser(msg.getTo(),
                new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
    }

    //消息传输错误处理
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

        if (webSocketSession.isOpen())
            webSocketSession.close();
        Iterator<Map.Entry<Long, WebSocketSession>> it =
                userSocketSessionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(webSocketSession.getId())) {
                userSocketSessionMap.remove(webSocketSession.getId());
                System.out.println("Socket 会话已经移除:用户ID " + entry.getKey());
                break;
            }
        }
    }

    //关闭连接
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("WebSocket: " + webSocketSession.getId() + " 已经关闭");
        Iterator<Map.Entry<Long, WebSocketSession>> it =
                userSocketSessionMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, WebSocketSession> entry = it.next();
            if (entry.getValue().getId().equals(webSocketSession.getId())) {
                userSocketSessionMap.remove(webSocketSession.getId());
                System.out.println("Socket 会话已经移除:用户ID " + entry.getKey());
                break;
            }
        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    public void broadcast(final TextMessage message) {
        Iterator<Map.Entry<Long, WebSocketSession>> it = userSocketSessionMap
                .entrySet().iterator();

        // 多线程群发
        while (it.hasNext()) {

            final Map.Entry<Long, WebSocketSession> entry = it.next();

            if (entry.getValue().isOpen()) {
                // entry.getValue().sendMessage(message);
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            if (entry.getValue().isOpen()) {
                                entry.getValue().sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }

        }
    }

    // 单个用户发送消息
    public void sendMessageToUser(Long uid, TextMessage message) throws IOException {
        WebSocketSession session = userSocketSessionMap.get(uid);
        if (null != session && session.isOpen())
            session.sendMessage(message);
    }
}
