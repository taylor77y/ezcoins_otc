package com.ezcoins.project.otc.service.impl;

import com.ezcoins.config.EzCoinsConfig;
import com.ezcoins.config.ServerConfig;
import com.ezcoins.project.common.service.OssService;
import com.ezcoins.project.otc.entity.EzOtcChatMsg;
import com.ezcoins.project.otc.entity.req.ChatMsgReqDto;
import com.ezcoins.project.otc.mapper.EzOtcChatMsgMapper;
import com.ezcoins.project.otc.service.EzOtcChatMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.FileUploadUtils;
import com.ezcoins.utils.StringUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * otc聊天信息表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-19
 */
@Service
public class EzOtcChatMsgServiceImpl extends ServiceImpl<EzOtcChatMsgMapper, EzOtcChatMsg> implements EzOtcChatMsgService {

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 发送 聊天信息 文字/图片 内容类型(0:图片 1：文字)
     *
     * @param msgReqDto
     * @return
     */
    @Override
    public BaseResponse sendChat(ChatMsgReqDto msgReqDto,String sendId) {
        EzOtcChatMsg ezOtcChatMsg = new EzOtcChatMsg();
        BeanUtils.copyBeanProp(ezOtcChatMsg, msgReqDto);
        if (StringUtils.isNotEmpty(sendId)){
            ezOtcChatMsg.setSendUserId(sendId);
        }
        baseMapper.insert(ezOtcChatMsg);
        //给用户一个信号
        WebSocketHandle.toChatWith(msgReqDto.getReceiveUserId(), msgReqDto.getOrderMatchNo());
        return BaseResponse.success();
    }
}
