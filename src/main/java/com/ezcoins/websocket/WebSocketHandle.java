package com.ezcoins.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * websocket 处理器
 */
@Service
public class WebSocketHandle {

    @Autowired
    private WebSocketService webSocketService;

    public  void orderStatusChange(String userId,String orderNo,String status) {
        NotifyMessage notifyMessage=new NotifyMessage();
        notifyMessage.setUserId(userId);
        Map<String, String> map = new HashMap<>(3);
        map.put("topic", TopicSocket.ODERSTATUS);
        map.put("orderNo", orderNo);
        map.put("status", status);
        webSocketService.sendMessageTo(notifyMessage);
    }

    public void toChatWith(String userId){
        NotifyMessage notifyMessage=new NotifyMessage();
        notifyMessage.setUserId(userId);
        Map<String, String> map = new HashMap<>(1);
        map.put("topic", TopicSocket.TOCHATWITH);
        notifyMessage.setData(map);
        webSocketService.sendMessageTo(notifyMessage);
    }








}
