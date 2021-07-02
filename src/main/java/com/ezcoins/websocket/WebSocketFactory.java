//package com.ezcoins.websocket;
//
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.Session;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//
//@Component
//public class WebSocketFactory implements ApplicationRunner, Ordered {
//
//    /**
//     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
//     */
//    public volatile static int onlineCount = 0;
//
//    /**
//     * key: userType_userId  value: session对象
//     */
//    public static ConcurrentHashMap<String, Session> userSessionMap = new ConcurrentHashMap<>();
//
//    /**
//     * key: session id  value: userType_userId
//     */
//    public static ConcurrentHashMap<String, String> sessionUserMap = new ConcurrentHashMap<>();
//
//    /**
//     * key: userType_userId value: topic
//     */
//    public static ConcurrentHashMap<String, List<String>> userTopicMap = new ConcurrentHashMap<>();
//
//    /**
//     * 记录发送时间
//     * key: userType_userId value: 时间戳
//     */
//    public static ConcurrentHashMap<String, Long> sessionTimeMap = new ConcurrentHashMap<>();
//
//    @Override
//    public void run(ApplicationArguments args) {
//        //每隔5秒查看时间
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    List<String> keys = new ArrayList<>();
//                    for (Map.Entry<String, Long> entry : sessionTimeMap.entrySet()) {
//                        String key = entry.getKey();
//                        Long value = entry.getValue();
//                        long time = new Date().getTime();
//                        long space = (time - value) / 1000;
//                        if (space >= 60) {
//                            Session session = userSessionMap.get(key);
//                            if (session != null) {
//                                try {
//                                    session.close();
//                                    userSessionMap.remove(key);
//                                    sessionUserMap.remove(session.getId());
//                                    userTopicMap.remove(key);
//                                    keys.add(key);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                    for (String key : keys) {
//                        sessionTimeMap.remove(key);
//                    }
//
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//
//    //心跳
//    public static void heartbeat(Session session) {
//        String key = sessionUserMap.get(session.getId());
//        if (PingPayStringUtils.isNotEmpty(key)) {
//            sessionTimeMap.put(key, new Date().getTime());
//        }
//    }
//
//    //取消
//    public static void cancel(Session session) {
//        try {
//            String key = sessionUserMap.get(session.getId());
//            if (PingPayStringUtils.isNotEmpty(key)) {
//                session.close();
//                userSessionMap.remove(key);
//                sessionTimeMap.remove(key);
//                userTopicMap.remove(key);
//                sessionUserMap.remove(session.getId());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //订阅
//    public static void subscribe(WebSocketMessage webSocketMessage, Session session) {
//        try {
//            List<String> topicList = webSocketMessage.getTopicList();
//
//            JSONObject jsonObject = JSONObject.parseObject(webSocketMessage.getData());
//            String userId = jsonObject.getString("userId");
//            String userType = jsonObject.getString("userType");
//
//            Session session1 = userSessionMap.get(getKey(userId, userType));
//            if (session1 != null) {
//                sessionUserMap.remove(session1.getId());
//                sessionTimeMap.remove(getKey(userId, userType));
//            }
//            userSessionMap.put(getKey(userId, userType), session);
//            //取消订阅
//            userTopicMap.remove(getKey(userId, userType));
//            //重新订阅
//            userTopicMap.put(getKey(userId, userType), topicList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String getKey(String userId, String userType) {
//        return userType + "_" + userId;
//    }
//
//    //发送消息
//    public synchronized static void sendText(String userId, String userType, String message) {
//        Session session = userSessionMap.get(getKey(userId, userType));
//        if (session != null) {
//            if (session.isOpen()) {
//                session.getAsyncRemote().sendText(message);
//            }
//        }
//    }
//
//    //发送消息
//    public synchronized static void sendText(String key, String message) {
//        Session session = userSessionMap.get(key);
//        if (session != null) {
//            if (session.isOpen()) {
//                session.getAsyncRemote().sendText(message);
//            }
//        }
//    }
//
//    //发送消息
//    public synchronized static void sendText(Session session, String message) {
//        if (session != null) {
//            if (session.isOpen()) {
//                session.getAsyncRemote().sendText(message);
//            }
//        }
//    }
//}
