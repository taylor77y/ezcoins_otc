package com.ezcoins.websocket;

import com.alibaba.fastjson.JSON;

import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.coin.CoinConstants.MainType;
import com.ezcoins.constant.enums.user.KycStatus;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;



/**
 * websocket 处理器
 */
public class WebSocketHandle {

    //订单状态变化
    public static void orderStatusChange(String userId, String status) {
        List<String> topicList = WebSocketFactory.userTopicMap.get(userId);
        if (topicList != null && topicList.size() > 0) {
            if (topicList.contains(TopicSocket.ODERSTATUS)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setTopic(TopicSocket.ODERSTATUS);
                sendMessage.setData(status);
                WebSocketFactory.sendText(userId, JSON.toJSONString(sendMessage));
            }
        }else {
            String message=String.format("【ezcoins】 %s。", "您的订单已发送变化，请前往查看");
            send(userId, message);
        }
    }

    //接受消息
    public static void toChatWith(String userId, String orderNo) {
        List<String> topicList = WebSocketFactory.userTopicMap.get(userId);
        if (topicList != null && topicList.size() > 0) {
            if (topicList.contains(TopicSocket.TOCHATWITH)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setTopic(TopicSocket.TOCHATWITH);
                sendMessage.setData(orderNo);
                WebSocketFactory.sendText(userId, JSON.toJSONString(sendMessage));
            }
        }
    }
    public static void price(HashMap hashMap){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setTopic(TopicSocket.PRICE);
        sendMessage.setData(hashMap);
        WebSocketFactory.sendMessageAll(JSON.toJSONString(sendMessage));
    }

    public static void nowOrder(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setTopic(TopicSocket.NEWORDER);
        WebSocketFactory.sendMessageAll(JSON.toJSONString(sendMessage));
    }



    //    //下一次重复性消费
//    public static void contractNextCyclePush(List<PingUser> userList) {
//        for (PingUser user : userList) {
//            //查询是否订阅了该频道
//            List<String> topicList = com.ping.api.websocket.WebSocketFactory.userTopicMap.get(getKey(user.getUserId(), LoginType.APP.getType()));
//            if (topicList != null && topicList.size() > 0) {
//                if (topicList.contains(TopicSocket.nextCycleConsume)) {
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setTopic(TopicSocket.nextCycleConsume);
//                    sendMessage.setData("0");
//                    com.ping.api.websocket.WebSocketFactory.sendText(user.getUserId(), LoginType.APP.getType(), JSON.toJSONString(sendMessage));
//                }
//            }
//        }
//    }
//
//    //下一次重复性消费
//    public static void contractNextCyclePush(ConcurrentHashMap<String, BigDecimal> userConsumeAmountMap) {
//        for (Map.Entry<String, BigDecimal> entry : userConsumeAmountMap.entrySet()) {
//            //查询是否订阅了该频道
//            String userId = entry.getKey();
//            List<String> topicList = com.ping.api.websocket.WebSocketFactory.userTopicMap.get(getKey(userId, LoginType.APP.getType()));
//            if (topicList != null && topicList.size() > 0) {
//                if (topicList.contains(TopicSocket.nextCycleConsume)) {
//                    BigDecimal value = entry.getValue();
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setTopic(TopicSocket.nextCycleConsume);
//                    sendMessage.setData(value.stripTrailingZeros().toPlainString());
//                    com.ping.api.websocket.WebSocketFactory.sendText(userId, LoginType.APP.getType(), JSON.toJSONString(sendMessage));
//                }
//            }
//        }
//    }
//
    //实名认证
    public static void realNameAuthentication(String userId, String status) {
        String message = null;
        if (status.equals(KycStatus.REFUSE.getCode())) {//拒绝
            message = String.format("【ezcoins】 %s。", "您的实名认证已拒绝，请重新提交实名认证信息");
        } else if (status.equals(KycStatus.BY.getCode())) {//通过
            message = String.format("【ezcoins】 %s。", "您的实名认证已通过");
        } else if (status.equals(KycStatus.PENDINGREVIEW.getCode())) {//待审核
            message = String.format("【ezcoins】 %s。", "您的实名认证已发起，请耐心等候");
        }
        if (StringUtils.isEmpty(message)) {
            return;
        }
        send(userId, message);
    }

    //高级认证
    public static void businessAuthentication(String userId, String status) {
        String message;
        if (status.equals(KycStatus.REFUSE.getCode())) {//拒绝
            message = String.format("【ezcoins】 %s。", "您的高级认证已拒绝，请重新提交高级认证信息");
        } else if (status.equals(KycStatus.BY.getCode())) {//通过
            message = String.format("【ezcoins】 %s。", "您的高级认证已通过");
        } else if (status.equals(KycStatus.PENDINGREVIEW.getCode())) {//待审核
            message = String.format("【ezcoins】 %s。", "您的高级认证已发起，请耐心等候");
        } else {
            return;
        }
        send(userId, message);
    }

    /**
     * 资产变化
     * @param userId
     * @param coinName
     * @param amount
     * @param sonType
     */
    public static void accountChange(String userId, String coinName, BigDecimal amount, String sonType) {
        String message;
        if (sonType.equals(RecordSonType.SYS_AIRPORT)) {//系统空投
            message = String.format("%s。", "系统空投"+amount+coinName+"，资产已到账");
        } else if (sonType.equals(RecordSonType.SYS_DEDUCTION)) {//通过
            message = String.format("%s。", "系统已变跟你的资产"+amount+coinName);
        } else if (sonType.equals(RecordSonType.TRANSFER_IN)) {//通过
            message = String.format("%s。", "你购买的"+amount+coinName+"已到账");
        }else if (sonType.equals(RecordSonType.TRANSFER_OUT)) {//通过
            message = String.format("%s。", "出售"+amount+coinName+"成功");
        }else if (sonType.equals(RecordSonType.ORDINARY_WITHDRAWAL)) {//通过
            message = String.format("%s。", "提币成功"+amount+coinName+"成功");
        }else if (sonType.equals(RecordSonType.ORDINARY_RECHARGE)) {//通过
            message = String.format("%s。", "冲币成功"+amount+coinName+"成功，资产已到账");
        }else if (sonType.equals(RecordSonType.HANDLING_FEE)) {//通过
            message = String.format("%s。", "发布订单成功，已扣除"+amount+coinName+"手续费");
        }else if (sonType.equals(RecordSonType.TRANSACTION_FREEZE)) {//交易冻结
            message = String.format("%s。", "交易进行中，已冻结"+amount+coinName);
        }else if (sonType.equals(RecordSonType.TRANSACTION_UNFREEZE)) {//交易解冻
            message = String.format("%s。", "交易完成，已解冻"+amount+coinName);
        }else {
            return;
        }
        send(userId, message);
    }

    private static void send(String userId, String message) {
        List<String> topicList = WebSocketFactory.userTopicMap.get(userId);
        if (topicList != null && topicList.size() > 0) {
            if (topicList.contains(TopicSocket.notification)) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setTopic(TopicSocket.notification);
                sendMessage.setData(message);
                WebSocketFactory.sendText(userId, JSON.toJSONString(sendMessage));
            }
        }
    }

//
//    //提币发起
//    public static void withdrawLaunch(String userId, String userType) {
//        boolean flag = getIsOpenNotice(userId, userType);
//
//        if (flag) {
//            String message = "【PingPay】 您的转账已发起，请耐心等候到账。";
//            List<String> topicList = com.ping.api.websocket.WebSocketFactory.userTopicMap.get(getKey(userId, userType));
//            if (topicList != null && topicList.size() > 0) {
//                if (topicList.contains(TopicSocket.notification)) {
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setTopic(TopicSocket.notification);
//                    sendMessage.setData(message);
//                    com.ping.api.websocket.WebSocketFactory.sendText(userId, userType, JSON.toJSONString(sendMessage));
//                }
//            }
//        }
//    }
//
//    //提币成功
//    public static void withdrawSuccess(String userId, String userType) {
//        boolean flag = getIsOpenNotice(userId, userType);
//
//        if (flag) {
//            String message = "【PingPay】 您的转账已到账，请查看。";
//            List<String> topicList = com.ping.api.websocket.WebSocketFactory.userTopicMap.get(getKey(userId, userType));
//            if (topicList != null && topicList.size() > 0) {
//                if (topicList.contains(TopicSocket.notification)) {
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setTopic(TopicSocket.notification);
//                    sendMessage.setData(message);
//                    com.ping.api.websocket.WebSocketFactory.sendText(userId, userType, JSON.toJSONString(sendMessage));
//                }
//            }
//        }
//    }
//
//    //充值已到账
//    public static void rechargeSuccess(String userId, String userType, BigDecimal amount) {
//        boolean flag = getIsOpenNotice(userId, userType);
//
//        if (flag) {
//            String message = String.format("【PingPay】 您充值的%s Register Point 已经到账。", amount.stripTrailingZeros().toPlainString());
//            List<String> topicList = com.ping.api.websocket.WebSocketFactory.userTopicMap.get(getKey(userId, userType));
//            if (topicList != null && topicList.size() > 0) {
//                if (topicList.contains(TopicSocket.notification)) {
//                    SendMessage sendMessage = new SendMessage();
//                    sendMessage.setTopic(TopicSocket.notification);
//                    sendMessage.setData(message);
//                    com.ping.api.websocket.WebSocketFactory.sendText(userId, userType, JSON.toJSONString(sendMessage));
//                }
//            }
//        }
//    }
//
//    //发送系统通知
//    public static void systemAll(String title, String message, String dateTime) {
//        for (Map.Entry<String, Session> entry : com.ping.api.websocket.WebSocketFactory.userSessionMap.entrySet()) {
//            SendMessage sendMessage = new SendMessage();
//            sendMessage.setTopic(TopicSocket.dialog);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("title", title);
//            jsonObject.put("message", message);
//            jsonObject.put("dataTime", dateTime);
//            sendMessage.setData(jsonObject);
//            com.ping.api.websocket.WebSocketFactory.sendText(entry.getValue(), JSON.toJSONString(sendMessage));
//        }
//    }
//
//    //发送系统通知
//    public static void systemUser(String userId, String userType, String title, String message, String dateTime) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setTopic(TopicSocket.dialog);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("title", title);
//        jsonObject.put("message", message);
//        jsonObject.put("dataTime", dateTime);
//        sendMessage.setData(jsonObject);
//        com.ping.api.websocket.WebSocketFactory.sendText(userId, userType, JSON.toJSONString(sendMessage));
//    }
//
//    //获取发送通知状态
//    public static boolean getIsOpenNotice(String userId, String userType) {
//        if (LoginType.APP.getType().equals(userType)) {
//            PingUserMapper userMapper = SpringUtils.getBean(PingUserMapper.class);
//            LambdaQueryWrapper<PingUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(PingUser::getUserId, userId);
//            PingUser user = userMapper.selectOne(lambdaQueryWrapper);
//            if (user.getIsOpenNotice().equals(UserConstant.OpenOrClose.ZERO)) {
//                return false;
//            }
//        }
//        return true;
//    }
}
