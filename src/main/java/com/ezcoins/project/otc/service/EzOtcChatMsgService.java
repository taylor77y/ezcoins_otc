package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.project.otc.entity.resp.ChatMsgRespDto;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * otc聊天信息表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-19
 */
public interface EzOtcChatMsgService extends IService<EzOtcChatMsg> {
    /**
     * 发送 聊天信息 文字/图片
     * @param chatMsgList
     * @return
     */
    Response sendChat(List<EzOtcChatMsg> chatMsgList, String sendId);


    /**
     * 存入系统提示消息
     */
    void sendSysChat(List<EzOtcChatMsg> chatMsgList, String status);


    /**
     * 根据匹配订单id查询聊天记录
     * @param orderMatchNo
     * @return
     */
    ResponseList<ChatMsgRespDto> chatMsg(String orderMatchNo);
}
