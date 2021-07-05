package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.response.BaseResponse;

import java.io.IOException;

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
     * @param msgReqDto
     * @return
     */
    BaseResponse sendChat(ChatMsgReqDto msgReqDto) ;
}
