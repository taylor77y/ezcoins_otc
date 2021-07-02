//package com.ezcoins.websocket;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ezcoins.redis.RedisCache;
//import com.ezcoins.utils.SpringUtils;
//import com.ezcoins.constant.Constants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * @Author：JCccc
// * @Description：
// * @Date： created in 15:56 2019/5/13
// */
//@Component
//@ServerEndpoint("/connectWebSocket/{userId}")
//public class WebSocketService {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    /**
//     * 在线人数
//     */
//    public static int onlineNumber = 0;
//    /**
//     * 以OTC昵称为key，WebSocket为对象保存起来
//     */
//    private static ConcurrentHashMap<String, Session> userIdMap = new ConcurrentHashMap<String, Session>();
//    /**
//     * 会话
//     */
//    private Session session;
//    /**
//     * 用户名称
//     */
//    private String userId;
//
//    @Autowired
//    private static final RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
//
//    /**
//     * 连接建立成功调用的方法
//     */
//    @OnOpen
//    public void onOpen(@PathParam("userId") String userId,Session session) {
//        logger.info("现在来连接的客户id：" + session.getId() + "用户名：" + userId);
//        this.userId=userId;
//        this.session=session;
//        userIdMap.put(userId,session);
//        addOnlineCount();
//    }
//
//    public static synchronized int getOnlineCount() {
//        return onlineNumber;
//    }
//
//    public static synchronized void addOnlineCount() {
//        WebSocketService.onlineNumber++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        WebSocketService.onlineNumber--;
//    }
//    @OnError
//    public void onError(Session session, Throwable error) {
//        logger.info("服务端发生了错误" + error.getMessage());
//    }
//
//    /**
//     * 连接关闭
//     */
//    @OnClose
//    public void onClose() {
//        onlineNumber--;
//        userIdMap.remove(userId);
//    }
//
//    /**
//     * 收到客户端的消息
//     *
//     * @param message 消息
//     * @param session 会话
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        // 消息内容
//        JSONObject jsonObject = JSONObject.parseObject(message);
//        String type = jsonObject.getString(TYPE);
//        Map<String, String> map = new HashMap();
//        map.put("type", type);
//        map.put("status", OK);
//        if (type.equals(PING)) {
//            System.out.println("连接成功");
//        } else {
//            List<String> stringList = redisCache.getCacheObject(Constants.WEBSOCKET_USER_CACHE+userId);
//            if (stringList != null) {
//                stringList.removeIf(s -> s.equals(JSONObject.toJSONString(map)));
//                map.put("status",NO);
//                session.getAsyncRemote().sendText(new NotifyMessage(userId, map).toString());
//                redisCache.setCacheObject(Constants.WEBSOCKET_USER_CACHE+userId,stringList);
//            }
//
//        }
//    }
//
//    public void sendMessageTo(String message, String TouserId) throws IOException {
//        for (WebSocketService item : clients.values()) {
//            if (item.userId.equals(TouserId)) {
//                item.session.getAsyncRemote().sendText(message);
//                break;
//            }
//        }
//    }
//
//    public void sendMessageToTopic(NotifyMessage message) {
//
//        List<String> stringList = redisCache.getCacheObject(Constants.WEBSOCKET_USER_CACHE+message.getUserId());
//        if (null == stringList) {
//            stringList = new ArrayList<>();
//        }
//        boolean present = stringList.stream().anyMatch(m -> m.equals(message.toString()));
//        if (!present) {
//            WebSocketService socketService = clients.get(message.getUserId());
//            if (null != socketService) {
//                Session session = socketService.session;
//                if (session != null) {
//                    try {
//                        synchronized (session) {
//                            if (session.isOpen()) {
//                                session.getAsyncRemote().sendText(message.toString());
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("===消息发送出错", e);
//                    }
//                }
//            }
//        }
//        Map data = (Map) message.getData();
//        if (data.get("status").equals(OK)) {
//            stringList.add(message.toString());
//            stringList=stringList.stream().distinct().collect(Collectors.toList());
//        } else {
//            Map map = new HashMap(2);
//            map.put("type", data.get("type"));
//            map.put("status", OK);
//            stringList.removeIf(s -> s.equals(JSONObject.toJSONString(map)));
//        }
//        redisCache.setCacheObject(Constants.WEBSOCKET_USER_CACHE+message.getUserId(),stringList);
////        sessionTopics.put(message.getUserId(), stringList);
//    }
//
//    public void sendMessageAll(NotifyMessage message, String FromuserId) throws IOException {
//        for (WebSocketService item : clients.values()) {
//            item.session.getAsyncRemote().sendText(message.toString());
//        }
//    }
//
//
//}
//
