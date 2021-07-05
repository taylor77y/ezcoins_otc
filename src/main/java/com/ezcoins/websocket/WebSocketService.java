package com.ezcoins.websocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSONObject;
import com.ezcoins.websocket.NotifyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author：JCccc
 * @Description：
 * @Date： created in 15:56 2019/5/13
 */

@Component
@ServerEndpoint("/connectWebSocket/{userId}")
public class WebSocketService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的userId为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocketService> clients = new ConcurrentHashMap<String, WebSocketService>();
    /**
     * 会话
     */
    private Session session;
    /**
     * 用户名称
     */
    private String userId;
    /**
     * 用户订阅容器
     */
//    public static Map<String, List<String>> sessionTopics = new ConcurrentHashMap<>();
    public static final String TYPE = "type";

    public static final String PING = "ping";
    /**
     * 建立连接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        onlineNumber++;
        logger.info("现在来连接的客户id：" + session.getId() + "用户名：" + userId);
        this.userId = userId;
        this.session = session;
        clients.put(userId, this);
        logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误" + error.getMessage());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        onlineNumber--;
        clients.remove(userId);
        logger.info("有连接关闭！ 当前在线人数" + clients.size());
    }
    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 消息内容
        JSONObject jsonObject = JSONObject.parseObject(message);
        String type = jsonObject.getString(TYPE);
        if (type.equals(PING)) {
            System.out.println("连接成功");
        }
    }
    public void sendMessageTo(NotifyMessage notifyMessage)  {
        for (WebSocketService item : clients.values()) {
            if (item.userId.equals(notifyMessage.userId)) {
                item.session.getAsyncRemote().sendText(notifyMessage.toString());
                break;
            }
        }
    }
    public void sendMessageToTopic(NotifyMessage message) {
    }
    public void sendMessageAll(NotifyMessage message, String FromuserId) throws IOException {
        for (WebSocketService item : clients.values()) {
            item.session.getAsyncRemote().sendText(message.toString());
        }
    }
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }
}

